package com.fasthub.backend.cmm.img;

// S3 전환 후 이 컨트롤러는 더 이상 사용되지 않습니다.
//
// 기존 역할: GET /api/img/get?imgNm=파일명 → 서버 로컬 디스크에서 파일을 읽어 반환
// 변경 후  : imgPath 컬럼에 S3 URL이 저장되므로 클라이언트가 해당 URL로 직접 접근
//
// 클라이언트 변경 사항:
//   기존: GET /api/img/get?imgNm={imgNm}
//   변경: imgPath 값(https://버킷.s3.리전.amazonaws.com/uuid_파일명)을 img src로 직접 사용
