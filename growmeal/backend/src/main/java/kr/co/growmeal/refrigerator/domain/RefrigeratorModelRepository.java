package kr.co.growmeal.refrigerator.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefrigeratorModelRepository extends JpaRepository<RefrigeratorModel, Long> {
    Optional<RefrigeratorModel> findByModel(String model);
}
