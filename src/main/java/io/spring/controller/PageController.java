package io.spring.controller;

import io.spring.exception.ResourceAlreadyExistsException;
import io.spring.exception.ResourceNotFoundException;
import io.spring.model.Page;
import io.spring.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
    private PageRepository pageRepository;

    @GetMapping
    ResponseEntity<List<Page>> index(@RequestParam(defaultValue = "10") Integer limit) {
        var result = pageRepository.findAll(PageRequest.of(0, limit));
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.getSize()))
                .body(result.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Page create(@RequestBody Page page) {
        try {
            pageRepository.save(page);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException("Page with slug '" + page.getSlug() + "' already exists");
        }
        return page;
    }

    @GetMapping("/{slug}")
    Page show(@PathVariable String slug) {
        return pageRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Page with slug '" + slug + "' not found"));
    }

    @PutMapping("/{slug}")
    Page update(@PathVariable String slug, @RequestBody Page data) {
        var page = pageRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Page with slug '" + slug + "' not found"));
        try {
            page.setName(data.getName());
            page.setSlug(data.getSlug());
            page.setBody(data.getBody());
            pageRepository.save(page);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException("Page with slug '" + page.getSlug() + "' already exists");
        }
        return data;
    }

    @DeleteMapping("/{slug}")
    void delete(@PathVariable String slug) {
        var page = pageRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Page with slug '" + slug + "' not found"));
        pageRepository.delete(page);
    }
}
