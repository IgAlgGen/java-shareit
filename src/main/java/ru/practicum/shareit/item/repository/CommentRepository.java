package ru.practicum.shareit.item.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

/**
 * Репозиторий комментариев к вещам.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItem_IdOrderByCreatedAsc(Long itemId);

    List<Comment> findByItem_IdInOrderByCreatedAsc(List<Long> itemIds);
}
