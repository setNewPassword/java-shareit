package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("add item request");
        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findALLItemRequestByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("get all item requests of user and item requests answers");
        return itemRequestClient.findALLItemRequestByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestByUser(@RequestParam(defaultValue = "0") int from,
                                                          @RequestParam(defaultValue = "20") int size,
                                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("incorrect parameters");
        }
        return itemRequestClient.getAllItemRequestByUser(PageRequest.of(from, size), userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long requestId,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getItemRequestById(requestId, userId);
    }

}