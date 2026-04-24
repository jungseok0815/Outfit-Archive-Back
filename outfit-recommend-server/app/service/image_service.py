import asyncio
import io

import httpx
from loguru import logger
from PIL import Image

from app.model.clip_model import CLIPEmbedder

_HTTP_HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
}


class ImageService:
    labels = [
          "a single product photo on clean background",   # 단독 상품
          "a person wearing clothes",                      # 사람 착용
          "multiple color variants of the same product",  # 다색상 콜라주
          "clothes on a mannequin",                        # 마네킹
          "flat lay clothing on floor",                    # 플랫레이
      ]

    def __init__(self, embedder: CLIPEmbedder):
        self.embedder = embedder

    def vectorize_bytes(self, image_bytes: bytes) -> list[float]:
        logger.debug(f"bytes 이미지 벡터화 시작: size={len(image_bytes)} bytes")
        image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        logger.debug(f"이미지 열기 완료: image size={image.size}")
        result = self.embedder.embed(image)
        logger.info("bytes 이미지 벡터화 완료")
        return result

    async def vectorize_url(self, url: str) -> list[float]:
        logger.info(f"URL 이미지 벡터화 시작: url={url}")
        async with httpx.AsyncClient(timeout=10.0, headers=_HTTP_HEADERS) as client:
            response = await client.get(url)
            response.raise_for_status()
        logger.debug(f"이미지 다운로드 완료: content_length={len(response.content)} bytes")
        result = self.vectorize_bytes(response.content)
        logger.info("URL 이미지 벡터화 완료")
        return result

    async def detect_clean_product(self, url: str) -> bool:
        logger.info(f"단독 상품 이미지 판별 시작: url={url}")
        async with httpx.AsyncClient(timeout=10.0, headers=_HTTP_HEADERS) as client:
            response = await client.get(url)
            response.raise_for_status()
        image = Image.open(io.BytesIO(response.content)).convert("RGB")
        probs = self.embedder.classify(image, self.labels)
        clean_product_prob = probs[0]
        logger.info(f"판별 완료: 단독 상품 확률={clean_product_prob:.2f}")
        return clean_product_prob >= 0.8

    async def detect_clean_product_batch(self, urls: list[str]) -> dict[str, bool]:
        # 1. 병렬 다운로드
        async with httpx.AsyncClient(timeout=10.0, headers=_HTTP_HEADERS) as client:
            responses = await asyncio.gather(
                *[client.get(url) for url in urls],
                return_exceptions=True
            )

        # 2. 실패 필터링
        valid_urls = []
        valid_images = []
        for url, response in zip(urls, responses):
            if isinstance(response, Exception):
                logger.error(f"[detect_clean_product_batch] 다운로드 실패: url={url}")
                continue
            valid_urls.append(url)
            valid_images.append(Image.open(io.BytesIO(response.content)).convert("RGB"))

        if not valid_images:
            return {}

        # 3. 배치 추론
        probs_list = self.embedder.classify_batch(valid_images, self.labels)

        # 4. 결과 매핑
        return {
            url: probs[0] >= 0.8
            for url, probs in zip(valid_urls, probs_list)
        }
