package io.spring.controller;

import io.spring.dto.page.PageCreateDTO;
import io.spring.dto.page.PageDTO;
import io.spring.dto.page.PageParamsDTO;
import io.spring.dto.page.PageUpdateDTO;
import io.spring.service.PageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pages")
public class PageController {

    @Autowired
    private PageService pageService;

    @GetMapping
    ResponseEntity<List<PageDTO>> index(PageParamsDTO params, @RequestParam(defaultValue = "10") Integer limit) {
        var pages = pageService.getAll(params, limit);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(pages.size()))
                .body(pages);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    PageDTO create(@Valid @RequestBody PageCreateDTO pageData) {
        return pageService.create(pageData);
    }

    @GetMapping("/{slug}")
    PageDTO show(@PathVariable String slug) {
        return pageService.get(slug);
    }

    @PutMapping("/{slug}")
    PageDTO update(@PathVariable String slug, @Valid @RequestBody PageUpdateDTO data) {
        return pageService.update(slug, data);
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable String slug) {
        pageService.delete(slug);
    }
}
