# AGENTS.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## About

Growmeal is a full-stack baby food management app (Korean). Parents track meals, manage refrigerator inventory, and get recipe suggestions based on available ingredients.

- **Backend**: Java 21, Spring Boot 3.5.11, Spring Data JPA, Spring Security + JWT (jjwt), Redis (token/verification management), PostgreSQL (local dev), H2 (test only)
- **Frontend**: React 18, TypeScript, Vite, React Router v7

## Commands

### Backend (run from `backend/`)

```bash
# Run the application
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "kr.co.growmeal.auth.AuthAcceptanceTest"

# Run a single test method
./gradlew test --tests "kr.co.growmeal.auth.AuthAcceptanceTest.회원가입_후_로그인_성공"
```

### Frontend (run from `frontend/`)

```bash
npm install
npm run dev      # dev server
npm run build    # type-check + Vite build
npm run lint     # ESLint
```

## Backend Architecture

### Package structure

All code lives under `kr.co.growmeal`. Each domain module follows a strict three-layer pattern:

```
{domain}/
  ui/              # Controllers + request/response DTOs + validators
  application/     # Service classes (business logic)
  domain/          # JPA entities, Repository interfaces, domain exceptions
```

Current modules: `auth`, `baby`, `refrigerator`, `common` (GlobalExceptionHandler).

### Key design decisions

**No FK constraints**: Entity relationships use only ID references. All `@ManyToOne`/`@OneToMany` use `@ForeignKey(ConstraintMode.NO_CONSTRAINT)`.

**Local DB migration**: When entity fields are added/changed/deleted, run the corresponding `ALTER TABLE` / `CREATE TABLE` SQL directly on the local PostgreSQL (`growmeal` DB). Test environment (H2) uses `ddl-auto=create-drop`, so no manual migration is needed there.

**JWT + Redis token flow**:
- Access token: 15 min. Sent as `Authorization: Bearer <token>` on every request.
- Refresh token: 7 days. Stored in Redis; invalidated on logout.
- Phone verification codes also stored in Redis under key `phone:verification:{phoneNumber}` (3-minute TTL).

**Security**: Stateless (no session). `JwtAuthenticationFilter` runs before `UsernamePasswordAuthenticationFilter`. Public routes: `/auth/**` (except `/auth/me`), `/refrigerator-models/**`, `/h2-console/**`.

**Error handling**: `GlobalExceptionHandler` (`@RestControllerAdvice`) maps domain exceptions to HTTP status codes. All handlers currently return `ResponseEntity<Void>` (empty body, no error JSON). The `api_spec.md` describes the intended `{success, data, error}` response format, but the actual error responses are body-less.

**RefrigeratorModel compartments**: The compartment layout is stored as a JSON column on `REFRIGERATOR_MODEL`. `INVENTORY_ITEM.compartmentId` stores the compartment's `id` string from that JSON. Model data is pre-seeded via `RefrigeratorModelDataInitializer` on startup.

### Acceptance tests

All backend tests are acceptance tests using REST-Assured against a full Spring context (`@SpringBootTest(webEnvironment = RANDOM_PORT)`). Embedded Redis (`EmbeddedRedisConfig`) is started statically and injected via `@DynamicPropertySource`. There are no unit tests—only acceptance tests.

Each test class must:
1. `@Import(EmbeddedRedisConfig.class)`
2. Declare `@DynamicPropertySource` to bind `spring.data.redis.port` to `EmbeddedRedisConfig::getRedisPort`
3. Set `RestAssured.port = port` in `@BeforeEach`

Phone verification codes can be read directly from Redis in tests via `redisTemplate.opsForValue().get("phone:verification:" + phoneNumber)`.

## TDD Workflow (backend)

For every new API endpoint, follow this strict order:

1. **Write acceptance test first** — happy-path only, with `// given`, `// when`, `// then` comments. Create stub implementations to make it compile and fail.
2. **Implement** — wire up Controller → Service → Repository.
3. **Refine tests** — clean up stubs, ensure tests pass.

## Reference Documents

- `api_spec.md` — full REST API specification (request/response shapes, error codes)
- `erd.md` — database schema as a Mermaid ERD

## Git Commit Rules

- Commit per feature change (new endpoint, spec change, meaningful improvement). Skip pure refactors.
- Commit messages: max 3 lines (1-line title + up to 2-line body).
