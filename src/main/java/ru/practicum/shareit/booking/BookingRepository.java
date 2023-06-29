package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker(User booker,
                                  Sort sort);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long bookerId,
                                                                          Long itemId,
                                                                          Status status,
                                                                          LocalDateTime end);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker,
                                                           LocalDateTime start,
                                                           LocalDateTime end,
                                                           Sort sort);

    List<Booking> findAllByBookerAndEndBefore(User booker,
                                              LocalDateTime end,
                                              Sort sort);

    List<Booking> findAllByBookerAndStartAfter(User booker,
                                               LocalDateTime start,
                                               Sort sort);

    List<Booking> findAllByBookerAndStatusEquals(User booker,
                                                 Status status,
                                                 Sort sort);

    List<Booking> findAllByItemOwner(User owner,
                                     Sort sort);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner,
                                                              LocalDateTime start,
                                                              LocalDateTime end,
                                                              Sort sort);

    List<Booking> findAllByItemOwnerAndEndBefore(User owner,
                                                 LocalDateTime end,
                                                 Sort sort);

    List<Booking> findAllByItemOwnerAndStartAfter(User owner,
                                                  LocalDateTime start,
                                                  Sort sort);

    List<Booking> findAllByItemOwnerAndStatusEquals(User owner,
                                                    Status status,
                                                    Sort sort);

    List<Booking> findTop1BookingByItemIdAndStartIsBeforeAndStatusIs(Long itemId,
                                                                     LocalDateTime end,
                                                                     Status status,
                                                                     Sort sort);

    List<Booking> findTop1BookingByItemIdAndStartIsAfterAndStatusIs(Long itemId,
                                                                    LocalDateTime end,
                                                                    Status status,
                                                                    Sort sort);
}