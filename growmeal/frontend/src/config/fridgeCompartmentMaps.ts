export interface CompartmentOverlay {
  compartmentId: string;
  top: number;
  left: number;
  width: number;
  height: number;
}

export const FRIDGE_COMPARTMENT_MAPS: Record<string, CompartmentOverlay[]> = {
  SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR: [
    // 냉장 좌 도어 (왼쪽 문 안쪽 선반)
    { compartmentId: 'bkf_1', top: 8, left: 10, width: 17, height: 11 },
    { compartmentId: 'bkf_2', top: 21, left: 10, width: 17, height: 11 },
    { compartmentId: 'bkf_3', top: 40, left: 10, width: 17, height: 11 },

    // 냉장 중앙 선반 (본체 중앙)
    { compartmentId: 'bkf_4', top: 10, left: 27, width: 46, height: 9 },
    { compartmentId: 'bkf_5', top: 21, left: 27, width: 46, height: 9 },
    { compartmentId: 'bkf_6', top: 32, left: 27, width: 46, height: 8 },

    // 냉장 하단 신선칸/보조칸
    { compartmentId: 'bkf_7', top: 40, left: 28, width: 17, height: 12 },
    { compartmentId: 'bkf_8', top: 40, left: 45, width: 12, height: 12 },
    { compartmentId: 'bkf_9', top: 40, left: 57, width: 17, height: 12 },

    // 냉장 우 도어 (오른쪽 문 안쪽 선반)
    { compartmentId: 'bkf_10', top: 8, left: 74, width: 17, height: 11 },
    { compartmentId: 'bkf_11', top: 21, left: 74, width: 17, height: 11 },
    { compartmentId: 'bkf_12', top: 40, left: 74, width: 17, height: 11 },

    // 냉동 좌 선반
    { compartmentId: 'bkf_13', top: 59, left: 12, width: 36, height: 10 },
    { compartmentId: 'bkf_14', top: 71, left: 12, width: 36, height: 10 },
    { compartmentId: 'bkf_15', top: 83, left: 12, width: 36, height: 10 },

    // 냉동 우 선반
    { compartmentId: 'bkf_16', top: 59, left: 52, width: 36, height: 10 },
    { compartmentId: 'bkf_17', top: 71, left: 52, width: 36, height: 10 },
    { compartmentId: 'bkf_18', top: 83, left: 52, width: 36, height: 10 },
  ],
};
