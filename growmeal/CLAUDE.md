# Growmeal AI Rules

@README.md
@api_spec.md
@erd.md

## 기본 지침 (Basic Guidelines)

- 기능 구현 시 항상 프로젝트 루트 디렉토리에 있는 `api_spec.md`와 `erd.md` 파일을 참조하여 요구사항과 데이터베이스 모델을 정확히 파악하세요.

## 개발 워크플로우 (Development Workflow)

> **적용 범위**: 이 섹션의 규칙들은 `backend/` 디렉토리 작업에만 해당됩니다.

### 데이터베이스 규칙

- **FK constraint 미사용**: 엔티티 간 관계 설정 시 id 값으로 참조하되, FK constraint는 걸지 않습니다. (JPA `@ManyToOne`, `@OneToMany` 등에서 `@JoinColumn`의 `foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)` 사용)
- **로컬 DB 마이그레이션**: 엔티티 필드 추가/변경/삭제 시, 로컬 PostgreSQL(`growmeal` DB)에 대응하는 `ALTER TABLE` / `CREATE TABLE` 문을 직접 실행합니다. 테스트 환경(H2)은 `ddl-auto=create-drop`이므로 별도 작업 불필요합니다.

### TDD 규칙

새로운 API 엔드포인트를 하나 구현할 때마다, 반드시 다음 3단계의 순서를 엄격하게 지켜서 작업하세요:

1. **인수 테스트 먼저 작성 (Write Acceptance Test)**
   - 해당 API 엔드포인트에 대한 해피 케이스(정상 작동 흐름) 인수 테스트 코드를 가장 먼저 작성합니다.
   - 테스트별로 API 엔드포인트 성격에 맞게 적절한 given, when, then 주석을 달아줍니다.
   - 더미 함수들을 만들어 실패하는 테스트를 만듭니다.
2. **실제 로직 구현 (Actual Implementation)**
   - 실제 비즈니스 로직과 데이터베이스 연동(Repository, Service 등) 코드를 구현합니다.
3. **테스트 코드 정돈 (Refactor Tests)**
   - 실제 코드 구현에 맞춰 기존에 작성한 테스트 코드가 정상적으로 돌아가도록 다듬고 정돈합니다.

## Git 커밋 규칙 (Commit Rules)

- **커밋 단위**: 기능이 수정될 때마다 커밋 (예: 엔드포인트 추가, 스펙 변경 등)
- **커밋 제외**: 단순 내부 개선사항(리팩토링, 코드 정리 등)은 커밋하지 않음. 단, 성능 개선 등 유의미한 개선은 커밋
- **메시지 길이 제한**: 커밋 메시지는 **최대 3줄**을 절대 넘지 않게 간결하고 명확하게 작성하세요 (제목 1줄, 본문 1~2줄 이내).
