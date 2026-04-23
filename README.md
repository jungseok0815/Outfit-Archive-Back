# Outfit Archive Back

패션 의상 아카이브 및 AI 기반 의상 추천 서비스의 백엔드 레포지토리입니다.

---

## 프로젝트 구조

```
Outfit-Archive-Back/
├── src/                        # Spring Boot 메인 서버
├── outfit-recommend-server/    # FastAPI 의상 추천 서버 (Python)
├── Dockerfile                  # Spring Boot 이미지 빌드
├── docker-compose.yml          # 로컬 실행 구성
├── build.gradle
└── .github/workflows/deploy.yml
```

---

## 서버 구성

| 서버 | 기술 스택 | 포트 |
|------|-----------|------|
| 메인 API 서버 | Spring Boot 3.4.1 / Java 17 | 8080 |
| 의상 추천 서버 | FastAPI / Python 3.13 | 8000 |

---

## 메인 서버 (Spring Boot)

### 기술 스택

| 분류 | 사용 기술 |
|------|-----------|
| 언어 / 프레임워크 | Java 17, Spring Boot 3.4.1 |
| 데이터베이스 | MySQL 8 (AWS RDS), Redis 7 |
| ORM | Spring Data JPA (Hibernate) |
| 인증 | Spring Security + JWT |
| 파일 저장 | AWS S3 |
| 결제 | Toss Payments |
| 외부 API | Naver API |
| 이메일 | JavaMailSender (Gmail SMTP) |
| 이미지 | Thumbnailator (리사이징) |
| 엑셀 파싱 | Apache POI |
| 분산 락 | Redisson |
| 설정 암호화 | Jasypt |
| 매핑 | MapStruct, ModelMapper |

### JWT 설정

| 토큰 | 만료 시간 |
|------|-----------|
| Access Token | 30분 (1,800,000ms) |
| Refresh Token | 3시간 (10,800,000ms) |

### 도메인 구조

```
src/main/java/com/fasthub/backend/
│
├── admin/                  # 관리자 도메인
│   ├── auth/               # 관리자 인증
│   ├── banner/             # 배너 관리
│   ├── brand/              # 브랜드 관리
│   ├── category/           # 카테고리 관리
│   ├── coupon/             # 쿠폰 관리
│   ├── keyword/            # 키워드 수집
│   ├── order/              # 주문 관리
│   ├── product/            # 상품 관리
│   ├── revenue/            # 매출 통계
│   └── review/             # 리뷰 관리
│
├── user/                   # 사용자 도메인
│   ├── usr/                # 회원 (가입/로그인)
│   ├── address/            # 배송지
│   ├── banner/             # 배너 조회
│   ├── brand/              # 브랜드 팔로우
│   ├── coupon/             # 쿠폰
│   ├── follow/             # 팔로우
│   ├── notification/       # 알림
│   ├── order/              # 주문
│   ├── payment/            # 결제 (Toss Payments)
│   ├── point/              # 포인트
│   ├── post/               # 코디 게시물 (아카이브)
│   ├── product/            # 상품 조회
│   ├── productview/        # 상품 조회 기록
│   ├── recommend/          # 의상 추천
│   ├── review/             # 리뷰
│   ├── similar/            # 유사 상품
│   └── wishlist/           # 위시리스트
│
└── cmm/                    # 공통 모듈
    ├── config/             # 설정
    ├── enums/              # 열거형
    ├── error/              # 에러 핸들링
    ├── img/                # 이미지 처리
    ├── jwt/                # JWT 유틸
    ├── naver/              # Naver API 연동
    ├── paging/             # 페이징
    └── result/             # 공통 응답 포맷
```

---

## 의상 추천 서버 (FastAPI)

CLIP(Contrastive Language-Image Pretraining) 모델을 활용해 이미지 임베딩을 생성하고 의상 유사도를 계산합니다.

### 기술 스택

