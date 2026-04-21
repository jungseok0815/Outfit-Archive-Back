from loguru import logger

from fastapi import FastAPI

from app.api.image import router as image_router

logger.add(
    "logs/app.log",
    rotation="1 day",
    retention="7 days",
    level="DEBUG",
    format="{time:YYYY-MM-DD HH:mm:ss} [{level}] {name} - {message}",
)

app = FastAPI(title="Outfit Recommend Server")

app.include_router(image_router, prefix="/api/v1")


@app.get("/health")
def health_check():
    return {"status": "ok"}



