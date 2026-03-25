INSERT INTO refrigerator_models (model, name, image_url, compartments)
VALUES ('SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR', '비스포크 키친핏맥스 4도어', 'https://example.com/four_door.png',
        '[{"id":"bkf_1","name":"냉장 좌 도어 상"},{"id":"bkf_2","name":"냉장 좌 도어 중"},{"id":"bkf_3","name":"냉장 좌 도어 하"},{"id":"bkf_4","name":"냉장 중앙 선반 상"},{"id":"bkf_5","name":"냉장 중앙 선반 중"},{"id":"bkf_6","name":"냉장 중앙 선반 하"},{"id":"bkf_7","name":"냉장 좌 신선칸"},{"id":"bkf_8","name":"냉장 중 보조칸"},{"id":"bkf_9","name":"냉장 우 신선칸"},{"id":"bkf_10","name":"냉장 우 도어 상"},{"id":"bkf_11","name":"냉장 우 도어 중"},{"id":"bkf_12","name":"냉장 우 도어 하"},{"id":"bkf_13","name":"냉동 좌 선반 상"},{"id":"bkf_14","name":"냉동 좌 선반 중"},{"id":"bkf_15","name":"냉동 좌 선반 하"},{"id":"bkf_16","name":"냉동 우 선반 상"},{"id":"bkf_17","name":"냉동 우 선반 중"},{"id":"bkf_18","name":"냉동 우 선반 하"}]');

INSERT INTO refrigerator_models (model, name, image_url, compartments)
VALUES ('LG_DIOS_OBJECT_FOUR_DOOR', 'LG 디오스 오브제컬렉션 4도어', 'https://example.com/lg_four_door.png',
        '[{"id":"냉장_좌_1단","name":"냉장 좌측 1단"},{"id":"냉장_좌_2단","name":"냉장 좌측 2단"},{"id":"냉장_우_1단","name":"냉장 우측 1단"},{"id":"냉장_우_2단","name":"냉장 우측 2단"},{"id":"냉동_상단","name":"냉동 상단"}]');

-- Ingredient Master 데이터
INSERT INTO ingredient_masters (name, category, main_nutrient, extra_nutrient, description)
VALUES ('두부', 'PROTEIN', 'PROTEIN', 'CALCIUM', '부드러운 식감의 단백질 공급원');

INSERT INTO ingredient_masters (name, category, main_nutrient, extra_nutrient, description)
VALUES ('미역', 'VEGETABLE', 'CALCIUM', 'IRON', '칼슘과 요오드가 풍부한 해조류');

INSERT INTO ingredient_masters (name, category, main_nutrient, extra_nutrient, description)
VALUES ('당근', 'VEGETABLE', 'VITAMIN_A', NULL, '비타민A가 풍부한 채소');

INSERT INTO ingredient_masters (name, category, main_nutrient, extra_nutrient, description)
VALUES ('순두부', 'PROTEIN', 'PROTEIN', 'CALCIUM', '부드러운 순두부');

-- Ingredient Master Allergy 데이터
INSERT INTO ingredient_master_allergies (ingredient_master_id, allergy_info)
VALUES (1, '대두');

INSERT INTO ingredient_master_allergies (ingredient_master_id, allergy_info)
VALUES (4, '대두');
