# GrowMeal API Specification

**Base URL**: `https://api.growmeal.app/api/v1`
**Auth**: Bearer Token (JWT) — 모든 요청에 `Authorization: Bearer <accessToken>` 헤더 필요 (로그인/회원가입 제외)
**Content-Type**: `application/json`

**Token 정책**:

- Access Token: 15분 유효, 모든 API 요청에 사용
- Refresh Token: 7일 유효, Access Token 갱신에만 사용

---

## 공통 응답 형식

```json
{
  "success": true,
  "data": { ... },
  "error": null
}
```

오류 시:

```json
{
  "success": false,
  "data": null,
  "error": { "code": "NOT_FOUND", "message": "해당 리소스를 찾을 수 없습니다." }
}
```

---

## 1. 인증 (Auth)

| Method | Path                                | 설명                   |
| ------ | ----------------------------------- | ---------------------- |
| `POST` | `/auth/phone-verifications`         | 전화번호 인증번호 발송 |
| `POST` | `/auth/phone-verifications/confirm` | 인증번호 확인          |
| `POST` | `/auth/register`                    | 회원가입               |
| `POST` | `/auth/login`                       | 로그인                 |
| `POST` | `/auth/refresh`                     | Access Token 갱신      |
| `POST` | `/auth/logout`                      | 로그아웃               |
| `GET`  | `/auth/me`                          | 내 프로필 조회         |

### POST /auth/phone-verifications

전화번호로 인증번호를 발송합니다. 회원가입 전에 반드시 전화번호 인증을 완료해야 합니다.

```json
// Request
{
  "phoneNumber": "01012345678"
}

// Response 200
{}

// Error 429 - 너무 많은 요청
{
  "success": false,
  "data": null,
  "error": {
    "code": "TOO_MANY_REQUESTS",
    "message": "인증번호 발송 요청이 너무 많습니다. 잠시 후 다시 시도해주세요."
  }
}
```

### POST /auth/phone-verifications/confirm

발송된 인증번호를 확인합니다.

```json
// Request
{
  "phoneNumber": "01012345678",
  "code": "123456"
}

// Response 200
{}

// Error 400 - 인증번호 불일치
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_VERIFICATION_CODE",
    "message": "인증번호가 일치하지 않습니다."
  }
}

// Error 410 - 인증번호 만료
{
  "success": false,
  "data": null,
  "error": {
    "code": "VERIFICATION_CODE_EXPIRED",
    "message": "인증번호가 만료되었습니다. 다시 요청해주세요."
  }
}
```

### POST /auth/register

```json
// Request
{
  "email": "mom@example.com",
  "phoneNumber": "01012345678",
  "password": "string",
  "name": "김엄마",
  "role": "MOM" // "MOM" | "DAD" | "GRANDMA" | "GRANDPA" | "OTHER"
}

// Response 201
{
  "userId": "uuid",
  "name": "김엄마",
  "role": "MOM",
}

// Error 409 - Email already exists
{
  "success": false,
  "data": null,
  "error": {
    "code": "DUPLICATE_EMAIL",
    "message": "이미 사용 중인 이메일입니다."
  }
}

// Error 409 - Phone number already exists
{
  "success": false,
  "data": null,
  "error": {
    "code": "DUPLICATE_PHONE_NUMBER",
    "message": "이미 사용 중인 전화번호입니다."
  }
}
```

### POST /auth/login

```json
// Request
{
  "email": "mom@example.com",
  "password": "string"
}

// Response 200
{
  "userId": "uuid",
  "name": "김엄마",
  "role": "MOM",
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 900  // Access Token 만료 시간 (초) - 15분
}

// Error 401 - Email or password is incorrect
{
  "success": false,
  "data": null,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "이메일 또는 비밀번호가 일치하지 않습니다."
  }
}
```

### POST /auth/refresh

Access Token이 만료되었을 때 Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.

```json
// Request
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

// Response 200
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 900  // Access Token 만료 시간 (초) - 15분
}

// Error 401 - Refresh Token이 만료되거나 유효하지 않은 경우
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_REFRESH_TOKEN",
    "message": "Refresh Token이 유효하지 않습니다. 다시 로그인해주세요."
  }
}
```

