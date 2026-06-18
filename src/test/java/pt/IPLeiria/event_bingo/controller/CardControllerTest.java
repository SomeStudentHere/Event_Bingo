package pt.IPLeiria.event_bingo.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pt.IPLeiria.event_bingo.entities.Card;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;
import pt.IPLeiria.event_bingo.repositories.CardRepository;
import pt.IPLeiria.event_bingo.services.CardService;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardControllerTest {
    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    void adminShouldSeeAllCards() {
        User admin = User.builder()
                .id(1)
                .role(UserRole.ADMIN)
                .build();

        Card approved = new Card();
        approved.setId(1);
        approved.setApproved(true);

        Card notApproved = new Card();
        notApproved.setId(2);
        notApproved.setApproved(false);

        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl(List.of(approved, notApproved));

        when(cardRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Card> result = cardService.list(admin, pageable);

        Assertions.assertEquals(2, result.getContent().size());
    }

    @Test
    void notAdminShouldSeeOnlyApprovedCards() {
        User user = User.builder()
                .id(2)
                .role(UserRole.USER)
                .build();

        Card approved = new Card();
        approved.setId(1);
        approved.setApproved(true);

        Card notApproved = new Card();
        notApproved.setId(2);
        var page = new PageImpl(List.of(approved));
        notApproved.setApproved(false);

        when(cardRepository.findCardsByApprovedIs(eq(true), any(Pageable.class))).thenReturn(page);

        var pageable = PageRequest.of(0, 10);

        Page<Card> result = cardService.list(user, pageable);

        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertTrue(result.getContent().get(0).isApproved());
    }
}
