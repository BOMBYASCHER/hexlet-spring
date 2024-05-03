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
import io.spring.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.spring.model.User;
import io.spring.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUser()).create();
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
                user -> user.node("lastName").isEqualTo(testUser.getLastName())
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
                .andExpect(status().isNoContent());
        assertThat(userRepository.findById(testUser.getId())).isNotPresent();
    }

    @Test
    public void testUpdate() throws Exception {
        userRepository.save(testUser);

        var data = new UserUpdateDTO();
        data.setUsername(JsonNullable.of("username"));
        data.setEmail(JsonNullable.of("email@email.com"));
        data.setPassword(JsonNullable.of("password"));

        var request = put("/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var user = userRepository.findById(testUser.getId()).get();
        assertThat(user.getUsername()).isEqualTo(data.getUsername().get());
        assertThat(user.getEmail()).isEqualTo(data.getEmail().get());
        assertThat(user.getPassword()).isEqualTo(data.getPassword().get());
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
