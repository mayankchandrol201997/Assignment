package com.assignment.assignment.dto;

import com.assignment.assignment.model.User;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class UserMapper {
    public User toUser(UserRequestDto userRequestDto)
    {
        User user = new User();
        user.setEmail(userRequestDto.getEmail());
        user.setName(userRequestDto.getName());
        return user;
    }

    public UserResponseDto toUserResponseDto(User user)
    {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setName(user.getName());
        userResponseDto.setErrorCode("0");
        userResponseDto.setErrorDescription("your request has been processed.");
        return userResponseDto;
    }
}