### POST /auth/logout

로그아웃 시 서버에서 Refresh Token을 무효화합니다.

```json
// Request
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

// Response 200
{
  "success": true,
  "data": { "message": "ok" },
  "error": null
}
```

### GET /auth/me

```json
// Response 200
{ "userId": "uuid", "name": "김엄마", "role": "MOM", "babyId": "uuid" }
```

---

## 2. 아기 프로필 (Baby)

| Method | Path              | 설명             |
| ------ | ----------------- | ---------------- |
| `POST` | `/babies`         | 아기 프로필 생성 |
| `GET`  | `/babies/:babyId` | 아기 프로필 조회 |
| `PUT`  | `/babies/:babyId` | 아기 프로필 수정 |

### POST /babies

```json
// Request
{
  "name": "하율",
  "birthDate": "2024-01-15",
  "allergies": ["땅콩", "우유"] // 알레르기 식품
}

// Response 201
{
  "babyId": "uuid",
  "name": "하율",
  "birthDate": "2024-01-15",
  "allergies": ["땅콩", "우유"]
}
```

### GET /babies/:babyId

```json
// Response 200
{
  "babyId": "uuid",
  "name": "하율",
  "birthDate": "2024-01-15",
  "allergies": ["땅콩", "우유"]
}
```

### PUT /babies/:babyId

```json
// Request
{
  "name": "도율",
  "birthDate": "2024-01-15",
  "allergies": []
}

// Response 200
{
  "babyId": "uuid",
  "name": "도율",
  "birthDate": "2024-01-15",
  "allergies": []
}
```

---

## 3. 냉장고 관리 (Refrigerators)

> 부모가 공유하는 냉장고를 관리합니다. 냉장고는 모델별로 정의된 compartment 시스템으로 재료/반찬의 위치를 표현합니다.

| Method   | Path                             | 설명                  |
| -------- | -------------------------------- | --------------------- |
| `GET`    | `/refrigerator-models`           | 냉장고 모델 목록 조회 |
| `POST`   | `/refrigerators`                 | 냉장고 생성           |
| `GET`    | `/refrigerators`                 | 내 냉장고 목록 조회   |
| `GET`    | `/refrigerators/:refrigeratorId` | 냉장고 상세 조회      |
| `PUT`    | `/refrigerators/:refrigeratorId` | 냉장고 정보 수정      |
| `DELETE` | `/refrigerators/:refrigeratorId` | 냉장고 삭제           |

### GET /refrigerator-models

사용 가능한 냉장고 모델 목록을 조회합니다.

```json
// Response 200
{
  "models": [
    {
      "model": "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR",
      "name": "비스포크 키친핏맥스 4도어",
      "imageUrl": "https://example.com/four_door.png"
    }
  ]
}
```

### POST /refrigerators

냉장고를 생성합니다.

```json
// Request
{
  "nickname": "주방 냉장고",
  "model": "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR"
}

// Response 201
{
  "refrigeratorId": "uuid",
  "nickname": "주방 냉장고",
  "model": "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR",
  "createdAt": "2026-03-08T10:00:00Z"
}
```

### GET /refrigerators

냉장고 목록을 조회합니다.

```json
// Response 200
{
  "refrigerators": [
    {
      "refrigeratorId": "uuid",
      "nickname": "주방 냉장고",
      "model": "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR",
      "itemCount": 15, // 보관 중인 재료/반찬 수
      "createdAt": "2026-03-08T10:00:00Z"
    }
  ]
}
```

### GET /refrigerators/:refrigeratorId

냉장고 기본 정보와 냉장고 칸 별 재료/반찬을 조회합니다.

```json
// Response 200
{
  "refrigeratorId": "uuid",
  "nickname": "주방 냉장고",
  "model": "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR",
  "compartments": [
    {
      "compartmentId": "냉장_좌_2단",
      "name": "냉장_좌_2단",
      "items": [
        {
          "itemId": "uuid",
          "name": "계란찜",
          "createdAt": "2026-03-08T00:00:00Z",
          "expirationDate": "2026-03-08T10:00:00Z"
        }
      ]
    },
    {
      "compartmentId": "냉장_우_2단",
      "name": "냉장_우_2단",
      "items": []
    }
  ],
  "createdAt": "2026-03-08T10:00:00Z"
}
```

