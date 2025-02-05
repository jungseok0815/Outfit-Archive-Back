package com.fasthub.backend.cmm.img;

public interface BaseImg<T> {
    void setImgNm(String imgNm);
    void setImgPath(String imgPath);
    void setImgOriginNm(String imgOriginNm);
    void setMappingEntity(T  mappingEntity);
}