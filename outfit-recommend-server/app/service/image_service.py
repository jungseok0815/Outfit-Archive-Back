import io

import httpx
from PIL import Image

from app.model.clip_model import CLIPEmbedder


class ImageService:
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
