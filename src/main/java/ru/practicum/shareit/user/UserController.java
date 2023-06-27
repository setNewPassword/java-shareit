package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        return userService.add(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") long userId,
                          @RequestBody UserDto userDto) {
        return userService.update(userDto, userId);
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable("id") long userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public List<UserDto> returnAll() {
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable("id") long userId) {
        userService.deleteById(userId);
    }
}