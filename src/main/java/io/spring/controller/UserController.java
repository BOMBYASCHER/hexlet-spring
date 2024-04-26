package io.spring.controller;

import io.spring.dto.user.UserCreateDTO;
import io.spring.dto.user.UserDTO;
import io.spring.dto.user.UserUpdateDTO;
import io.spring.exception.ResourceAlreadyExistsException;
import io.spring.exception.ResourceNotFoundException;
import io.spring.mapper.UserMapper;
import io.spring.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;
    @GetMapping
    List<UserDTO> index() {
        return userRepository.findAll()
                .stream()
                .map(user -> userMapper.map(user))
                .toList();
    }

    @GetMapping("/{id}")
    UserDTO show(@PathVariable Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.map(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDTO create(@Valid @RequestBody UserCreateDTO userDTO) {
        var user = userMapper.map(userDTO);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException("User with email '" + user.getEmail() + "' already exists");
        }
        return userMapper.map(user);
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO data) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        try {
            userMapper.update(data, user);
            userRepository.save(user);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException("User with email '" + user.getEmail() + "' already exists");
        }
        return userMapper.map(user);
    }
}
