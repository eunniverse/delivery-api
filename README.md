# Delivery API

Spring Boot 기반의 배달 주문 및 주소 변경 기능을 제공하는 RESTful API입니다.  
JWT 기반 인증, Swagger 문서화, Flyway 마이그레이션 및 Kakao API 연동을 포함하고 있습니다.

---

## 주요 기능

- 회원가입 및 로그인 (JWT 발급)
- 배달 조회 (기간, 상태 필터링, 페이징 지원)
- 배달 주소 변경 (단, 상태가 `PENDING`일 때만 가능)
- JWT 기반 인증 및 예외 처리
- Kakao API를 통한 주소 좌표 변환 및 거리 계산

---

## 기술 스택

- Java 17, Spring Boot 3.2.4
- Spring Web / JPA / Spring Security / Validation
- H2 / MySQL / Flyway
- JWT (jjwt)
- Swagger (springdoc-openapi)
- Kakao Local & Mobility API
- Gradle / JUnit5 / MockMvc

---

## 샘플 데이터

초기 테스트를 위한 임시 데이터가 포함되어 있습니다.

- 위치: `src/main/resources/db/migration/V3__insert_sample_data.sql`
- 기본 사용자 ID: `test`, 비밀번호: `passwo@rd12345` (BCrypt 적용)
- 배송 데이터 20건 자동 삽입  
  (상태값 다양하게 포함: `PENDING`, `ONGOING`, `COMPLETE`, `CANCELLED`)

> 운영 환경에서는 샘플 데이터 제거가 필요합니다.

---

## 외부 API 연동 (Kakao)

카카오 주소 검색 및 경로 계산 API를 사용합니다.
API 호출을 위해 아래와 같이 API 키 등록이 필요합니다. 그러나 프로젝트 실행 편의성을 위해 카카오 API Key를 등록하여 커밋하였습니다.

`API 키가 없으면 주소 변경 시 오류(403 등)가 발생할 수 있습니다.`
### 1. `application.yml` 예시

```yaml
kakao:
  api-key: YOUR_KAKAO_REST_API_KEY
```

---

## 데이터베이스 환경
이 프로젝트는 H2와 MySQL 두 가지 데이터베이스 환경을 함께 지원합니다.

### 2가지 DB를 사용한 이유
* 프로젝트를 실행할 때 별도로 DB를 설치하거나 계정을 만들지 않아도 되도록 하기 위해 내장형 H2 DB를 도입했습니다. 덕분에 clone 후 바로 실행하고 테스트할 수 있어 개발 속도와 접근성이 향상됩니다.
* 운영 환경에서는 MySQL과 Flyway를 사용하여 정형화된 DB 마이그레이션과 구조 통제를 병행합니다.

| 환경    | 용도              | 설명                                                              |
| ----- | --------------- | --------------------------------------------------------------- |
| H2    | 개발 편의성 및 빠른 테스트 | 별도의 DB 설치나 설정 없이도 즉시 실행 가능. 프로젝트 초기 개발 시 반복적인 쿼리 작업 없이 빠르게 확인 가능 |
| MySQL | 운영 환경           | 실제 배포 및 운영을 위한 데이터베이스로 Flyway를 통해 마이그레이션 관리됨                    |

