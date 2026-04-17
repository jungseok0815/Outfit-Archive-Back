from functools import lru_cache

import torch
from PIL.Image import Image
from transformers import CLIPModel, CLIPProcessor

from app.core.config import settings


class CLIPEmbedder:
    def __init__(self):
        self.device = settings.device
        self.processor = CLIPProcessor.from_pretrained(settings.clip_model_name)
        self.model = CLIPModel.from_pretrained(settings.clip_model_name).to(self.device)
        self.model.eval()


    def embed(self, image: Image) -> list[float]:
        inputs = self.processor(images=image, return_tensors="pt").to(self.device)
        with torch.no_grad():
            features = self.model.get_image_features(**inputs)
        normalized = features / features.norm(dim=-1, keepdim=True)
        return normalized[0].tolist()

    def classify(self, image: Image, labels: list[str]) -> list[float]
        inputs = self.processor(text=labels, images=image, return_tensors="pt", padding=True).to(self.device)
        with torch.no_grad():



@lru_cache(maxsize=1)
def get_clip_embedder() -> CLIPEmbedder:
    return CLIPEmbedder()
