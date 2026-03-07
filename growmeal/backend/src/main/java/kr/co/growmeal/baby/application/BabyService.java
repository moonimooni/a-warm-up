package kr.co.growmeal.baby.application;

import kr.co.growmeal.auth.domain.User;
import kr.co.growmeal.auth.domain.UserRepository;
import kr.co.growmeal.baby.domain.Baby;
import kr.co.growmeal.baby.domain.BabyRepository;
import kr.co.growmeal.baby.ui.dto.request.CreateBabyRequest;
import kr.co.growmeal.baby.ui.dto.response.CreateBabyResponse;
import kr.co.growmeal.auth.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BabyService {

    private final BabyRepository babyRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateBabyResponse createBaby(String email, CreateBabyRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        String allergies = toAllergiesString(request.allergies());

        Baby baby = Baby.builder()
            .user(user)
            .name(request.name())
            .birthDate(request.birthDate())
            .heightCm(request.heightCm())
            .weightKg(request.weightKg())
            .allergies(allergies)
            .build();

        Baby saved = babyRepository.save(baby);
        return CreateBabyResponse.from(saved);
    }

    private String toAllergiesString(List<String> allergies) {
        if (allergies == null || allergies.isEmpty()) {
            return null;
        }
        return String.join(",", allergies);
    }
}
