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

    @Query("select r from ItemRequest r where r.requesterId <> ?1")
    List<ItemRequest> findAllOtherUsersRequests(Long userId);

}