### PUT /refrigerators/:refrigeratorId

냉장고 정보를 수정합니다.

```json
// Request
{
  "nickname": "우리집 냉장고"
}

// Response 200
{
  "refrigeratorId": "uuid",
  "nickname": "우리집 냉장고",
  "model": "SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR",
  "updatedAt": "2026-03-08T11:00:00Z"
}
```

## 4. 인벤토리 (Inventory)

> 반찬과 재료를 관리합니다.

| Method   | Path                 | 설명               |
| -------- | -------------------- | ------------------ |
| `GET`    | `/inventory`         | 인벤토리 목록 조회 |
| `POST`   | `/inventory`         | 인벤토리 추가      |
| `PUT`    | `/inventory/:itemId` | 인벤토리 정보 수정 |
| `DELETE` | `/inventory/:itemId` | 인벤토리 삭제      |

### GET /inventory

모든 반찬/재료를 조회합니다.

```json
// Response 200
{
  "inventory": [
    {
      "itemId": "uuid",
      "name": "계란찜",
      "type": "MEAL",
      "refrigeratorId": "uuid", // 보관 중인 냉장고 ID
      "compartmentId": "냉장_좌_2단", // 냉장고 compartment ID
      "nutrients": ["PROTEIN"],
      "allergyInfo": ["EGG"],
      "expiresAt": "2026-03-09",
      "createdAt": "2026-03-07T10:00:00Z"
    }
  ]
}
```

### POST /inventory

```json
// Request
{
  "name": "당근",
  "type": "INGREDIENT",
  "refrigeratorId": "uuid",     // 보관할 냉장고 ID
  "compartmentId": "냉장_우_1단",  // 냉장고 compartment ID
  "expiresAt": "2026-03-10"
}

// Response 201
{
  "itemId": "uuid",
  "name": "당근",
  "type": "INGREDIENT",
  "refrigeratorId": "uuid",     // 보관할 냉장고 ID
  "compartmentId": "냉장_우_1단",  // 냉장고 compartment ID
  "nutrients": ["VITAMIN_A"], // ingredient master의 nutrients
  "allergyInfo": [], // ingredient master의 allergyInfo
  "expiresAt": "2026-03-10"
}
```

### PUT /inventory/:itemId

```json
// Request (변경할 필드만 전송)
{
  "compartmentId": "냉동_상단" // 위치 변경
}


// Response 200
{
  "itemId": "uuid",
  "name": "당근",
  "type": "INGREDIENT",
  "refrigeratorId": "uuid", // 보관할 냉장고 ID
  "compartmentId": "냉장\_우\_1단", // 냉장고 compartment ID
  "nutrients": ["VITAMIN_A"], // ingredient master의 nutrients
  "allergyInfo": [], // ingredient master의 allergyInfo
  "expiresAt": "2026-03-10"
}
```

---

## 5. 재료 마스터 정보 (Ingredient Master)

> 각 재료별 영양소, 알러지 정보 등을 관리하는 마스터 데이터입니다.
> 레시피 생성 시 자동으로 영양소와 알러지 정보를 계산하는 데 사용됩니다.

| Method | Path                                | 설명                |
| ------ | ----------------------------------- | ------------------- |
| `GET`  | `/ingredients/master`               | 모든 재료 정보 조회 |
| `GET`  | `/ingredients/master/search`        | 재료 검색           |
| `GET`  | `/ingredients/master/:ingredientId` | 특정 재료 정보 조회 |

### GET /ingredients/master

모든 재료 마스터 정보를 조회합니다.

```json
// Response 200
{
  "ingredients": [
    {
      "ingredientId": "uuid",
      "name": "두부",
      "category": "PROTEIN", // PROTEIN | VEGETABLE | GRAIN | DAIRY | MEAT | FISH | FRUIT | ETC
      "nutrients": ["PROTEIN", "CALCIUM", "IRON"],
      "allergyInfo": ["SOYBEAN"],
      "description": "부드러운 식감의 단백질 공급원"
    },
    {
      "ingredientId": "uuid",
      "name": "미역",
      "category": "VEGETABLE",
      "nutrients": ["CALCIUM", "IRON", "VITAMIN_K"],
      "allergyInfo": [],
      "description": "칼슘과 요오드가 풍부한 해조류"
    }
  ]
}
```

