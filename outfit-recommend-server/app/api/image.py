from loguru import logger
from fastapi import APIRouter, Depends, File, HTTPException, UploadFile

from app.model.clip_model import CLIPEmbedder, get_clip_embedder
from app.schema.image_schema import ImageUrlRequest, VectorResponse
from app.service.image_service import ImageService

router = APIRouter(prefix="/image", tags=["image"])

_stats = {
    "vectorize":      {"success": 0, "fail": 0},
    "vectorize_url":  {"success": 0, "fail": 0},
    "detect_clean":   {"success": 0, "fail": 0},
}

def _log_stats(key: str):
    s, f = _stats[key]["success"], _stats[key]["fail"]
    total = s + f
    success_pct = (s / total * 100) if total else 0
    fail_pct    = (f / total * 100) if total else 0
    logger.info(
        "[Stats][{}] 총={}건 | 성공={}건({:.1f}%) | 실패={}건({:.1f}%)",
        key, total, s, success_pct, f, fail_pct
    )


def get_image_service(embedder: CLIPEmbedder = Depends(get_clip_embedder)) -> ImageService:
    return ImageService(embedder)


@router.post("/vectorize", response_model=VectorResponse)
async def vectorize_image(
    file: UploadFile = File(...),
    service: ImageService = Depends(get_image_service),
):
    if not file.content_type or not file.content_type.startswith("image/"):
        _stats["vectorize"]["fail"] += 1
        _log_stats("vectorize")
        raise HTTPException(status_code=400, detail="이미지 파일만 업로드 가능합니다.")

    try:
        image_bytes = await file.read()
        vector = service.vectorize_bytes(image_bytes)
        _stats["vectorize"]["success"] += 1
        _log_stats("vectorize")
        return VectorResponse(vector=vector, dimension=len(vector))
    except Exception as e:
        logger.exception(f"[vectorize] 처리 실패: {e}")
        _stats["vectorize"]["fail"] += 1
        _log_stats("vectorize")
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/vectorize-url", response_model=VectorResponse)
async def vectorize_image_url(
    body: ImageUrlRequest,
    service: ImageService = Depends(get_image_service),
):
    try:
        vector = await service.vectorize_url(str(body.url))
        _stats["vectorize_url"]["success"] += 1
        _log_stats("vectorize_url")
        return VectorResponse(vector=vector, dimension=len(vector))
    except Exception as e:
        logger.exception(f"[vectorize_url] 처리 실패: {e}")
        _stats["vectorize_url"]["fail"] += 1
        _log_stats("vectorize_url")
        raise HTTPException(status_code=400, detail="이미지 URL을 처리할 수 없습니다.")


@router.post("/detect-clean-product")
async def detect_clean_product(
    body: ImageUrlRequest,
    service: ImageService = Depends(get_image_service),
):
    try:
        logger.info("상품 이미지 분류 시작!!!!")
        result = await service.detect_clean_product(str(body.url))
        _stats["detect_clean"]["success"] += 1
        _log_stats("detect_clean")
        return {"is_clean_product": result}
    except Exception as e:
        logger.exception(f"[detect_clean_product] 처리 실패: {e}")
        _stats["detect_clean"]["fail"] += 1
        _log_stats("detect_clean")
        raise HTTPException(status_code=400, detail="이미지 URL을 처리할 수 없습니다.")

@router.post("/detect_clean_product-batch")
async def detect_clean_product_batch(
    body: ImageUrlBatchRequest,
    service: ImageService = Depends(get_image_service),
):
    try:
        results = await service.detect_clean_product_batch([str(url) for url in body.urls])
        return {"results" : results}
    except Exception as e:
        logger.Exception("f"[detect_clean_product_batch] 처리 실패: {e}"")
        raise HTTPException(status_code=400, detail="배치 처리 실패")
