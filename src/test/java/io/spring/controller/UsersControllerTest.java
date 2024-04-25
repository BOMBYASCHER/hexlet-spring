package io.spring.controller;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

import io.spring.dto.user.UserUpdateDTO;
import io.spring.mapper.UserMapper;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.spring.model.User;
import io.spring.repository.UserRepository;
import net.datafaker.Faker;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final Faker faker = new Faker();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getUsername), () -> faker.name().username())
                .supply(Select.field(User::getPassword), () -> faker.internet().password())
                .create();
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    public void testShow() throws Exception {
        userRepository.save(testUser);
        var response = mockMvc.perform(get("/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).and(
                user -> user.node("id").isEqualTo(testUser.getId()),
                user -> user.node("username").isEqualTo(testUser.getUsername()),
                user -> user.node("firstName").isEqualTo(testUser.getFirstName()),
                user -> user.node("lastName").isEqualTo(testUser.getLastName()),
                user -> user.node("email").isEqualTo(testUser.getEmail()),
                user -> user.node("password").isEqualTo(testUser.getPassword())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var userCreateDTO = userMapper.map(testUser);
        var request = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userCreateDTO));
        var response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(userRepository.findById(om.readValue(response, User.class).getId())).isNotEmpty();
    }

    @Test
    public void testDelete() throws Exception {
        userRepository.save(testUser);
        mockMvc.perform(delete("/users/" + testUser.getId()))
                .andExpect(status().isOk());
        assertThat(userRepository.findById(testUser.getId())).isNotPresent();
    }

    @Test
    public void testUpdate() throws Exception {
        userRepository.save(testUser);

        var data = new UserUpdateDTO();
        data.setUsername("username");
        data.setEmail("email@email.com");
        data.setPassword("password");

        var request = put("/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var user = userRepository.findById(testUser.getId()).get();
        assertThat(user.getUsername()).isEqualTo((data.getUsername()));
        assertThat(user.getEmail()).isEqualTo((data.getEmail()));
        assertThat(user.getPassword()).isEqualTo((data.getPassword()));
    }

    @Test
    public void testUpdateWithGarbageData() throws Exception {
        userRepository.save(testUser);

        var data = new HashMap();
        data.put("username", "anotherusername");
        data.put("email", "anotheremail@email.com");
        data.put("password", "anotherpassword");
        data.put("firstName", "firstname");
        data.put("lastName", "lastname");

        var request = put("/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var user = userRepository.findById(testUser.getId()).get();
        assertThat(user.getUsername()).isEqualTo((data.get("username")));
        assertThat(user.getEmail()).isEqualTo((data.get("email")));
        assertThat(user.getPassword()).isEqualTo((data.get("password")));
        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(testUser.getLastName());
    }
}
