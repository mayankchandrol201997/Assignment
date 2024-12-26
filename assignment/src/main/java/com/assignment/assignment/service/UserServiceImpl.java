package com.assignment.assignment.service;

import com.assignment.assignment.dto.UserMapper;
import com.assignment.assignment.dto.UserRequestDto;
import com.assignment.assignment.dto.UserResponseDto;
import com.assignment.assignment.exception.IdNotFoundException;
import com.assignment.assignment.exception.UserNotFoundException;
import com.assignment.assignment.model.User;
import com.assignment.assignment.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RedisCacheService redisCacheService;
    private ObjectMapper objectMapper;
    private ExecutorService executorService;
    private UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, RedisCacheService redisCacheService, ObjectMapper objectMapper, ExecutorService executorService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.redisCacheService = redisCacheService;
        this.objectMapper = objectMapper;
        this.executorService = executorService;
        this.userMapper = userMapper;
    }

    @Override
    @Retryable(maxAttempts = 2, backoff = @Backoff(delay = 3000, multiplier = 1.5))
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = userMapper.toUser(userRequestDto);
        User savedUser = userRepository.save(user);
        System.out.println(savedUser.getEmail());
        UserResponseDto userResponseDto = userMapper.toUserResponseDto(savedUser);
        return userResponseDto;
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        String userJson = redisCacheService.getFromCache("USER", "user_" + id);
        User user;
        if (userJson != null) {
            try {
                user = objectMapper.readValue(userJson, User.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            user = userRepository.findById(id).orElseThrow(() -> new IdNotFoundException("User not found with id " + id));
            try {
                userJson = objectMapper.writeValueAsString(user);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            redisCacheService.updateToCache("USER", "user_" + id, userJson);
        }
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUser() {
        List<User> savedUser = userRepository.findAll();
        if (savedUser.isEmpty()) {
            throw new UserNotFoundException("No user found");
        }
        ;
        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        for (User user : savedUser) {
            userResponseDtos.add(userMapper.toUserResponseDto(user));
        }
        return userResponseDtos;
    }

    @Override
    public UserResponseDto updateUser(UserRequestDto userRequestDto, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IdNotFoundException("User not found with id " + id));
        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user = userRepository.save(user);
        redisCacheService.deleteFromCache("USER", "user_" + id);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public Boolean deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IdNotFoundException("User not found with id " + id));
        userRepository.delete(user);
        redisCacheService.deleteFromCache("USER", "user_" + id);
        return true;
    }

    public List<UserResponseDto> fetchUsersConcurrently(List<Long> ids) {
        List<Future<UserResponseDto>> futures = new ArrayList<>();
        for (Long id : ids) {
            Callable<UserResponseDto> task = () -> getUserById(id);
            futures.add(executorService.submit(task));
        }
        List<UserResponseDto> users = new ArrayList<>();
        for (Future<UserResponseDto> future : futures) {
            try {
                users.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return users;
    }
}
