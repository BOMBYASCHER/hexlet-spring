package io.spring.controller;

import io.spring.model.Page;
import io.spring.repository.PageRepository;
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
import java.util.Optional;

@RestController
@RequestMapping("/pages")
public class PageController {
    @Autowired
    private PageRepository pageRepository;

    @GetMapping
    ResponseEntity<List<Page>> index(@RequestParam(defaultValue = "10") Integer limit) {
        var result = pageRepository.findAll().stream().limit(limit).toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.size()))
                .body(result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Page create(@RequestBody Page page) {
        pageRepository.save(page);
        return page;
    }

    @GetMapping("/{slug}")
    Optional<Page> show(@PathVariable String slug) {
        return pageRepository.findAll().stream()
                .filter(page -> page.getSlug().equals(slug))
                .findFirst();
    }

    @PutMapping("/{slug}")
    Page update(@PathVariable String slug, @RequestBody Page data) {
        var optionalPage = pageRepository.findAll().stream()
                .filter(p -> p.getSlug().equals(slug))
                .findFirst();
        if (optionalPage.isPresent()) {
            var page = optionalPage.get();
            page.setName(data.getName());
            page.setSlug(data.getSlug());
            page.setBody(data.getBody());
            pageRepository.save(page);
        }
        return data;
    }

    @DeleteMapping("/{slug}")
    void delete(@PathVariable String slug) {
        var page = pageRepository.findAll().stream()
                .filter(p -> p.getSlug().equals(slug))
                .findFirst();
        pageRepository.deleteById(page.get().getId());
    }
}
