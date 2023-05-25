package pl.lotto.feature;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.lotto.BaseIntegrationTest;
import pl.lotto.domain.numbersgenerator.WinningNumberGenerable;

import java.util.Set;

class UserPlayedLottoAndWonIntegrationTest extends BaseIntegrationTest {

    @Autowired
    WinningNumberGenerable winningNumberGenerable;


    @Test
    void should_user_win_and_system_should_generate_winner() {
//        step 1: external service returns 6 random numbers (1,2,3,4,5,6)
        //given
        Set<Integer> winningRandomNumbers = winningNumberGenerable.generateWinningRandomNumbers();
        //when
        //then


//        step 2: system fetched winning numbers for draw date: 19.11.2022 12:00
//        step 3: user made POST /inputNumbers with 6 numbers (1, 2, 3, 4, 5, 6) at 16-11-2022 10:00 and system returned OK(200) with message: “success” and Ticket (DrawDate:19.11.2022 12:00 (Saturday), TicketId: sampleTicketId)
//        step 4: user made GET /results/notExistingId and system returned 404(NOT_FOUND) and body with (“message”: “Not found for id: notExistingId” and “status”: “NOT_FOUND”)
//        step 5: 3 days and 55 minutes passed, and it is 5 minute before draw (19.11.2022 11:55)
//        step 6: system generated result for TicketId: sampleTicketId with draw date 19.11.2022 12:00, and saved it with 6 hits
//        step 7: 6 minutes passed and it is 1 minute after the draw (19.11.2022 12:01)
//        step 8: user made GET /results/sampleTicketId and system returned 200 (OK)

    }
}