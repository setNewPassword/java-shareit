package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

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