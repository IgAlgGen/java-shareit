package ru.practicum.shareit.item.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

/**
 * Репозиторий вещей.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderById(Long ownerId);

    @Query(" select i from Item i "
            + "where (upper(i.name) like upper(concat('%', ?1, '%')) "
            + " or upper(i.description) like upper(concat('%', ?1, '%'))) "
            + " and i.available = true")
    List<Item> search(String text);

    void deleteAllByOwnerId(Long ownerId);

    List<Item> findByRequestId(Long requestId);

    List<Item> findByRequestIdInOrderById(List<Long> requestIds);
}
