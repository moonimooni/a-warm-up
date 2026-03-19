package kr.co.growmeal.refrigerator.application;

import kr.co.growmeal.refrigerator.domain.RefrigeratorModel;
import kr.co.growmeal.refrigerator.domain.RefrigeratorModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefrigeratorModelDataInitializer implements ApplicationRunner {

    private final RefrigeratorModelRepository refrigeratorModelRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (refrigeratorModelRepository.count() == 0) {
            refrigeratorModelRepository.save(RefrigeratorModel.builder()
                .model("SAMSUNG_BESPOKE_KITCHENFITMAX_FOUR_DOOR")
                .name("비스포크 키친핏맥스 4도어")
                .imageUrl("https://example.com/four_door.png")
                .compartments("[{\"id\":\"냉장_좌_1단\",\"name\":\"냉장 좌측 1단\"},{\"id\":\"냉장_좌_2단\",\"name\":\"냉장 좌측 2단\"},{\"id\":\"냉장_우_1단\",\"name\":\"냉장 우측 1단\"},{\"id\":\"냉장_우_2단\",\"name\":\"냉장 우측 2단\"},{\"id\":\"냉동_상단\",\"name\":\"냉동 상단\"},{\"id\":\"냉동_하단\",\"name\":\"냉동 하단\"}]")
                .build());

            refrigeratorModelRepository.save(RefrigeratorModel.builder()
                .model("LG_DIOS_OBJECT_FOUR_DOOR")
                .name("LG 디오스 오브제컬렉션 4도어")
                .imageUrl("https://example.com/lg_four_door.png")
                .compartments("[{\"id\":\"냉장_좌_1단\",\"name\":\"냉장 좌측 1단\"},{\"id\":\"냉장_좌_2단\",\"name\":\"냉장 좌측 2단\"},{\"id\":\"냉장_우_1단\",\"name\":\"냉장 우측 1단\"},{\"id\":\"냉장_우_2단\",\"name\":\"냉장 우측 2단\"},{\"id\":\"냉동_상단\",\"name\":\"냉동 상단\"}]")
                .build());
        }
    }
}
