package kr.co.growmeal.refrigerator.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefrigeratorRepository extends JpaRepository<Refrigerator, Long> {
    List<Refrigerator> findByUserId(Long userId);
    Optional<Refrigerator> findByIdAndUserId(Long id, Long userId);
}
