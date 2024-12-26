package com.assignment.assignment.service;

import com.assignment.assignment.dto.UserMapper;
import com.assignment.assignment.dto.UserRequestDto;
import com.assignment.assignment.dto.UserResponseDto;
import com.assignment.assignment.exception.IdNotFoundException;
import com.assignment.assignment.exception.UserNotFoundException;
import com.assignment.assignment.model.User;
import com.assignment.assignment.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisCacheService redisCacheService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ExecutorService executorService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userRequestDto = mockUserRequestDto();
        userResponseDto = mockUserResponseDto();
        user = mockUser();
    }

    @Test
    public void testCreateUser() {
        when(userMapper.toUser(userRequestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.createUser(userRequestDto);

        assertNotNull(result);
        assertEquals(userResponseDto, result);
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John", result.getName());
    }

    @Test
    public void testGetUserById_UserFound() {
        // Arrange
        when(redisCacheService.getFromCache("USER", "user_1")).thenReturn(null); // No cache
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(userResponseDto);

        // Act
        UserResponseDto result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(userResponseDto, result);
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John", result.getName());
    }

    @Test
    public void testGetUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IdNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    public void testGetAllUser_UsersFound() {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toUserResponseDto(user)).thenReturn(userResponseDto);

        List<UserResponseDto> result = userService.getAllUser();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john@example.com", result.get(0).getEmail());
        assertEquals("John", result.get(0).getName());
    }

    @Test
    public void testGetAllUser_NoUsersFound() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        assertThrows(UserNotFoundException.class, () -> userService.getAllUser());
    }

    @Test
    public void testUpdateUser() {
        UserRequestDto updatedUserRequest = new UserRequestDto();
        userRequestDto.setName("John");
        userRequestDto.setEmail("john@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.updateUser(updatedUserRequest, 1L);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John", result.getName());
    }

    @Test
    public void testDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testFetchUsersConcurrently() throws Exception {
        List<Long> ids = List.of(1L, 2L, 3L);

        Future<UserResponseDto> mockFuture = mock(Future.class);
        when(executorService.submit(any(Callable.class))).thenReturn(mockFuture);
        when(mockFuture.get()).thenReturn(userResponseDto);

        List<UserResponseDto> result = userService.fetchUsersConcurrently(ids);

        assertNotNull(result);
        assertEquals(3, result.size()); // We expect 3 users to be fetched
    }

    private User mockUser()
    {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");
        return user;
    }
    private UserResponseDto mockUserResponseDto()
    {
        UserResponseDto user = new UserResponseDto();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");
        return user;
    }
    private UserRequestDto mockUserRequestDto()
    {
        UserRequestDto user = new UserRequestDto();
        user.setName("John");
        user.setEmail("john@example.com");
        return user;
    }
}
