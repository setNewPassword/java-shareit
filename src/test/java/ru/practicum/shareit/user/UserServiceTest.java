package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.impl.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private User homerSimpson;

    @BeforeEach
    public void beforeEach() {
        homerSimpson = User
                .builder()
                .id(1L)
                .name("Homer Simpson")
                .email("homer@springfield.net")
                .build();
    }

    @Test
    void addTest() {
        UserDto homerDto = UserDto
                .builder()
                .name(homerSimpson.getName())
                .email(homerSimpson.getEmail())
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(homerSimpson);

        UserDto newUser = userService.add(homerDto);

        assertEquals(1, newUser.getId());
        assertEquals(homerDto.getName(), newUser.getName());
        assertEquals(homerDto.getEmail(), newUser.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateTest() {
        homerSimpson.setName("Bart");
        UserDto inputDto = userMapper.toDto(homerSimpson);

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(homerSimpson));
        when(userRepository.save(any()))
                .thenReturn(homerSimpson);

        UserDto userDto = userService.update(inputDto, homerSimpson.getId());

        assertEquals(userDto.getId(), 1);
        assertEquals(userDto.getName(), inputDto.getName());
    }

    @Test
    void findByIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(homerSimpson));

        UserDto userDto = userService.getById(1);

        assertEquals(1, userDto.getId());

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void deleteByIdTest() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllTest() {
        when(userRepository.findAll())
                .thenReturn(List.of(homerSimpson));

        List<UserDto> users = userService.getAll();

        assertFalse(users.isEmpty());
        assertEquals(homerSimpson.getId(), users.get(0).getId());

        verify(userRepository, times(1)).findAll();
    }

}