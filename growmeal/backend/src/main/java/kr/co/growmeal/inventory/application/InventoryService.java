package kr.co.growmeal.inventory.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.growmeal.auth.domain.User;
import kr.co.growmeal.auth.domain.UserRepository;
import kr.co.growmeal.auth.domain.exception.UserNotFoundException;
import kr.co.growmeal.inventory.domain.InventoryItem;
import kr.co.growmeal.inventory.domain.InventoryItemRepository;
import kr.co.growmeal.inventory.domain.exception.InvalidCompartmentException;
import kr.co.growmeal.inventory.ui.dto.request.CreateInventoryItemRequest;
import kr.co.growmeal.inventory.ui.dto.response.CreateInventoryItemResponse;
import kr.co.growmeal.inventory.ui.dto.response.InventoryItemResponse;
import kr.co.growmeal.inventory.ui.dto.response.InventoryResponse;
import kr.co.growmeal.refrigerator.domain.Refrigerator;
import kr.co.growmeal.refrigerator.domain.RefrigeratorRepository;
import kr.co.growmeal.refrigerator.domain.exception.RefrigeratorNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final RefrigeratorRepository refrigeratorRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public InventoryResponse getInventory(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        List<Long> refrigeratorIds = refrigeratorRepository.findByUserId(user.getId()).stream()
            .map(Refrigerator::getId)
            .toList();

        if (refrigeratorIds.isEmpty()) {
            return new InventoryResponse(List.of());
        }

        List<InventoryItemResponse> items = inventoryItemRepository.findByRefrigeratorIdIn(refrigeratorIds).stream()
            .sorted(Comparator.comparing(InventoryItem::getCreatedAt).reversed())
            .map(item -> InventoryItemResponse.from(item, List.of(), List.of()))
            .toList();

        return new InventoryResponse(items);
    }

    @Transactional
    public CreateInventoryItemResponse createInventory(String email, CreateInventoryItemRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        Refrigerator refrigerator = refrigeratorRepository.findByIdAndUserId(request.refrigeratorId(), user.getId())
            .orElseThrow(RefrigeratorNotFoundException::new);

        validateCompartmentId(refrigerator.getRefrigeratorModel().getCompartments(), request.compartmentId());

        InventoryItem inventoryItem = InventoryItem.builder()
            .name(request.name())
            .type(request.type())
            .refrigeratorId(request.refrigeratorId())
            .compartmentId(request.compartmentId())
            .expiresAt(request.expiresAt())
            .build();

        InventoryItem saved = inventoryItemRepository.save(inventoryItem);
        return CreateInventoryItemResponse.from(saved, List.of(), List.of());
    }

    private void validateCompartmentId(String compartmentsJson, String compartmentId) {
        if (compartmentsJson == null || compartmentsJson.isBlank()) {
            throw new InvalidCompartmentException();
        }

        try {
            List<Map<String, String>> compartments = objectMapper.readValue(
                compartmentsJson,
                new TypeReference<List<Map<String, String>>>() {
                }
            );
            boolean compartmentExists = compartments.stream()
                .map(compartment -> compartment.get("id"))
                .anyMatch(compartmentId::equals);

            if (!compartmentExists) {
                throw new InvalidCompartmentException();
            }
        } catch (InvalidCompartmentException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidCompartmentException();
        }
    }
}
