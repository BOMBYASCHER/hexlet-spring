package io.spring.service;

import io.spring.dto.post.PostCreateDTO;
import io.spring.dto.post.PostDTO;
import io.spring.dto.post.PostParamsDTO;
import io.spring.dto.post.PostUpdateDTO;
import io.spring.exception.ResourceNotFoundException;
import io.spring.mapper.PostMapper;
import io.spring.repository.PostRepository;
import io.spring.specification.PostSpecification;
import io.spring.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private PostSpecification specBuilder;

    public List<PostDTO> getAll(PostParamsDTO params, int page) {
        var specs = specBuilder.build(params);
        var posts = postRepository.findAll(specs, PageRequest.of(page - 1, 10));
        return posts.map(postMapper::map).toList();
    }

    public PostDTO create(PostCreateDTO postData) {
        var post = postMapper.map(postData);
        post.setAuthor(userUtils.getCurrentUser());
        postRepository.save(post);
        return postMapper.map(post);
    }

    public PostDTO get(Long id) {
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));
        return postMapper.map(post);
    }

    public PostDTO update(PostUpdateDTO postData, Long id) {
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));
        postMapper.update(postData, post);
        postRepository.save(post);
        return postMapper.map(post);
    }

    public void delete(Long id) {
        postRepository.deleteById(id);
    }
}
