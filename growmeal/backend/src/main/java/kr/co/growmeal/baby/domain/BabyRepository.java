package kr.co.growmeal.baby.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BabyRepository extends JpaRepository<Baby, Long> {
    Optional<Baby> findFirstByUserId(Long userId);
}
