package pt.IPLeiria.event_bingo.controller;

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pt.IPLeiria.event_bingo.controllers.AuthController;
import pt.IPLeiria.event_bingo.dtos.users.UserRegisterDto;
import pt.IPLeiria.event_bingo.exeptions.BadRequestException;
import pt.IPLeiria.event_bingo.mapper.UserMapper;
import pt.IPLeiria.event_bingo.services.UserService;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
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
        var userRegister = new UserRegisterDto();
        userRegister.setUsername("test");
        userRegister.setFull_name("test");
        userRegister.setEmail("a@mail.com");
        userRegister.setPassword("test");
        userRegister.setAvatar(null);

        given(userService.register(ArgumentMatchers.any())).willReturn("<token>");

        ResultActions response = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegister)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("{\"token\":\"<token>\"}"));
    }

    @Test
    public void registerFailUser() throws Exception{
        var userRegister = new UserRegisterDto();
        userRegister.setUsername("test");
        userRegister.setFull_name("test");
        userRegister.setEmail("a@mail.com");
        userRegister.setPassword("test");

        given(userService.register(ArgumentMatchers.any())).willThrow(new  BadRequestException("User's email already exists!"));

        ResultActions response = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegister)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("{\"error\":\"User's email already exists!\"}"));
    }
}
