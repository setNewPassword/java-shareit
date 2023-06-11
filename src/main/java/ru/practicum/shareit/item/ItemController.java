package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody @Valid ItemDto itemDto) {
        Item item = itemMapper.toEntity(itemDto);
        User user = userService.getById(userId);
        item = item.toBuilder()
                .owner(user)
                .build();
        item = itemService.add(item);
        return itemMapper.toDto(item);
    }

    @PatchMapping("/{id}")
    public ItemDto changeInfo(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable("id") long itemId,
                              @RequestBody ItemDto itemDto) {
        User user = userService.getById(userId);
        Item item = itemMapper.toEntity(itemDto);
        item = item.toBuilder()
                .id(itemId)
                .owner(user)
                .build();
        item = itemService.update(item);
        return itemMapper.toDto(item);
    }

    @GetMapping
    public List<ItemDto> returnUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemsByUserId(userId)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemDto returnById(@PathVariable("id") long itemId) {
        Item item = itemService.getById(itemId);
        return itemMapper.toDto(item);
    }

    @GetMapping("/search")
    public List<ItemDto> returnByQuery(@RequestParam("text") String query) {
        return itemService.getByQuery(query)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }
}
