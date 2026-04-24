from functools import lru_cache

import torch
from PIL.Image import Image
from transformers import CLIPModel, CLIPProcessor

from loguru import logger

from app.core.config import settings


class CLIPEmbedder:
    def __init__(self):
        self.device = settings.device
        logger.info(f"CLIP 모델 로딩 시작: model={settings.clip_model_name}, device={self.device}")
        self.processor = CLIPProcessor.from_pretrained(settings.clip_model_name)
        self.model = CLIPModel.from_pretrained(settings.clip_model_name).to(self.device)
        self.model.eval()
        logger.info("CLIP 모델 로딩 완료")

    def embed(self, image: Image) -> list[float]:
        logger.debug("이미지 임베딩 시작")
        inputs = self.processor(images=image, return_tensors="pt").to(self.device)
        logger.debug(f"전처리 완료: shape={inputs['pixel_values'].shape}")
        with torch.no_grad():
            features = self.model.get_image_features(**inputs)
        logger.debug(f"특징 추출 완료: features shape={features.shape}")
        normalized = features / features.norm(dim=-1, keepdim=True)
        result = normalized[0].tolist()
        logger.info(f"임베딩 완료: dimension={len(result)}")
        return result

    def classify(self, image: Image, labels: list[str]) -> list[float]:
        logger.debug(f"이미지 분류 시작: labels={labels}")
        inputs = self.processor(text=labels, images=image, return_tensors="pt", padding=True).to(self.device)
        with torch.no_grad():
            outputs = self.model(**inputs)
            logits = outputs.logits_per_image  # shape: [1, len(labels)]
            probs = logits.softmax(dim=1)
        result = probs[0].tolist()
        logger.info(f"분류 완료: {dict(zip(labels, result))}")
        return result

    def classify_batch(self, images: list[Image], labels: list[str]) -> list[list[float]]:
        logger.debug(f"배치 이미지 분류 시작: count={len(images)}, labels={labels}")
        inputs = self.processor(
            text=labels,
            images=images,
            return_tensors="pt",
            padding=True
        ).to(self.device)
        with torch.no_grad():
            outputs = self.model(**inputs)
            probs = outputs.logits_per_image.softmax(dim=1)  # shape: [batch_size, len(labels)]
        result = probs.tolist()
        logger.info(f"배치 분류 완료: count={len(result)}")
        return result



@lru_cache(maxsize=1)
def get_clip_embedder() -> CLIPEmbedder:
    return CLIPEmbedder()
