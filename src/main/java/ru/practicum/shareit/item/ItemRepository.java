package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(long userId, Pageable pageable);

    @Query("select it " +
            "from Item as it " +
            "join it.owner as u " +
            "where (lower(it.name) like %?1% or lower(it.description) like %?1%) " +
            "and it.available=true")
    List<Item> findAllByQuery(String query, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);

    boolean deleteById(long itemId);

    List<Item> findByRequestIdIn(List<Long> requestIds);
}