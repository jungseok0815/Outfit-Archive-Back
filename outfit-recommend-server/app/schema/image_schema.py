from pydantic import BaseModel, HttpUrl


class VectorResponse(BaseModel):
    vector: list[float]
    dimension: int


class ImageUrlRequest(BaseModel):
    url: HttpUrl

class ImageUrlBatchRequest(BaseModel)
    url: list[str]
