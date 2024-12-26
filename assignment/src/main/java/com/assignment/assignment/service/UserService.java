package com.assignment.assignment.service;

import com.assignment.assignment.dto.UserRequestDto;
import com.assignment.assignment.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);
    UserResponseDto getUserById(Long id);
    List<UserResponseDto> getAllUser();
    UserResponseDto updateUser(UserRequestDto userRequestDto,Long id);
    Boolean deleteUser(Long id);
    List<UserResponseDto> fetchUsersConcurrently(List<Long> id);
}
