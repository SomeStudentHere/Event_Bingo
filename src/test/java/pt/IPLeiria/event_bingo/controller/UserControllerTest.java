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
import pt.IPLeiria.event_bingo.controllers.UserController;
import pt.IPLeiria.event_bingo.dtos.users.UserAllDto;
import pt.IPLeiria.event_bingo.dtos.users.UserDto;
import pt.IPLeiria.event_bingo.dtos.users.UserPatchDto;
import pt.IPLeiria.event_bingo.dtos.users.UserRegisterDto;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;
import pt.IPLeiria.event_bingo.entities.enums.UserStatus;
import pt.IPLeiria.event_bingo.mapper.UserMapper;
import pt.IPLeiria.event_bingo.security.JwtService;
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
    private JwtService jwtService;

    @MockitoBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    User user = User.builder()
            .id(0)
            .full_name("test")
            .username("test")
            .email("a@mail.com")
            .password("test")
            .balance(0)
            .role(UserRole.USER)
            .avatar(null)
            .cards(new ArrayList<>())
            .status(UserStatus.ACTIVE)
            .build();

    UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getFull_name(), user.getAvatar(), user.getRole());
    UserAllDto userAllDto = new UserAllDto(user.getId(), user.getFull_name(), user.getUsername(), user.getEmail(), user.getBalance(), user.getStatus(), user.getAvatar(), new ArrayList<>(), user.getRole());
    UserPatchDto userPatchDto = new UserPatchDto();
    UserRegisterDto userRegisterDto = new UserRegisterDto(user.getFull_name(), user.getUsername(), user.getEmail(), user.getPassword(), user.getAvatar());


    @Test
    public void endpointsWillReturnOkOnSuccess() throws Exception {
        given(userService.list()).willReturn(new ArrayList<>());
        given(userService.get(ArgumentMatchers.any())).willReturn(user);
        given(userService.update(ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(user);
        given(userService.patch(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(user);

        given(userService.findByUsername(ArgumentMatchers.any())).willReturn(user);

        given(userMapper.toDto(ArgumentMatchers.any())).willReturn(userDto);
        given(userMapper.toAllDto(ArgumentMatchers.any())).willReturn(userAllDto);

        given(jwtService.extractUserId(ArgumentMatchers.any())).willReturn(1L);

        ResultActions response = mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());

        response = mockMvc.perform(get("/users/" + user.getId())
                .header("Authorization", "<token>"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(userDto)));

        response = mockMvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDto)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(userDto)));

        response = mockMvc.perform(patch("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPatchDto)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(userAllDto)));

        response = mockMvc.perform(delete("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getUserRedirect()  throws Exception {
        given(userService.get(ArgumentMatchers.any())).willReturn(user);
        given(jwtService.extractUserId(ArgumentMatchers.any())).willReturn(user.getId());

        ResultActions response = mockMvc.perform(get("/users/" + user.getId())
                        .header("Authorization", "<token>"));

        response.andExpect(MockMvcResultMatchers.status().is(302));
    }
}
