package ru.practicum.shareit.booking.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ru.practicum.shareit.user.repository.UserRepository userRepository;

    @Autowired
    private ru.practicum.shareit.item.repository.ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setup() {
        owner = userRepository.save(User.builder().name("Owner").email("owner@mail.com").build());
        booker = userRepository.save(User.builder().name("Booker").email("booker@mail.com").build());
        item = itemRepository.save(Item.builder().name("Bike").description("Fast").available(true).owner(owner).build());

        booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());
    }

    @Test
    void findByBookerId_shouldReturnBooking() {
        List<Booking> result = bookingRepository.findByBooker_Id(booker.getId(), Sort.by("start").descending());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void findByOwnerId_shouldReturnBooking() {
        List<Booking> result = bookingRepository.findByOwnerId(owner.getId(), Sort.by("start").descending());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void findByBookerIdAndStatus_shouldWork() {
        List<Booking> result = bookingRepository.findByBooker_IdAndStatus(booker.getId(), BookingStatus.WAITING, Sort.by("id"));
        assertThat(result).containsExactly(booking);
    }

    @Test
    void existsByBookerIdAndItemIdAndStatusAndEndBefore_shouldReturnFalse() {
        boolean exists = bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                booker.getId(), item.getId(), BookingStatus.APPROVED, LocalDateTime.now());
        assertThat(exists).isFalse();
    }
}
