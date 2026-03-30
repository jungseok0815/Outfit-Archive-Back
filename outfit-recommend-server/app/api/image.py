from fastapi import APIRouter, Depends, File, HTTPException, UploadFile

from app.model.clip_model import CLIPEmbedder, get_clip_embedder
from app.schema.image_schema import ImageUrlRequest, VectorResponse
from app.service.image_service import ImageService

router = APIRouter(prefix="/image", tags=["image"])


def get_image_service(embedder: CLIPEmbedder = Depends(get_clip_embedder)) -> ImageService:
    return ImageService(embedder)


@router.post("/vectorize", response_model=VectorResponse)
async def vectorize_image(
    file: UploadFile = File(...),
    service: ImageService = Depends(get_image_service),
):
    if not file.content_type or not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="이미지 파일만 업로드 가능합니다.")

    image_bytes = await file.read()
    vector = service.vectorize_bytes(image_bytes)
    return VectorResponse(vector=vector, dimension=len(vector))


@router.post("/vectorize-url", response_model=VectorResponse)
async def vectorize_image_url(
    body: ImageUrlRequest,
    service: ImageService = Depends(get_image_service),
):
    try:
        vector = await service.vectorize_url(str(body.url))
    except Exception:
        raise HTTPException(status_code=400, detail="이미지 URL을 처리할 수 없습니다.")
    return VectorResponse(vector=vector, dimension=len(vector))