### GET /ingredients/master/search

재료 이름으로 검색합니다.

Query params: `q` (검색어, 필수)

```json
// Request
GET /ingredients/master/search?q=두부

// Response 200
{
  "ingredients": [
    {
      "ingredientId": "uuid",
      "name": "두부",
      "category": "PROTEIN",
      "nutrients": ["PROTEIN", "CALCIUM", "IRON"],
      "allergyInfo": ["SOYBEAN"],
      "description": "부드러운 식감의 단백질 공급원"
    },
    {
      "ingredientId": "uuid",
      "name": "순두부",
      "category": "PROTEIN",
      "nutrients": ["PROTEIN", "CALCIUM"],
      "allergyInfo": ["SOYBEAN"],
      "description": "부드러운 순두부"
    }
  ]
}
```

### GET /ingredients/master/:ingredientId

특정 재료의 상세 정보를 조회합니다.

```json
// Response 200
{
  "ingredientId": "uuid",
  "name": "두부",
  "category": "PROTEIN",
  "nutrients": ["PROTEIN", "CALCIUM", "IRON"],
  "allergyInfo": ["대두"],
  "description": "부드러운 식감의 단백질 공급원"
}
```

**영양소 코드 (Nutrient Codes)**

| Code        | 한글명   |
| ----------- | -------- |
| `PROTEIN`   | 단백질   |
| `CALCIUM`   | 칼슘     |
| `IRON`      | 철분     |
| `VITAMIN_A` | 비타민A  |
| `VITAMIN_C` | 비타민C  |
| `VITAMIN_D` | 비타민D  |
| `VITAMIN_K` | 비타민K  |
| `FIBER`     | 식이섬유 |
| `OMEGA_3`   | 오메가3  |
| `ZINC`      | 아연     |

---

## 6. 레시피 (Recipes)

> 레시피 생성 시 `ingredients`만 입력하면, 서버에서 재료 마스터 정보를 기반으로 `nutrients`와 `allergyWarnings`를 자동 계산합니다.

| Method   | Path                 | 설명                       |
| -------- | -------------------- | -------------------------- |
| `GET`    | `/recipes`           | 보유 재료 기반 레시피 추천 |
| `POST`   | `/recipes`           | 레시피 추가                |
| `GET`    | `/recipes/:recipeId` | 레시피 상세 조회           |
| `PUT`    | `/recipes/:recipeId` | 레시피 수정                |
| `DELETE` | `/recipes/:recipeId` | 레시피 삭제                |

### GET /recipes

```json
// Response 200
{
  "recipes": [
    {
      "recipeId": "uuid",
      "name": "두부미역국",
      "difficulty": "MEDIUM", // EASY | MEDIUM | HARD
      "steps": [
        {
          "step": 1,
          "description": "두부를 깍둑썬다",
          "image": "https://example.com/step1.png"
        }
      ],
      "nutrients": ["CALCIUM", "IRON"],
      "ingredients": [
        {
          "name": "두부",
          "amount": "50g"
        },
        {
          "name": "미역",
          "amount": "50g"
        }
      ],
      "missingIngredients": [
        {
          "name": "마늘",
          "amount": "5g"
        }
      ],
      "allergyWarnings": [],
      "createdAt": "2026-03-08T10:00:00Z"
    }
  ]
}
```

### GET /recipes/:recipeId

```json
// Response 200
{
  "recipeId": "uuid",
  "name": "두부미역국",
  "difficulty": "MEDIUM", // EASY | MEDIUM | HARD
  "steps": [
    {
      "step": 1,
      "description": "두부를 깍둑썬다",
      "image": "https://example.com/step1.png"
    }
  ],
  "nutrients": ["CALCIUM", "IRON"],
  "ingredients": [
    {
      "name": "두부",
      "amount": "50g"
    },
    {
      "name": "미역",
      "amount": "50g"
    }
  ],
  "missingIngredients": [
    {
      "name": "마늘",
      "amount": "5g"
    }
  ],
  "allergyWarnings": [],
  "createdAt": "2026-03-08T10:00:00Z"
}
```

