package io.spring.service;

import io.spring.dto.user.UserCreateDTO;
import io.spring.dto.user.UserDTO;
import io.spring.dto.user.UserUpdateDTO;
import io.spring.exception.ResourceAlreadyExistsException;
import io.spring.exception.ResourceNotFoundException;
import io.spring.mapper.UserMapper;
import io.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public List<UserDTO> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO get(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        return userMapper.map(user);
    }

    public UserDTO create(UserCreateDTO createDTO) {
        var user = userMapper.map(createDTO);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException("User with email '" + user.getEmail() + "' already exists");
        }
        return userMapper.map(user);
    }

    public UserDTO update(Long userId, UserUpdateDTO updateDTO) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        try {
            userMapper.update(updateDTO, user);
            userRepository.save(user);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException("User with email '" + user.getEmail() + "' already exists");
        }
        return userMapper.map(user);
    }

    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
