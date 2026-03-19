package kr.co.growmeal.refrigerator.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refrigerator_models")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefrigeratorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String model;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String compartments;

    @Builder
    public RefrigeratorModel(String model, String name, String imageUrl, String compartments) {
        this.model = model;
        this.name = name;
        this.imageUrl = imageUrl;
        this.compartments = compartments;
    }
}
