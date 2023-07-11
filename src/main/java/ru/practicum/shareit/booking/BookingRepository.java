package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBooker(User booker,
                                  Pageable pageable);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long bookerId,
                                                                          Long itemId,
                                                                          Status status,
                                                                          LocalDateTime end);

    Page<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker,
                                                           LocalDateTime start,
                                                           LocalDateTime end,
                                                           Pageable pageable);

    Page<Booking> findAllByBookerAndEndBefore(User booker,
                                              LocalDateTime end,
                                              Pageable pageable);

    Page<Booking> findAllByBookerAndStartAfter(User booker,
                                               LocalDateTime start,
                                               Pageable pageable);

    Page<Booking> findAllByBookerAndStatusEquals(User booker,
                                                 Status status,
                                                 Pageable pageable);

    Page<Booking> findAllByItemOwner(User owner,
                                     Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner,
                                                              LocalDateTime start,
                                                              LocalDateTime end,
                                                              Pageable pageable);

    Page<Booking> findAllByItemOwnerAndEndBefore(User owner,
                                                 LocalDateTime end,
                                                 Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStartAfter(User owner,
                                                  LocalDateTime start,
                                                  Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStatusEquals(User owner,
                                                    Status status,
                                                    Pageable pageable);

    List<Booking> findTop1BookingByItemIdAndStartIsBeforeAndStatusIs(Long itemId,
                                                                     LocalDateTime end,
                                                                     Status status,
                                                                     Sort sort);

    List<Booking> findTop1BookingByItemIdAndStartIsAfterAndStatusIs(Long itemId,
                                                                    LocalDateTime end,
                                                                    Status status,
                                                                    Sort sort);
}