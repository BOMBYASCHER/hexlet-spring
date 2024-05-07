package io.spring.controller;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.spring.repository.UserRepository;
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

import io.spring.model.Post;

import io.spring.dto.post.PostUpdateDTO;
import io.spring.mapper.PostMapper;
import io.spring.repository.PostRepository;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
public class PostsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private Post testPost;

    @BeforeEach
    public void setUp() {
        var user = Instancio.of(modelGenerator.getUser()).create();
        userRepository.save(user);
        testPost = Instancio.of(modelGenerator.getPost()).create();
        testPost.setAuthor(user);
    }

    @Test
    public void testIndex() throws Exception {
        postRepository.save(testPost);

        var result = mockMvc.perform(get("/posts"))
                .andExpect(status()
                        .isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testIndexWithNameContains() throws Exception {
        testPost.setName("Lamp");
        postRepository.save(testPost);
        var result = mockMvc.perform(get("/posts?nameCont=lamp"))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        var postDTO = postMapper.map(testPost);
        assertThatJson(body).isArray()
                .contains(postDTO)
                .allSatisfy(element -> assertThatJson(element)
                        .and(v -> v.node("name").asString().containsIgnoringCase("lamp"))
        );
    }

    @Test
    public void testIndexWithAuthorId() throws Exception {
        postRepository.save(testPost);
        System.out.println(testPost.getId());
        var result = mockMvc.perform(get("/posts?authorId=" + testPost.getAuthor().getId()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        var postDTO = postMapper.map(testPost);
        assertThatJson(body).isArray()
                .contains(postDTO)
                .allSatisfy(element -> assertThatJson(element)
                        .and(v -> v.node("authorId").isEqualTo(testPost.getId()))
        );
    }

    @Test
    public void testIndexWithCreatedAtGreaterThan() throws Exception {
        postRepository.save(testPost);
        testPost.setCreatedAt(LocalDate.parse("2024-02-02"));
        postRepository.save(testPost);
        var result = mockMvc.perform(get("/posts?createdAtGt=2024-01-01"))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        var postDTO = postMapper.map(testPost);
        assertThatJson(body).isArray()
                .contains(postDTO)
                .allSatisfy(element -> {
                    var createdAt = om.readValue(element.toString(), Post.class).getCreatedAt();
                    assertThat(createdAt).isAfterOrEqualTo("2024-01-01");
                });
    }

    @Test
    public void testIndexWithCreatedAtLessThan() throws Exception {
        postRepository.save(testPost);
        testPost.setCreatedAt(LocalDate.parse("2023-01-01"));
        postRepository.save(testPost);
        var result = mockMvc.perform(get("/posts?createdAtLt=2024-01-01"))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        var postDTO = postMapper.map(testPost);
        assertThatJson(body).isArray()
                .contains(postDTO)
                .allSatisfy(element -> {
                    var createdAt = om.readValue(element.toString(), Post.class).getCreatedAt();
                    assertThat(createdAt).isBeforeOrEqualTo("2024-01-01");
                });
    }

    @Test
    public void testIndexComplexCondition() throws Exception {
        postRepository.save(testPost);
        testPost.setName("Default");
        testPost.setCreatedAt(LocalDate.parse("2024-01-02"));
        postRepository.save(testPost);
        var authorId = testPost.getAuthor().getId().intValue();
        var request = get("/posts"
                + "?nameCont=default"
                + "&authorId=" + authorId
                + "&createdAtGt=2024-01-01"
                + "&createdAtLt=2024-01-03"
        );
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        var postDTO = postMapper.map(testPost);
        System.out.println(body);
        assertThatJson(body).isArray()
                .contains(postDTO)
                .allSatisfy(element -> assertThatJson(element)
                        .and(v -> v.node("name").asString().containsIgnoringCase("deFault"))
                        .and(v -> v.node("authorId").isEqualTo(authorId))
        );
    }

    @Test
    public void testCreate() throws Exception {
        var dto = postMapper.map(testPost);

        var request = post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var post = postRepository.findBySlug(dto.getSlug()).get();
        assertNotNull(post);
        assertThat(post.getName()).isEqualTo(dto.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        postRepository.save(testPost);

        var dto = new PostUpdateDTO();
        dto.setName(JsonNullable.of("new name"));

        var request = put("/posts/" + testPost.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var post = postRepository.findById(testPost.getId()).get();
        assertThat(post.getName()).isEqualTo(dto.getName().get());
    }

    @Test
    public void testShow() throws Exception {
        postRepository.save(testPost);

        var request = get("/posts/" + testPost.getId());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("slug").isEqualTo(testPost.getSlug()),
                v -> v.node("name").isEqualTo(testPost.getName()),
                v -> v.node("body").isEqualTo(testPost.getBody())
        );
    }

    @Test
    public void testDestroy() throws Exception {
        postRepository.save(testPost);
        var request = delete("/posts/" + testPost.getId());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(postRepository.existsById(testPost.getId())).isEqualTo(false);
    }
}
