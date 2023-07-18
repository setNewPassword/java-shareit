package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ItemTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User homerSimpson;

    private Item screwdriver, hammer;

    @BeforeEach
    public void beforeEach() {
        screwdriver = new Item(null,
                "Phillips head screwdriver",
                "It has a head with pointed edges in the shape of a cross",
                true,
                null,
                null);
        hammer = new Item(null,
                "STANLEY FatMax Hammer",
                "It's reduces the effects of torque on wrists and elbows",
                true,
                null,
                null);

        homerSimpson = new User(null,
                "Homer Simpson",
                "homer@springfield.net");
    }

    @Test
    @DisplayName("Тест на добавление и получение всех предметов конкретного юзера")
    void findAllByOwnerIdTest() {
        ResponseEntity<User> responseUser = restTemplate.postForEntity("/users", homerSimpson, User.class);
        User newUser = userRepository.findById(Objects.requireNonNull(responseUser.getBody()).getId()).get();

        assertEquals(responseUser.getBody().getName(), homerSimpson.getName());
        assertEquals(responseUser.getBody().getEmail(), homerSimpson.getEmail());
        assertEquals(newUser.getId(), responseUser.getBody().getId());
        assertEquals(newUser.getName(), responseUser.getBody().getName());

        screwdriver.setOwner(responseUser.getBody());
        hammer.setOwner(responseUser.getBody());

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", responseUser.getBody().getId().toString());
        getPostResponseWithHeader(screwdriver, responseUser.getBody().getId().toString());
        getPostResponseWithHeader(hammer, responseUser.getBody().getId().toString());

        Item[] itemsFromController = restTemplate
                .exchange("/items", HttpMethod.GET, new HttpEntity<>(headers), Item[].class).getBody();

        List<Item> itemsFromDB = itemRepository
                .findAllByOwnerId(responseUser.getBody().getId(), PageRequest.of(0, 10));

        assert itemsFromController != null;
        assertEquals(itemsFromController.length, 2);
        assertEquals(itemsFromDB.size(), 2);

        for (int i = 0; i < itemsFromDB.size(); i++) {
            assertEquals(itemsFromController[i].getId(), itemsFromDB.get(i).getId());
            assertEquals(itemsFromController[i].getName(), itemsFromDB.get(i).getName());
            assertEquals(itemsFromController[i].getDescription(), itemsFromDB.get(i).getDescription());
        }
    }

    private void getPostResponseWithHeader(Item item, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId);
        HttpEntity<Item> entity = new HttpEntity<>(item, headers);
        restTemplate.exchange("/items", HttpMethod.POST, entity, Item.class);
    }

}