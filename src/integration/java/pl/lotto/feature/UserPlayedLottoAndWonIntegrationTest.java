package pl.lotto.feature;

import com.github.tomakehurst.wiremock.client.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.test.web.servlet.*;
import pl.lotto.*;
import pl.lotto.domain.numbersgenerator.*;
import pl.lotto.domain.numbersreceiver.dto.*;

import java.time.*;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserPlayedLottoAndWonIntegrationTest extends BaseIntegrationTest {

    @Autowired
    NumbersGeneratorFacade numbersGeneratorFacade;


    @Test
    void should_user_win_and_system_should_generate_winner() throws Exception {
//        step 1: external service returns 6 random numbers (1,2,3,4,5,6)
        //given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=25").willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value()).withHeader("Content-Type", "application/json").withBody("[43, 5, 80, 14, 62, 31, 99, 51, 22, 40, 4, 75, 71, 31, 30, 66, 64, 53, 78, 72, 68, 4, 62, 70, 84]")));
        //when
        numbersGeneratorFacade.generateSixNumbers();


//        step 2: system fetched winning numbers for draw date: 19.11.2022 10:00
        //given
        LocalDateTime drawDate = LocalDateTime.of(2022, 11, 26, 12, 0, 0);
        //when & then
        await().atMost(Duration.ofSeconds(20)).pollInterval(Duration.ofSeconds(1)).until(() -> {
            try {
                return !numbersGeneratorFacade.retrieveAllWinnerNumbersByNextDrawDate(drawDate).winningNumbers().isEmpty();

            } catch (WinningNunmbersNotFoundExeption exception) {
                return false;
            }
        });


//        step 3: user made POST /inputNumbers with 6 numbers (1, 2, 3, 4, 5, 6) at 16-11-2022 10:00 and system returned OK(200) with message:
//        “success” and Ticket (DrawDate:19.11.2022 12:00 (Saturday), TicketId: sampleTicketId)
        //given
        //when
        ResultActions perform = mockMvc.perform(post("/inputNumbers").content("""
                {
                "inputNumbers" : [1, 2, 3, 4, 5, 6]
                }
                """.trim()).contentType(MediaType.APPLICATION_JSON));
        //then
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        TicketDto ticketDto = objectMapper.readValue(contentAsString, TicketDto.class);

        assertAll(
                () -> assertThat(ticketDto.drawDate()).isEqualTo(drawDate),
                () -> assertThat(ticketDto.ticketId()).isNotNull(),
                () -> assertThat(ticketDto.message()).isEqualTo("Success")
        );


//        step 4: user made GET /results/notExistingId and system returned 404(NOT_FOUND) and body with (“message”: “Not found for id: notExistingId” and “status”: “NOT_FOUND”)
//        step 5: 3 days and 55 minutes passed, and it is 5 minute before draw (19.11.2022 11:55)
//        step 6: system generated result for TicketId: sampleTicketId with draw date 19.11.2022 12:00, and saved it with 6 hits
//        step 7: 6 minutes passed, and it is 1 minute after the draw (19.11.2022 12:01)
//        step 8: user made GET /results/sampleTicketId and system returned 200 (OK)

    }
}