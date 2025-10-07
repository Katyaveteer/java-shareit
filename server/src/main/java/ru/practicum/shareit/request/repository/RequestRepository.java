package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;


@Repository
public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequesterId(Long requesterId, Sort sort);

    @Query("SELECT r FROM ItemRequest r WHERE r.requester.id <> ?1 ORDER BY r.created DESC")
    List<ItemRequest> findAllOtherUsersRequests(Long userId);

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long requesterId);

    List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(Long requesterId);

}