### POST /recipes

새로운 레시피를 생성합니다. `ingredients`만 입력하면 서버에서 재료 마스터 정보를 기반으로 `nutrients`와 `allergyWarnings`를 자동 계산합니다. 재료 마스터에 없는 재료가 포함된 경우 해당 재료의 `nutrient`와 `allergyWarning`은 계산되지 않습니다.

```json
// Request
{
  "name": "두부미역국",
  "difficulty": "MEDIUM",  // "EASY" | "MEDIUM" | "HARD"
  "steps": [
    {
      "step": 1,
      "description": "두부를 깍둑썬다",
      "image": "https://example.com/step1.png"  // optional
    },
    {
      "step": 2,
      "description": "미역을 불린다"
    },
    {
      "step": 3,
      "description": "함께 끓인다"
    }
  ],
  "ingredients": [
    {
      "name": "두부",
      "amount": "50g"
    },
    {
      "name": "미역",
      "amount": "30g"
    },
    {
      "name": "마늘",
      "amount": "5g"
    }
  ]
  // nutrients와 allergyWarnings는 자동 계산되므로 입력하지 않음
}

// Response 201
{
  "recipeId": "uuid",
  "name": "두부미역국",
  "difficulty": "MEDIUM",
  "steps": [
    {
      "step": 1,
      "description": "두부를 깍둑썬다",
      "image": "https://example.com/step1.png"
    },
    {
      "step": 2,
      "description": "미역을 불린다"
    },
    {
      "step": 3,
      "description": "함께 끓인다"
    }
  ],
  "nutrients": ["PROTEIN", "CALCIUM", "IRON", "VITAMIN_K"],  // 자동 계산됨 (두부 + 미역 + 마늘)
  "ingredients": [
    {
      "name": "두부",
      "amount": "50g"
    },
    {
      "name": "미역",
      "amount": "30g"
    },
    {
      "name": "마늘",
      "amount": "5g"
    }
  ],
  "allergyWarnings": ["대두"],  // 자동 계산됨 (두부의 알러지 정보)
  "createdAt": "2026-03-08T10:00:00Z"
}

```

### PUT /recipes/:recipeId

레시피 정보를 수정합니다. 변경할 필드만 전송합니다. `ingredients`를 수정하면 `nutrients`와 `allergyWarnings`가 자동으로 재계산됩니다.

```json
// Request (변경할 필드만 전송)
{
  "name": "두부미역국 (개선판)",
  "difficulty": "EASY",
  "steps": [
    {
      "step": 1,
      "description": "두부와 미역을 준비한다"
    },
    {
      "step": 2,
      "description": "함께 끓인다"
    }
  ]
}

// Response 200
{
  "recipeId": "uuid",
  "name": "두부미역국 (개선판)",
  "difficulty": "EASY",
  "steps": [
    {
      "step": 1,
      "description": "두부와 미역을 준비한다"
    },
    {
      "step": 2,
      "description": "함께 끓인다"
    }
  ],
  "nutrients": ["PROTEIN", "CALCIUM", "IRON", "VITAMIN_K"],
  "ingredients": [
    {
      "name": "두부",
      "amount": "50g"
    },
    {
      "name": "미역",
      "amount": "30g"
    },
    {
      "name": "마늘",
      "amount": "5g"
    }
  ],
  "allergyWarnings": ["대두"],
  "updatedAt": "2026-03-08T11:30:00Z"
}

// Example: ingredients를 수정하는 경우
// Request
{
  "ingredients": [
    {
      "name": "두부",
      "amount": "100g"
    },
    {
      "name": "미역",
      "amount": "50g"
    }
  ]
}

// Response 200
{
  "recipeId": "uuid",
  "name": "두부미역국 (개선판)",
  "difficulty": "EASY",
  "steps": [...],
  "nutrients": ["PROTEIN", "CALCIUM", "IRON", "VITAMIN_K"],  // 재계산됨
  "ingredients": [
    {
      "name": "두부",
      "amount": "100g"
    },
    {
      "name": "미역",
      "amount": "50g"
    }
  ],
  "allergyWarnings": ["대두"],  // 재계산됨
  "updatedAt": "2026-03-08T11:35:00Z"
}
```

