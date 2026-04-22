from contextlib import asynccontextmanager

from loguru import logger

from fastapi import FastAPI

from app.api.image import router as image_router
from app.model.clip_model import get_clip_embedder

logger.add(
    "logs/app.log",
    rotation="1 day",
    retention="7 days",
    level="DEBUG",
    format="{time:YYYY-MM-DD HH:mm:ss} [{level}] {name} - {message}",
)


@asynccontextmanager
async def lifespan(app: FastAPI):
    get_clip_embedder()
    yield


app = FastAPI(title="Outfit Recommend Server", lifespan=lifespan)

app.include_router(image_router, prefix="/api/v1")


@app.get("/health")
def health_check():
    return {"status": "ok"}