| 분류 | 사용 기술 |
|------|-----------|
| 언어 / 프레임워크 | Python 3.13, FastAPI 0.135 |
| AI 모델 | OpenAI CLIP (HuggingFace transformers) |
| 딥러닝 | PyTorch (CPU) |
| HTTP 클라이언트 | httpx |
| 로깅 | loguru |

### API 엔드포인트

| 메서드 | 경로 | 설명 |
|--------|------|------|
| `GET` | `/health` | 헬스 체크 |
| `POST` | `/api/v1/image/vectorize` | 이미지 파일 → 벡터 변환 |
| `POST` | `/api/v1/image/vectorize-url` | 이미지 URL → 벡터 변환 |
| `POST` | `/api/v1/image/detect-clean-product` | 단독 상품 이미지 여부 판별 |
| `POST` | `/api/v1/image/detect-clean-product-batch` | 단독 상품 이미지 배치 판별 |

### 이미지 분류 레이블 (CLIP Zero-shot)

단독 상품 이미지 판별 시 아래 레이블로 Zero-shot 분류를 수행하며, 단독 상품 확률이 **0.8 이상**이면 단독 상품으로 판정합니다.

- `a single product photo on clean background` — 단독 상품
- `a person wearing clothes` — 사람 착용
- `multiple color variants of the same product` — 다색상 콜라주
- `clothes on a mannequin` — 마네킹
- `flat lay clothing on floor` — 플랫레이

---

## 인프라

### 아키텍처

```
[Client]
   │
   ├──▶ [Spring Boot :8080]  ──▶ [MySQL (AWS RDS)]
   │          │               ──▶ [Redis]
   │          │               ──▶ [AWS S3]
   │          │
   │          └──▶ [FastAPI :8000]  (CLIP 이미지 임베딩)
```

### AWS 리소스

| 리소스 | 상세 |
|--------|------|
| RDS | MySQL 8, `ap-northeast-2` (서울) |
| S3 | `jungseok-outfit-s3`, `ap-northeast-2` |
| EC2 | Docker 컨테이너로 서비스 운영 |

---

## 실행 방법

### 사전 요구 사항

- Docker, Docker Compose 설치
- `JASYPT_ENCRYPTOR_PASSWORD` 환경변수 설정

### 로컬 실행

```bash
# .env 파일에 환경변수 설정
echo "JASYPT_ENCRYPTOR_PASSWORD=your_password" > .env

# 전체 서비스 실행 (Redis + Spring Boot + FastAPI)
docker-compose up -d
```

### 서비스별 포트

| 서비스 | 포트 |
|--------|------|
| Spring Boot API | http://localhost:8080 |
| FastAPI 추천 서버 | http://localhost:8000 |
| Redis | localhost:6379 |

---

## CI/CD

`main` 브랜치에 push 시 GitHub Actions가 자동으로 실행됩니다.

### 배포 파이프라인

![CI/CD Architecture](docs/cicd-architecture.png)

### 필요한 GitHub Secrets

| 시크릿 | 설명 |
|--------|------|
| `JASYPT_PASSWORD` | Jasypt 암호화 키 |
| `EC2_HOST` | EC2 인스턴스 IP |
| `EC2_USER` | EC2 SSH 사용자명 |
| `EC2_KEY` | EC2 SSH 개인키 |
| `GHCR_TOKEN` | GitHub Container Registry 토큰 |

---

## 환경 변수

| 변수 | 설명 |
|------|------|
| `SPRING_PROFILES_ACTIVE` | Spring 프로파일 (`local` / `prod`) |
| `JASYPT_ENCRYPTOR_PASSWORD` | 설정 값 복호화 키 |
| `SPRING_DATA_REDIS_HOST` | Redis 호스트 |
| `CLIP_SERVER_URL` | FastAPI 추천 서버 URL (기본값: `http://localhost:8000`) |
| `MAIL_USERNAME` | Gmail 발송 계정 |
| `MAIL_PASSWORD` | Gmail 앱 비밀번호 |
