package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    private User homerSimpson, stan;

    @BeforeEach
    public void beforeEach() {
        homerSimpson = User
                .builder()
                .name("Homer Simpson")
                .email("homer@springfield.net")
                .build();

        stan = User
                .builder()
                .name("Stanley Randall Marsh")
                .email("stan@southpark.net")
                .build();
    }

    @Test
    void findAllTest() {
        restTemplate.postForEntity("/users", homerSimpson, User.class);
        restTemplate.postForEntity("/users", stan, User.class);

        HttpHeaders headers = new HttpHeaders();
        User[] usersFromController = restTemplate
                .exchange("/users", HttpMethod.GET, new HttpEntity<>(headers),
                        User[].class).getBody();
        List<User> usersFromDB = userRepository.findAll();

        assert usersFromController != null;
        assertEquals(usersFromController.length, 2);
        assertEquals(usersFromDB.size(), 2);

        for (int i = 0; i < usersFromDB.size(); i++) {
            System.out.println(usersFromDB.get(i).getName() + usersFromDB.get(i).getEmail());
            assertEquals(usersFromController[i].getId(), usersFromDB.get(i).getId());
            assertEquals(usersFromController[i].getName(), usersFromDB.get(i).getName());
            assertEquals(usersFromController[i].getEmail(), usersFromDB.get(i).getEmail());
        }
    }

}