### DELETE /recipes/:recipeId

레시피를 삭제합니다.

```json
// Response 200
{
  "success": true,
  "data": {
    "message": "레시피가 삭제되었습니다.",
    "recipeId": "uuid"
  },
  "error": null
}

// Error 404 - 레시피를 찾을 수 없는 경우
{
  "success": false,
  "data": null,
  "error": {
    "code": "RECIPE_NOT_FOUND",
    "message": "해당 레시피를 찾을 수 없습니다."
  }
}
```

---

## 7. 끼니 기록 (Meal Logs)

> 아침·점심·저녁·간식을 매 끼니마다 기록하고 부모가 공유합니다.

| Method   | Path                                              | 설명                        |
| -------- | ------------------------------------------------- | --------------------------- |
| `GET`    | `/babies/:babyId/meals`                           | 날짜별 끼니 기록 조회       |
| `POST`   | `/babies/:babyId/meals`                           | 끼니 기록 생성              |
| `PUT`    | `/babies/:babyId/meals/:mealId`                   | 끼니 기록 수정              |
| `DELETE` | `/babies/:babyId/meals/:mealId`                   | 끼니 기록 삭제              |
| `GET`    | `/babies/:babyId/meals/:mealId`                   | 끼니 기록 조회              |
| `GET`    | `/babies/:babyId/meals/recommendations/inventory` | 냉장고 인벤토리 아이템 추천 |

### GET /babies/:babyId/meals

Query params: `date=2026-03-08` (필수 또는 `startDate` + `endDate`)

```json
// Response 200
{
  "date": "2026-03-08",
  "meals": [
    {
      "mealId": "uuid",
      "type": "BREAKFAST", // "BREAKFAST" | "LUNCH" | "DINNER" | "SNACK"
      "snackIndex": null, // SNACK일 때 순서 (1, 2, 3 ...)
      "foods": [
        {
          "name": "쌀밥",
          "inventoryItemId": "uuid"
        }
      ],
      "notes": "잘 먹었어요",
      "reaction": "GOOD", // "GOOD" | "NEUTRAL" | "BAD"
      "createdBy": "MOM",
      "createdAt": "2026-03-08T08:30:00Z"
    }
  ]
}
```

### POST /babies/:babyId/meals

```json
// Request
{
  "type": "SNACK",
  "snackIndex": 1,
  "foods": [
    { "name": "쌀과자" }
  ],
  "notes": "",
  "reaction": "GOOD"
}

// Response 201
{ "mealId": "uuid", ... }
```

### GET /babies/:babyId/meals/:mealId

```json
// Response 200
{
  "mealId": "uuid",
  "type": "BREAKFAST", // "BREAKFAST" | "LUNCH" | "DINNER" | "SNACK"
  "snackIndex": null, // SNACK일 때 순서 (1, 2, 3 ...)
  "foods": [
    {
      "name": "쌀밥",
      "inventoryItemId": "uuid"
    }
  ],
  "notes": "잘 먹었어요",
  "reaction": "GOOD", // "GOOD" | "NEUTRAL" | "BAD"
  "createdBy": "MOM",
  "createdAt": "2026-03-08T08:30:00Z"
}
```

### GET /babies/:babyId/meals/recommendations/inventory

당일 먹은 음식을 기반으로 부족한 영양소를 채워줄 수 있는 냉장고 인벤토리 아이템을 추천합니다.
당일 첫끼라면 한 주간의 끼니를 기반으로 추천합니다.

```json
// Response 200
{
  "recommendations": [
    {
      "inventory": [
        {
          "itemId": "uuid",
          "name": "소불고기",
          "type": "FOOD",
          "refrigeratorId": "uuid",
          "compartmentId": "냉장\_우\_1단",
          "nutrients": ["PROTEIN"],
          "allergyInfo": [],
          "createdAt": "2026-03-08T08:30:00Z",
          "expiresAt": "2026-03-15T08:30:00Z"
        }
      ]
    }
  ]
}
```
