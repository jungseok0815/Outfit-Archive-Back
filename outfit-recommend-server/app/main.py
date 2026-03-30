from fastapi import FastAPI

from app.api.image import router as image_router

app = FastAPI(title="Outfit Recommend Server")

app.include_router(image_router, prefix="/api/v1")


@app.get("/health")
def health_check():
    return {"status": "ok"}
