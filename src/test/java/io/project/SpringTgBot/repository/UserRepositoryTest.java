package io.project.SpringTgBot.repository;

import io.project.SpringTgBot.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private final String NAME = "user0";
    private final long ID = 0L;

    @Before
    public void init() {
        User user = new User();
        user.setId(ID);
        user.setFirstName(NAME);
        userRepository.save(user);
    }

    @Test
    public void save_ShouldSave() {
        User testUser = new User();
        testUser.setFirstName("user1");
        testUser.setId(1L);

        User user = userRepository.save(testUser);

        assertThat(user).isNotNull();
        assertEquals(testUser.getId(), user.getId());
        assertEquals(testUser.getFirstName(), user.getFirstName());
    }

    @Test
    public void getById_ShouldReturnUserById() {
        User expectedUser = userRepository.getUserById(ID);

        assertThat(expectedUser).isNotNull();
        assertEquals(expectedUser.getId(), ID);
        assertEquals(expectedUser.getFirstName(), NAME);
    }

    @Test
    public void existById_ShouldReturnTrue() {
        var expectTrue = userRepository.existsById(ID);

        assertTrue(expectTrue);
    }

    @Test
    public void existById_ShouldReturnFalse() {
        var expectFalse = userRepository.existsById(2L);

        assertFalse(expectFalse);
    }
}