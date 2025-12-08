package ru.practicum.server.request.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.request.ItemRequest;

/**
 * Репозиторий для запросов вещей.
 */
@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestor_IdOrderByCreatedDesc(Long requestorId);

    List<ItemRequest> findAllByRequestor_IdNotOrderByCreatedDesc(Long requestorId);
}
