package kr.co.growmeal.refrigerator.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefrigeratorRepository extends JpaRepository<Refrigerator, Long> {
    List<Refrigerator> findByUserId(Long userId);
}
