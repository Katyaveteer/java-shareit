package ru.practicum.shareit.request.repository;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindAllByRequesterIdOrderByCreatedDesc() {
        User user = userRepository.save(new User(null, "Alice", "a@mail.com"));
        ItemRequest r1 = requestRepository.save(new ItemRequest(null, "Need drill", user, LocalDateTime.now().minusHours(1)));
        ItemRequest r2 = requestRepository.save(new ItemRequest(null, "Need hammer", user, LocalDateTime.now()));

        List<ItemRequest> result = requestRepository.findAllByRequesterIdOrderByCreatedDesc(user.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEqualTo("Need hammer");
        assertThat(result.get(1).getDescription()).isEqualTo("Need drill");
    }
}
