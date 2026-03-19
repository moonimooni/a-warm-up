package kr.co.growmeal.refrigerator.application;

import kr.co.growmeal.auth.domain.User;
import kr.co.growmeal.auth.domain.UserRepository;
import kr.co.growmeal.auth.domain.exception.UserNotFoundException;
import kr.co.growmeal.refrigerator.domain.Refrigerator;
import kr.co.growmeal.refrigerator.domain.RefrigeratorModel;
import kr.co.growmeal.refrigerator.domain.RefrigeratorModelRepository;
import kr.co.growmeal.refrigerator.domain.RefrigeratorRepository;
import kr.co.growmeal.refrigerator.domain.exception.RefrigeratorModelNotFoundException;
import kr.co.growmeal.refrigerator.ui.dto.request.CreateRefrigeratorRequest;
import kr.co.growmeal.refrigerator.ui.dto.response.CreateRefrigeratorResponse;
import kr.co.growmeal.refrigerator.ui.dto.response.RefrigeratorsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RefrigeratorService {

    private final RefrigeratorRepository refrigeratorRepository;
    private final RefrigeratorModelRepository refrigeratorModelRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateRefrigeratorResponse createRefrigerator(String email, CreateRefrigeratorRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        RefrigeratorModel model = refrigeratorModelRepository.findByModel(request.model())
            .orElseThrow(RefrigeratorModelNotFoundException::new);

        Refrigerator refrigerator = Refrigerator.builder()
            .user(user)
            .refrigeratorModel(model)
            .nickname(request.nickname())
            .build();

        Refrigerator saved = refrigeratorRepository.save(refrigerator);
        return CreateRefrigeratorResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public RefrigeratorsResponse getMyRefrigerators(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        List<Refrigerator> refrigerators = refrigeratorRepository.findByUserId(user.getId());
        return RefrigeratorsResponse.from(refrigerators);
    }
}
