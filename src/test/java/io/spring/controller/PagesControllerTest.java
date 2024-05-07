package io.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.dto.page.PageDTO;
import io.spring.dto.page.PageUpdateDTO;
import io.spring.mapper.PageMapper;
import io.spring.model.Page;
import io.spring.repository.PageRepository;
import io.spring.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PagesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final Faker faker = new Faker();

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PageMapper pageMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    private Page testPage;

    @BeforeEach
    public void setUp() {
        testPage = Instancio.of(modelGenerator.getPage()).create();
    }

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/pages"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).isArray();
    }

    @Test
    public void testIndexWithNameContains() throws Exception {
        testPage.setName("Begin");
        pageRepository.save(testPage);
        var response = mockMvc.perform(get("/pages?nameCont=begin"))
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString();
        var pageDTO = pageMapper.map(testPage);
        assertThatJson(response).isArray()
                .contains(pageDTO)
                .allSatisfy(element -> assertThatJson(element)
                        .and(v -> v.node("name").asString().containsIgnoringCase("begin"))
        );
    }

    @Test
    public void testIndexWithCreatedAtGreaterThan() throws Exception {
        pageRepository.save(testPage);
        testPage.setCreatedAt(LocalDate.parse("2024-02-02"));
        pageRepository.save(testPage);
        System.out.println(testPage.getCreatedAt());
        var result = mockMvc.perform(get("/pages?createdAtGt=2024-01-01"))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray()
                .allSatisfy((element) -> {
                    var createdAt = om.readValue(element.toString(), PageDTO.class).getCreatedAt();
                    assertThat(createdAt).isAfterOrEqualTo("2024-01-01");
                });
    }

    @Test
    public void testIndexWithCreatedAtLessThan() throws Exception {
        pageRepository.save(testPage);
        testPage.setCreatedAt(LocalDate.parse("2023-01-01"));
        pageRepository.save(testPage);
        var result = mockMvc.perform(get("/pages?createdAtLt=2024-01-01"))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray()
                .allSatisfy((element) -> {
                    var createdAt = om.readValue(element.toString(), PageDTO.class).getCreatedAt();
                    assertThat(createdAt).isBeforeOrEqualTo("2024-01-01");
                });
    }

    @Test
    public void testShow() throws Exception {
        pageRepository.save(testPage);
        var response = mockMvc.perform(get("/pages/" + testPage.getSlug()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).and(
                page -> page.node("name").isEqualTo(testPage.getName()),
                page -> page.node("body").isEqualTo(testPage.getBody()),
                page -> page.node("slug").isEqualTo(testPage.getSlug()),
                page -> page.node("id").isEqualTo(testPage.getId())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var pageCreateDTO = pageMapper.map(testPage);
        var request = post("/pages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(pageCreateDTO));
        var response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(pageRepository.findById(om.readValue(response, Page.class).getId())).isNotEmpty();
    }

    @Test
    public void testDelete() throws Exception {
        pageRepository.save(testPage);
        mockMvc.perform(delete("/pages/" + testPage.getSlug()))
                .andExpect(status().isNoContent());
        assertThat(pageRepository.findById(testPage.getId())).isNotPresent();
    }

    @Test
    public void testUpdate() throws Exception {
        pageRepository.save(testPage);
        var data = new PageUpdateDTO();
        data.setName(JsonNullable.of("testdomainname"));
        data.setBody(JsonNullable.of(faker.text().text(10, 1000)));

        var request = put("/pages/" + testPage.getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var page = pageRepository.findById(testPage.getId()).get();
        assertThatJson(page).and(
                p -> p.node("name").isEqualTo(data.getName()),
                p -> p.node("body").isEqualTo(data.getBody())
        );
    }

    @Test
    public void testUpdateWithGarbageData() throws Exception {
        pageRepository.save(testPage);
        var data = new HashMap();
        data.put("slug", "nonvalidslug");
        data.put("body", faker.text().text());
        data.put("name", faker.internet().domainName());

        var request = put("/pages/" + testPage.getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var product = pageRepository.findById(testPage.getId()).get();

        assertThat(product.getSlug()).isEqualTo(testPage.getSlug());
        assertThat(product.getBody()).isEqualTo(data.get("body"));
        assertThat(product.getName()).isEqualTo(data.get("name"));
    }
}
