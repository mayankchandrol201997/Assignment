package com.assignment.assignment.controller;

import com.assignment.assignment.dto.FetchUserRequestDto;
import com.assignment.assignment.dto.UserRequestDto;
import com.assignment.assignment.dto.UserResponseDto;
import com.assignment.assignment.model.User;
import com.assignment.assignment.service.UserService;
import com.assignment.assignment.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserServiceImpl userServiceImpl;

    @InjectMocks
    private UserController userController;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testCreateUser() throws Exception {
        UserResponseDto userResponseDto= mockUserResponseDto();
        UserRequestDto userRequestDto = mockUserRequestDto();
        when(userServiceImpl.createUser(any(UserRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(userResponseDto)));
    }

    @Test
    void testGetUserById() throws Exception {
        UserResponseDto userResponseDto= mockUserResponseDto();
        when(userServiceImpl.getUserById(1L)).thenReturn(userResponseDto);

        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(userResponseDto)));
    }

    @Test
    void testFetchUsersConcurrently() throws Exception {
        FetchUserRequestDto fetchUserRequestDto = new FetchUserRequestDto();
        fetchUserRequestDto.setId(Arrays.asList(1L, 2L));

        UserResponseDto user1 = mockUserResponseDto();
        UserResponseDto user2 = mockUserResponseDto();
        user2.setId(2L);

        List<UserResponseDto> users = Arrays.asList(user1, user2);

        when(userServiceImpl.fetchUsersConcurrently(fetchUserRequestDto.getId())).thenReturn(users);

        mockMvc.perform(get("/user/fetch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":[\"1\",\"2\"]}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(users)));
    }


    @Test
    void testGetAllUsers() throws Exception {
        UserResponseDto user1 = mockUserResponseDto();
        UserResponseDto user2 = mockUserResponseDto();
        user2.setId(2L);

        List<UserResponseDto> users = Arrays.asList(user1, user2);

        when(userServiceImpl.getAllUser()).thenReturn(users);

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(users)));
    }

    @Test
    void testDeleteUser() throws Exception {
        when(userServiceImpl.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserResponseDto userResponseDto= mockUserResponseDto();
        UserRequestDto userRequestDto = mockUserRequestDto();
        when(userServiceImpl.updateUser(any(UserRequestDto.class), any(Long.class))).thenReturn(userResponseDto);

        mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(userResponseDto)));
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
        user.setErrorCode("0");
        user.setErrorDescription("Your request has been processed.");
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
