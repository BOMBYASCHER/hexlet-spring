package io.spring.service;


import io.spring.dto.page.PageCreateDTO;
import io.spring.dto.page.PageDTO;
import io.spring.dto.page.PageParamsDTO;
import io.spring.dto.page.PageUpdateDTO;
import io.spring.exception.ResourceAlreadyExistsException;
import io.spring.exception.ResourceNotFoundException;
import io.spring.mapper.PageMapper;
import io.spring.repository.PageRepository;
import io.spring.specification.PageSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageService {
    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private PageMapper pageMapper;

    @Autowired
    private PageSpecification specBuilder;

    public List<PageDTO> getAll(PageParamsDTO params, int limit) {
        var specs = specBuilder.build(params);
        return pageRepository.findAll(specs, PageRequest.of(0, limit))
                .map(pageMapper::map)
                .toList();
    }

    public PageDTO create(PageCreateDTO data) {
        var page = pageMapper.map(data);
        try {
            pageRepository.save(page);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException("Page with slug '" + page.getSlug() + "' already exists");
        }
        return pageMapper.map(page);
    }

    public PageDTO get(String slug) {
        var page = pageRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Page with slug '" + slug + "' not found"));
        return pageMapper.map(page);
    }

    public PageDTO update(String slug, PageUpdateDTO data) {
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

    public void delete(String slug) {
        var page = pageRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Page with slug '" + slug + "' not found"));
        pageRepository.delete(page);
    }
}
