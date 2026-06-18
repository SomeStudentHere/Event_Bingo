package pt.IPLeiria.event_bingo.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pt.IPLeiria.event_bingo.controllers.TransactionController;
import pt.IPLeiria.event_bingo.dtos.transactions.AdminTransactionDto;
import pt.IPLeiria.event_bingo.dtos.transactions.MoneyDto;
import pt.IPLeiria.event_bingo.dtos.transactions.TransactionDto;
import pt.IPLeiria.event_bingo.entities.Transaction;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.TransactionType;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;
import pt.IPLeiria.event_bingo.mapper.TransactionMapper;
import pt.IPLeiria.event_bingo.repositories.TransactionRepository;
import pt.IPLeiria.event_bingo.security.JwtAuthFilter;
import pt.IPLeiria.event_bingo.security.JwtService;
import pt.IPLeiria.event_bingo.services.LogBufferService;
import pt.IPLeiria.event_bingo.services.TransactionService;
import pt.IPLeiria.event_bingo.services.UserService;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private TransactionMapper transactionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private LogBufferService  logBufferService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private TransactionRepository transactionRepository;

    @Test
    public void willReturnSuccessfulMessage() throws Exception{
        MoneyDto moneyDto = new MoneyDto(TransactionType.DEPOSIT, 10d,
                "0000 0000 0000 0000", "02/30",
                "Test", "123");


        ResultActions response = mockMvc.perform(post("/transactions")
                .header("Authorization", "<token>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moneyDto)));

        response.andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"message\":\"Deposit successful\"}"));
    }

    @Test
    void adminShouldSeeAllTransactions() throws Exception {
        User admin = User.builder()
                .id(1)
                .role(UserRole.ADMIN)
                .build();

        Transaction dto1 = new Transaction();
        Transaction dto2 = new Transaction();

        var page = new PageImpl(List.of(dto1, dto2));

        given(transactionService.list(any(), any())).willReturn(page);

        mockMvc.perform(get("/transactions")
                        .principal(new UsernamePasswordAuthenticationToken(
                                admin,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void userShouldSeeOnlyOwnTransactions() throws Exception {

        User user = User.builder()
                .id(2)
                .role(UserRole.USER)
                .build();

        User otherUser = User.builder()
                .id(3)
                .role(UserRole.USER)
                .build();

        Transaction t1 = new Transaction();
        t1.setId(1);
        t1.setUser(user);

        Transaction t2 = new Transaction();
        t2.setId(2);
        t2.setUser(otherUser);

        Transaction userOnlyTransaction = new Transaction();
        userOnlyTransaction.setId(3);
        userOnlyTransaction.setUser(user);

        var page = new PageImpl(List.of(userOnlyTransaction));

        given(transactionService.list(any(), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/transactions")
                        .principal(new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }
}
