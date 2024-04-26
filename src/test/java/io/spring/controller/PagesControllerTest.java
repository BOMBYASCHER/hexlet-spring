package io.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.dto.page.PageUpdateDTO;
import io.spring.mapper.PageMapper;
import io.spring.model.Page;
import io.spring.repository.PageRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    private Page testPage;

    @BeforeEach
    public void setUp() {
        testPage = Instancio.of(Page.class)
                .ignore(Select.field(Page::getId))
                .ignore(Select.field(Page::getCreatedAt))
                .ignore(Select.field(Page::getUpdatedAt))
                .supply(Select.field(Page::getSlug), () -> faker.internet().slug())
                .supply(Select.field(Page::getName), () -> faker.internet().domainName())
                .supply(Select.field(Page::getBody), () -> faker.text().text(10, 1000))
                .create();
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/pages"))
                .andExpect(status().isOk());
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
                .andExpect(status().isOk());
        assertThat(pageRepository.findById(testPage.getId())).isNotPresent();
    }

    @Test
    public void testUpdate() throws Exception {
        pageRepository.save(testPage);

        var data = new PageUpdateDTO();
        data.setName("testdomainname");
        data.setBody(faker.text().text());

        var request = put("/pages/" + testPage.getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var page = pageRepository.findById(testPage.getId()).get();
        assertThat(page.getName()).isEqualTo((data.getName()));
        assertThat(page.getBody()).isEqualTo((data.getBody()));
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
