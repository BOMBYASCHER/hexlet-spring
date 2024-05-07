package io.spring.controller;

import io.spring.dto.post.PostCreateDTO;
import io.spring.dto.post.PostDTO;
import io.spring.dto.post.PostParamsDTO;
import io.spring.dto.post.PostUpdateDTO;
import io.spring.exception.ResourceNotFoundException;
import io.spring.mapper.PostMapper;
import io.spring.repository.PostRepository;
import io.spring.specification.PostSpecification;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostSpecification specBuilder;

    @GetMapping
    public List<PostDTO> index(PostParamsDTO params, @RequestParam(defaultValue = "1") int page) {
        var spec = specBuilder.build(params);
        var posts = postRepository.findAll(spec, PageRequest.of(page - 1, 10));
        return posts.map(postMapper::map).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostDTO show(@PathVariable Long id) {
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        return postMapper.map(post);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDTO create(@RequestBody PostCreateDTO postData) {
        var post = postMapper.map(postData);
        postRepository.save(post);
        return postMapper.map(post);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostDTO update(@RequestBody @Valid PostUpdateDTO postData, @PathVariable Long id) {
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));
        postMapper.update(postData, post);
        postRepository.save(post);
        return postMapper.map(post);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        postRepository.deleteById(id);
    }
}
