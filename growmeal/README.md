# Growmeal

아기 이유식/식사 관리를 위한 풀스택 애플리케이션입니다. 부모가 함께 아기의 끼니를 기록하고, 냉장고 재료를 관리하며, 보유 재료 기반으로 레시피를 추천받을 수 있습니다.

## 주요 기능

- **인증**: 회원가입, 로그인, JWT 기반 토큰 관리
- **아기 프로필**: 아기 정보 및 알레르기 관리
- **냉장고 관리**: 냉장고 모델별 칸(compartment) 시스템으로 재료/반찬 위치 관리
- **인벤토리**: 반찬(MEAL) 및 재료(INGREDIENT) 관리
- **레시피**: 보유 재료 기반 레시피 추천, 영양소/알레르기 자동 계산
- **끼니 기록**: 아침/점심/저녁/간식 기록, 반응(GOOD/NEUTRAL/BAD) 추적

## 기술 스택

### Backend
- Java 21
- Spring Boot 4.0.2
- Spring Data JPA
- Spring Security + JWT (jjwt)
- Redis (토큰 관리)
- H2 Database (개발용)

### Frontend
- React 18
- TypeScript
- Vite
- React Router

## 프로젝트 구조

```
growmeal/
├── api_spec.md          # API 명세서
├── erd.md               # ERD 문서 (Mermaid)
├── backend/             # Spring Boot 백엔드
│   ├── src/
│   ├── build.gradle
│   └── gradlew
└── frontend/            # React 프론트엔드
    ├── src/
    ├── package.json
    └── vite.config.ts
```

## 시작하기

### Backend

```bash
cd backend
./gradlew bootRun
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## API 문서

자세한 API 명세는 [api_spec.md](./api_spec.md)를 참조하세요.

## ERD

데이터베이스 구조는 [erd.md](./erd.md)를 참조하세요.
