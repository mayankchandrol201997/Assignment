package com.assignment.assignment.controller;

import com.assignment.assignment.dto.FetchUserRequestDto;
import com.assignment.assignment.dto.UserRequestDto;
import com.assignment.assignment.dto.UserResponseDto;
import com.assignment.assignment.exception.InvalidRequestException;
import com.assignment.assignment.service.UserService;
import com.assignment.assignment.service.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
        validateUserRequestDto(userRequestDto);
        return ResponseEntity.ok(userServiceImpl.createUser(userRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userServiceImpl.getUserById(id));
    }

    @GetMapping("/fetch")
    public ResponseEntity<List<UserResponseDto>> fetchUsersConcurrently(@RequestBody FetchUserRequestDto fetchUserRequestDto) {
        if(null==fetchUserRequestDto||fetchUserRequestDto.getId().isEmpty())
            throw new InvalidRequestException("Invalid Request");
        return ResponseEntity.ok(userServiceImpl.fetchUsersConcurrently(fetchUserRequestDto.getId()));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userServiceImpl.getAllUser());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userServiceImpl.deleteUser(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable("id") Long id,@RequestBody UserRequestDto userRequestDto) {
        validateUserRequestDto(userRequestDto);
        return ResponseEntity.ok(userServiceImpl.updateUser(userRequestDto,id));
    }

    private void validateUserRequestDto(UserRequestDto userRequestDto) throws InvalidRequestException {
        if(userRequestDto==null||null==userRequestDto.getEmail()||userRequestDto.getName()==null
                ||userRequestDto.getEmail().isEmpty()||userRequestDto.getEmail().isBlank()||
                userRequestDto.getName().isEmpty()||userRequestDto.getName().isBlank())
        {
            throw new InvalidRequestException("Invalid request");
        }
    }
}
