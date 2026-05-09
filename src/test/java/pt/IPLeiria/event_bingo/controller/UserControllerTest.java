package pt.IPLeiria.event_bingo.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pt.IPLeiria.event_bingo.controllers.UserController;
import pt.IPLeiria.event_bingo.dtos.users.UserDto;
import pt.IPLeiria.event_bingo.dtos.users.UserRegisterDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;
import pt.IPLeiria.event_bingo.mapper.UserMapper;
import pt.IPLeiria.event_bingo.services.UserService;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void registerUser() throws Exception{
        var user =  User.builder()
                .username("test")
                .full_name("test")
                .email("a@mail.com")
                .password("test")
                .status(UserStatus.ACTIVE)
                .balance(0f)
                .cards(new ArrayList<>())
                .build();

        var userRegister = new UserRegisterDto();
        userRegister.setUsername(user.getUsername());
        userRegister.setFull_name(user.getFull_name());
        userRegister.setEmail(user.getEmail());
        userRegister.setPassword(user.getPassword());

        var userDto = new UserDto(0l, user.getUsername(), user.getFull_name());

        given(userService.create(ArgumentMatchers.any())).willReturn(user);
        given(userMapper.toEntity(ArgumentMatchers.any())).willReturn(user);
        given(userMapper.toDto(ArgumentMatchers.any())).willReturn(userDto);

        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegister)));

        response.andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
