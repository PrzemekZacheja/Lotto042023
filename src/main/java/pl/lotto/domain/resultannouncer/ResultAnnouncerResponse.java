package pl.lotto.domain.resultannouncer;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
record ResultAnnouncerResponse(
        String ticketId,
        LocalDateTime drawDate,
        Set<Integer> numbersFromUser,
        Set<Integer> winnersNumbers,
        boolean isWinner,
        String message
) {
}