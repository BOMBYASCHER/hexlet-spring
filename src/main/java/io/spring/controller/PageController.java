package io.spring.controller;

import io.spring.dto.page.PageCreateDTO;
import io.spring.dto.page.PageDTO;
import io.spring.dto.page.PageUpdateDTO;
import io.spring.exception.ResourceAlreadyExistsException;
import io.spring.exception.ResourceNotFoundException;
import io.spring.mapper.PageMapper;
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

    @Autowired
    private PageMapper pageMapper;

    @GetMapping
    ResponseEntity<List<PageDTO>> index(@RequestParam(defaultValue = "10") Integer limit) {
        var result = pageRepository.findAll(PageRequest.of(0, limit))
                .stream()
                .map(page -> pageMapper.map(page))
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.size()))
                .body(result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    PageDTO create(@RequestBody PageCreateDTO pageData) {
        var page = pageMapper.map(pageData);
        try {
            pageRepository.save(page);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException("Page with slug '" + page.getSlug() + "' already exists");
        }
        return pageMapper.map(page);
    }

    @GetMapping("/{slug}")
    PageDTO show(@PathVariable String slug) {
        var page = pageRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Page with slug '" + slug + "' not found"));
        return pageMapper.map(page);
    }

    @PutMapping("/{slug}")
    PageDTO update(@PathVariable String slug, @RequestBody PageUpdateDTO data) {
        var page = pageRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Page with slug '" + slug + "' not found"));
        try {
            pageMapper.update(data, page);
            pageRepository.save(page);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException("Page with slug '" + page.getSlug() + "' already exists");
        }
        return pageMapper.map(page);
    }

    @DeleteMapping("/{slug}")
    void delete(@PathVariable String slug) {
        var page = pageRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Page with slug '" + slug + "' not found"));
        pageRepository.delete(page);
    }
}
