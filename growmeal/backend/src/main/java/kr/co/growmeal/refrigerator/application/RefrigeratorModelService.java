package kr.co.growmeal.refrigerator.application;

import kr.co.growmeal.refrigerator.domain.RefrigeratorModel;
import kr.co.growmeal.refrigerator.domain.RefrigeratorModelRepository;
import kr.co.growmeal.refrigerator.ui.dto.response.RefrigeratorModelsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RefrigeratorModelService {

    private final RefrigeratorModelRepository refrigeratorModelRepository;

    @Transactional(readOnly = true)
    public RefrigeratorModelsResponse getAllModels() {
        List<RefrigeratorModel> models = refrigeratorModelRepository.findAll();
        return RefrigeratorModelsResponse.from(models);
    }
}
