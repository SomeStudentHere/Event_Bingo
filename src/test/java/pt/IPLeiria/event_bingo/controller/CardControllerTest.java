package pt.IPLeiria.event_bingo.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pt.IPLeiria.event_bingo.entities.Card;
import pt.IPLeiria.event_bingo.entities.User;
import pt.IPLeiria.event_bingo.entities.enums.UserRole;
import pt.IPLeiria.event_bingo.repositories.CardRepository;
import pt.IPLeiria.event_bingo.services.CardService;


import java.util.List;

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

        when(cardRepository.findAll()).thenReturn(List.of(approved, notApproved));

        List<Card> result = cardService.list(admin);

        Assertions.assertEquals(2, result.size());
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
        notApproved.setApproved(false);

        when(cardRepository.findCardsByApprovedIs(true)).thenReturn(List.of(approved));

        List<Card> result = cardService.list(user);

        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.get(0).isApproved());
    }
}
