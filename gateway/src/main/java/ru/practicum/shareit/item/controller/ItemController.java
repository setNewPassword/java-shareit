package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@Valid @RequestBody ItemDto itemDto,
                                          @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("received a request to add Item");
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @NotNull @PathVariable Long itemId,
                                             @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("request to update Item with id: {}", itemId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@NotNull @PathVariable Long itemId,
                                          @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("request to get info for itemId: {}", itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "0") @Min(0) int from,
                                              @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("request to get list of things for userId: {}", userId);
        return itemClient.getAllItems(userId, PageRequest.of(from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestParam(defaultValue = "0") @Min(0) int from,
                                              @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("request to search a thing by description: {}", text);
        return itemClient.searchItems(text, PageRequest.of(from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(commentDto, itemId, userId);
    }

}