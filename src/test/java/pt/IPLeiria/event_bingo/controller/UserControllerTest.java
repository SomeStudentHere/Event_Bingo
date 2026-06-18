package pt.IPLeiria.event_bingo.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import pt.IPLeiria.event_bingo.security.JwtAuthFilter;
import pt.IPLeiria.event_bingo.security.JwtService;
import pt.IPLeiria.event_bingo.services.LogBufferService;
import pt.IPLeiria.event_bingo.services.UserService;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc//(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private LogBufferService logBufferService;

    @MockitoBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

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


    //todo
    @Test
    public void endpointsWillReturnOkOnSuccess() throws Exception {

        var page = new PageImpl(List.of());

        given(userService.list(any())).willReturn(page);
        given(userService.get(any())).willReturn(user);
        given(userService.update(any(), any(), any())).willReturn(user);
        given(userService.patch(any(), any(), any())).willReturn(user);

        given(userService.findByUsername(any())).willReturn(user);

        given(userMapper.toDto(any())).willReturn(userDto);
        given(userMapper.toAllDto(any())).willReturn(userAllDto);

        given(jwtService.extractUserId(any())).willReturn(1L);

        ResultActions response = mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());

        response = mockMvc.perform(get("/users/" + user.getId()));

        response.andExpect(status().isOk());

        response = mockMvc.perform(put("/users/" + user.getId())
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ADMIN")))
                ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDto)));

        response.andExpect(status().isOk());

        response = mockMvc.perform(patch("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPatchDto)));

        response.andExpect(status().isOk());

        response = mockMvc.perform(delete("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());
    }

    //todo to fix
    /*
    @Test
    public void getUserRedirect()  throws Exception {
        given(userService.get(any())).willReturn(user);
        given(jwtService.extractUserId(any())).willReturn(user.getId());

        ResultActions response = mockMvc.perform(get("/users/" + user.getId())
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("User")))
                )));

        response.andExpect(status().is(302));
    }*/

    //todo
    /*
    @Test
    void adminShouldSeeAllUsers() throws Exception {
        User admin = User.builder()
                .id(0)
                .role(UserRole.ADMIN)
                .build();

        User userActive = User.builder()
                .id(1)
                .status(UserStatus.ACTIVE)
                .build();

        User userSuspended = User.builder()
                .id(2)
                .status(UserStatus.SUSPENDED)
                .build();

        User userDeleted = User.builder()
                .id(2)
                .status(UserStatus.DELETED)
                .build();

        given(userService.listAll()).willReturn(List.of(userActive, userSuspended, userDeleted));

        given(userMapper.toAllDto(userActive))
                .willReturn(new UserAllDto(
                        userActive.getId(),
                        userActive.getFull_name(),
                        userActive.getUsername(),
                        userActive.getEmail(),
                        userActive.getBalance(),
                        userActive.getStatus(),
                        userActive.getAvatar(),
                        new ArrayList<>(),
                        userActive.getRole()
                ));

        given(userMapper.toAllDto(userSuspended))
                .willReturn(new UserAllDto(
                        userSuspended.getId(),
                        userSuspended.getFull_name(),
                        userSuspended.getUsername(),
                        userSuspended.getEmail(),
                        userSuspended.getBalance(),
                        userSuspended.getStatus(),
                        userSuspended.getAvatar(),
                        new ArrayList<>(),
                        userSuspended.getRole()
                ));

        given(userMapper.toAllDto(userDeleted))
                .willReturn(new UserAllDto(
                        userSuspended.getId(),
                        userSuspended.getFull_name(),
                        userSuspended.getUsername(),
                        userSuspended.getEmail(),
                        userSuspended.getBalance(),
                        userSuspended.getStatus(),
                        userSuspended.getAvatar(),
                        new ArrayList<>(),
                        userSuspended.getRole()
                ));

        mockMvc.perform(get("/users")
                        .principal(new UsernamePasswordAuthenticationToken(
                                admin,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }


    @Test
    void userShouldSeeOnlyActiveUsers() throws Exception {

        User principalUser = User.builder()
                .id(0)
                .role(UserRole.USER)
                .build();

        User userActive1 = User.builder()
                .id(1)
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();

        User userActive2 = User.builder()
                .id(2)
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();

        User userSuspended = User.builder()
                .id(3)
                .role(UserRole.USER)
                .status(UserStatus.SUSPENDED)
                .build();

        given(userService.list())
                .willReturn(List.of(userActive1, userActive2));

        given(userMapper.toDto(userActive1))
                .willReturn(new UserDto(
                        userActive1.getId(),
                        userActive1.getUsername(),
                        userActive1.getFull_name(),
                        userActive1.getAvatar(),
                        userActive1.getRole()
                ));

        given(userMapper.toDto(userActive2))
                .willReturn(new UserDto(
                        userActive2.getId(),
                        userActive2.getUsername(),
                        userActive2.getFull_name(),
                        userActive2.getAvatar(),
                        userActive2.getRole()
                ));

        given(userMapper.toDto(userSuspended))
                .willReturn(new UserDto(
                        userSuspended.getId(),
                        userSuspended.getUsername(),
                        userSuspended.getFull_name(),
                        userSuspended.getAvatar(),
                        userSuspended.getRole()
                ));

        mockMvc.perform(get("/users")
                        .principal(new UsernamePasswordAuthenticationToken(
                                principalUser,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
     */
}
