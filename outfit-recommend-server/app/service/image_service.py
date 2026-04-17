import io

import httpx
from PIL import Image

from app.model.clip_model import CLIPEmbedder


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
        image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        return self.embedder.embed(image)

    async def vectorize_url(self, url: str) -> list[float]:
        async with httpx.AsyncClient(timeout=10.0) as client:
            response = await client.get(url)
            response.raise_for_status()
        return self.vectorize_bytes(response.content)
