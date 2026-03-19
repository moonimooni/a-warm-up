package kr.co.growmeal.refrigerator.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.growmeal.auth.domain.User;
import kr.co.growmeal.auth.domain.UserRepository;
import kr.co.growmeal.auth.domain.exception.UserNotFoundException;
import kr.co.growmeal.refrigerator.domain.Refrigerator;
import kr.co.growmeal.refrigerator.domain.RefrigeratorModel;
import kr.co.growmeal.refrigerator.domain.RefrigeratorModelRepository;
import kr.co.growmeal.refrigerator.domain.RefrigeratorRepository;
import kr.co.growmeal.refrigerator.domain.exception.RefrigeratorModelNotFoundException;
import kr.co.growmeal.refrigerator.domain.exception.RefrigeratorNotFoundException;
import kr.co.growmeal.refrigerator.ui.dto.request.CreateRefrigeratorRequest;
import kr.co.growmeal.refrigerator.ui.dto.request.UpdateRefrigeratorRequest;
import kr.co.growmeal.refrigerator.ui.dto.response.CompartmentResponse;
import kr.co.growmeal.refrigerator.ui.dto.response.CreateRefrigeratorResponse;
import kr.co.growmeal.refrigerator.ui.dto.response.RefrigeratorDetailResponse;
import kr.co.growmeal.refrigerator.ui.dto.response.RefrigeratorsResponse;
import kr.co.growmeal.refrigerator.ui.dto.response.UpdateRefrigeratorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    @Transactional(readOnly = true)
    public RefrigeratorDetailResponse getRefrigeratorDetail(String email, Long refrigeratorId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        Refrigerator refrigerator = refrigeratorRepository.findByIdAndUserId(refrigeratorId, user.getId())
            .orElseThrow(RefrigeratorNotFoundException::new);

        List<CompartmentResponse> compartments = parseCompartments(refrigerator.getRefrigeratorModel().getCompartments());
        return RefrigeratorDetailResponse.from(refrigerator, compartments);
    }

    @Transactional
    public UpdateRefrigeratorResponse updateRefrigerator(String email, Long refrigeratorId, UpdateRefrigeratorRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        Refrigerator refrigerator = refrigeratorRepository.findByIdAndUserId(refrigeratorId, user.getId())
            .orElseThrow(RefrigeratorNotFoundException::new);

        refrigerator.updateNickname(request.nickname());
        return UpdateRefrigeratorResponse.from(refrigerator);
    }

    @Transactional
    public void deleteRefrigerator(String email, Long refrigeratorId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        Refrigerator refrigerator = refrigeratorRepository.findByIdAndUserId(refrigeratorId, user.getId())
            .orElseThrow(RefrigeratorNotFoundException::new);

        refrigeratorRepository.delete(refrigerator);
    }

    private List<CompartmentResponse> parseCompartments(String compartmentsJson) {
        if (compartmentsJson == null || compartmentsJson.isBlank()) {
            return List.of();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> compartments = objectMapper.readValue(
                compartmentsJson, new TypeReference<List<Map<String, String>>>() {}
            );
            return compartments.stream()
                .map(c -> CompartmentResponse.of(c.get("id"), c.get("name")))
                .toList();
        } catch (Exception e) {
            return List.of();
        }
    }
}
