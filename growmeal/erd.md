# GrowMeal ERD

```mermaid
erDiagram
	direction TB
	USER {
		long id PK ""
		string email UK ""
		string password  ""
		string name  ""
		string role  "MOM|DAD|GRANDMA|GRANDPA|OTHER"
		datetime createdAt  ""
		datetime updatedAt  ""
	}

	BABY {
		long id PK ""
		string name  ""
		date birthDate  ""
		datetime createdAt  ""
		datetime updatedAt  ""
	}

	BABY_ALLERGY {
		long id PK ""
		long babyId  ""
		string allergy  ""
	}

	USER_BABY {
		long userId PK ""
		long babyId PK ""
	}

	REFRIGERATOR_MODEL {
		long id PK ""
		string model UK "e.g. SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR"
		string name  ""
		string imageUrl  ""
		json compartments  "[{id, name}, ...]"
	}

	REFRIGERATOR {
		long id PK ""
		long refrigeratorModelId  ""
		string nickname  ""
		long userId  ""
		datetime createdAt  ""
		datetime updatedAt  ""
	}

	INGREDIENT_MASTER {
		long id PK ""
		string name UK ""
		string category  "PROTEIN|VEGETABLE|GRAIN|DAIRY|MEAT|FISH|FRUIT|ETC"
		string mainNutrient  "PROTEIN|CALCIUM|IRON|VITAMIN_A|..."
		string extraNutrient  "nullable"
		string description  ""
	}

	INGREDIENT_MASTER_ALLERGY {
		long id PK ""
		long ingredientMasterId  ""
		string allergyInfo  "e.g. 밀, 대두"
	}

	INVENTORY_ITEM {
		long id PK ""
		string name  ""
		string type  "MEAL|INGREDIENT"
		long refrigeratorId  ""
		string compartmentId  "JSON 내 id 참조"
		date expiresAt  ""
		datetime createdAt  ""
	}

	INVENTORY_ITEM_INGREDIENT {
		long id PK ""
		long inventoryItemId  ""
		long ingredientMasterId  "nullable, 마스터 재료일 시"
		string name  "nullable, 마스터에 없는 재료일 시"
	}

	RECIPE {
		long id PK ""
		string name  ""
		string difficulty  "EASY|MEDIUM|HARD"
		datetime createdAt  ""
		datetime updatedAt  ""
	}

	RECIPE_STEP {
		long id PK ""
		long recipeId  ""
		int step  ""
		string description  ""
		string image  "nullable"
	}

	RECIPE_INGREDIENT {
		long id PK ""
		long recipeId  ""
		long ingredientMasterId  "nullable, 마스터 재료일 시"
		string name  "nullable, 마스터에 없는 재료일 시"
		string amount  "e.g. 50g"
	}

	MEAL_LOG {
		long id PK ""
		long babyId  ""
		string type  "BREAKFAST|LUNCH|DINNER|SNACK"
		int snackIndex  "nullable, SNACK일 때만"
		string notes  ""
		string reaction  "GOOD|NEUTRAL|BAD"
		long createdByUserId  ""
		datetime createdAt  ""
	}

	MEAL_FOOD {
		long id PK ""
		long mealLogId  ""
		long inventoryItemId  "nullable, 인벤토리 참조 시"
		string name  "nullable, 직접 입력 시"
	}

	%% 관계
	USER ||--o{ USER_BABY : ""
	BABY ||--o{ USER_BABY : ""
	BABY ||--o{ BABY_ALLERGY : "보유"
	BABY ||--o{ MEAL_LOG : "기록"

	USER ||--o{ REFRIGERATOR : "소유"
	USER ||--o{ MEAL_LOG : "작성"
	REFRIGERATOR_MODEL ||--o{ REFRIGERATOR : "모델"
	REFRIGERATOR ||--o{ INVENTORY_ITEM : "보관"

	INGREDIENT_MASTER ||--o{ INGREDIENT_MASTER_ALLERGY : ""
	INGREDIENT_MASTER ||--o{ INVENTORY_ITEM_INGREDIENT : ""
	INGREDIENT_MASTER ||--o{ RECIPE_INGREDIENT : ""

	INVENTORY_ITEM ||--o{ INVENTORY_ITEM_INGREDIENT : "구성"
	INVENTORY_ITEM ||--o{ MEAL_FOOD : "참조"

	RECIPE ||--o{ RECIPE_STEP : "단계"
	RECIPE ||--o{ RECIPE_INGREDIENT : "재료"

	MEAL_LOG ||--o{ MEAL_FOOD : "음식"
```

## 참고사항

### Compartments (냉장고 칸)
- `REFRIGERATOR_MODEL.compartments` JSON 예시:
```json
[
  {"id": "00101", "name": "냉장 좌측 1단"},
  {"id": "00102", "name": "냉장 좌측 2단"},
  {"id": "00201", "name": "냉동 상단"}
]
```
- `INVENTORY_ITEM.compartmentId`는 해당 JSON 내의 `id`를 문자열로 저장

### Nullable 제약조건
- `INVENTORY_ITEM_INGREDIENT`: `ingredientMasterId` 또는 `name` 중 하나는 필수
- `RECIPE_INGREDIENT`: `ingredientMasterId` 또는 `name` 중 하나는 필수
- `MEAL_FOOD`: `inventoryItemId` 또는 `name` 중 하나는 필수
