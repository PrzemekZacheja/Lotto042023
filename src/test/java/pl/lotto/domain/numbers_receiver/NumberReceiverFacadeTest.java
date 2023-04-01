package pl.lotto.domain.numbers_receiver;

import org.junit.jupiter.api.Test;
import pl.lotto.domain.AdjustableClock;
import pl.lotto.domain.numbers_receiver.dto.InputNumbersResultDto;
import pl.lotto.domain.numbers_receiver.dto.TicketDto;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class NumberReceiverFacadeTest {

    AdjustableClock clock = new AdjustableClock(LocalDateTime.of(2023, 3, 31, 10, 0, 0).toInstant(ZoneOffset.UTC), ZoneId.systemDefault());

    NumberReceiverFacade numberReceiverFacade = new NumberReceiverFacade(
            new NumberValidator(),
            new InMemoryNumberReceiverRepositoryImpl(),
            clock,
            new HashGenerator(),
            new DateDrawGenerator(clock)
    );

    @Test
    void should_return_success_when_user_gave_six_numbers() {
        //given
        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);
        //when
        InputNumbersResultDto result = numberReceiverFacade.inputNumbers(numbersFromUser);
        //then
        assertThat(result.message()).isEqualTo("Success");
    }

    @Test
    void should_return_fail_when_user_gave_less_than_six_numbers() {
        //given
        Set<Integer> numbersFromUser = Set.of(2, 3, 4, 5, 6);
        //when
        InputNumbersResultDto result = numberReceiverFacade.inputNumbers(numbersFromUser);
        //then
        assertThat(result.message()).isEqualTo("Fail");
    }

    @Test
    void should_return_fail_when_user_gave_more_than_six_numbers() {
        //given
        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6, 7);
        //when
        InputNumbersResultDto result = numberReceiverFacade.inputNumbers(numbersFromUser);
        //then
        assertThat(result.message()).isEqualTo("Fail");
    }

    @Test
    void should_return_success_when_user_gave_six_numbers_in_range_from_1_to_99() {
        //given
        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);
        //when
        InputNumbersResultDto result = numberReceiverFacade.inputNumbers(numbersFromUser);
        //then
        assertThat(result.message()).isEqualTo("Success");
    }

    @Test
    void should_return_fail_when_user_gave_six_numbers_out_of_range_from_1_to_99() {
        //given
        Set<Integer> numbersFromUser = Set.of(1000, 2, 3, 4, 5, 6);
        //when
        InputNumbersResultDto result = numberReceiverFacade.inputNumbers(numbersFromUser);
        //then
        assertThat(result.message()).isEqualTo("Fail");
    }

    @Test
    void should_return_saved_object_when_user_gave_six_numbers() {
        //given
        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);
        InputNumbersResultDto result = numberReceiverFacade.inputNumbers(numbersFromUser);
        LocalDateTime drawDate = LocalDateTime.of(2023, 4, 1, 12, 0, 0);
        //when
        List<TicketDto> ticketDtos = numberReceiverFacade.usersNumbers(drawDate);
        //then
        assertThat(ticketDtos).contains(
                TicketDto.builder()
                        .ticketId(result.ticketId())
                        .drawDate(result.drawDate())
                        .numbersFromUser(result.userNumbers())
                        .build()

        );
    }

    @Test
    void should_return_correct_hash_for_returned_object() {
        //given
        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);
        //when
        InputNumbersResultDto result = numberReceiverFacade.inputNumbers(numbersFromUser);
        //then
        assertThat(result.ticketId().length()).isEqualTo(36);
        assertThat(result.ticketId()).isNotNull();
    }

    @Test
    public void it_should_return_next_Saturday_draw_date_when_date_is_Saturday_noon() {
        //given
        Clock clock = Clock.fixed(LocalDateTime.of(2023, 4, 1, 12, 0, 0).toInstant(ZoneOffset.UTC),
                ZoneId.of("Europe/London"));
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverFacade(
                new NumberValidator(),
                new InMemoryNumberReceiverRepositoryImpl(),
                clock,
                new HashGenerator(),
                new DateDrawGenerator(clock)
        );
        LocalDateTime expected = LocalDateTime.of(2023, 4, 8, 12, 0, 0);
        //when
        InputNumbersResultDto resultDto = numberReceiverFacade.inputNumbers(Set.of(1, 2, 3, 4, 5, 6));
        //then
        assertThat(expected).isEqualTo(resultDto.drawDate());
    }

    @Test
    public void it_should_return_next_Saturday_draw_date_when_date_is_Saturday_afternoon() {
        //given
        Clock clock = Clock.fixed(LocalDateTime.of(2023, 4, 1, 15, 0, 0).toInstant(ZoneOffset.UTC),
                ZoneId.of("Europe/London"));
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverFacade(
                new NumberValidator(),
                new InMemoryNumberReceiverRepositoryImpl(),
                clock,
                new HashGenerator(),
                new DateDrawGenerator(clock)
        );
        LocalDateTime expected = LocalDateTime.of(2023, 4, 8, 12, 0, 0);
        //when
        InputNumbersResultDto resultDto = numberReceiverFacade.inputNumbers(Set.of(1, 2, 3, 4, 5, 6));
        //then
        assertThat(expected).isEqualTo(resultDto.drawDate());
    }

    @Test
    public void it_should_return_this_Saturday_draw_date_when_date_is_before_Saturday_noon() {
        //given
        Clock clock = Clock.fixed(LocalDateTime.of(2023, 3, 30, 15, 0, 0).toInstant(ZoneOffset.UTC),
                ZoneId.of("Europe/London"));
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverFacade(
                new NumberValidator(),
                new InMemoryNumberReceiverRepositoryImpl(),
                clock,
                new HashGenerator(),
                new DateDrawGenerator(clock)
        );
        LocalDateTime expected = LocalDateTime.of(2023, 4, 1, 12, 0, 0);
        //when
        InputNumbersResultDto resultDto = numberReceiverFacade.inputNumbers(Set.of(1, 2, 3, 4, 5, 6));
        //then
        assertThat(expected).isEqualTo(resultDto.drawDate());
    }

    @Test
    public void it_should_return_empty_list_when_given_date_is_after_next_drawDate() {
        //given
        Clock clock = Clock.fixed(LocalDateTime.of(2023, 3, 30, 15, 0, 0).toInstant(ZoneOffset.UTC),
                ZoneId.of("Europe/London"));
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverFacade(
                new NumberValidator(),
                new InMemoryNumberReceiverRepositoryImpl(),
                clock,
                new HashGenerator(),
                new DateDrawGenerator(clock)
        );
        InputNumbersResultDto resultDto = numberReceiverFacade.inputNumbers(Set.of(1, 2, 3, 4, 5, 6));
        LocalDateTime drawDate = resultDto.drawDate();
        //when
        List<TicketDto> listOfTickets = numberReceiverFacade.retrieveAllTicketsByNextDrawDate(drawDate.plusWeeks(1L));
        //then
        assertThat(listOfTickets.size()).isEqualTo(0);
    }
}