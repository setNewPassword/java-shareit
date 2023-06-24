package ru.practicum.shareit.booking.model;

import lombok.Data;
import org.hibernate.annotations.Immutable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    private User booker;
    @Enumerated(EnumType.STRING)
    private Status status;
}
