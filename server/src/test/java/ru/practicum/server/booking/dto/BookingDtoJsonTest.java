package ru.practicum.server.booking.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.user.dto.UserDto;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void shouldSerializeReadOnlyFields() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 12, 0);
        LocalDateTime end = start.plusDays(1);
        BookingDto dto = new BookingDto(1L, start, end, 2L, 3L, BookingStatus.APPROVED,
                new ItemDto(2L, "Дрель", "Сильная", true, null), new UserDto(3L, "Заказчик", "заказчик@example.com"));

        JsonContent<BookingDto> content = json.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(content).extractingJsonPathNumberValue("$.booker.id").isEqualTo(3);
        assertThat(content).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @Test
    void shouldIgnoreReadOnlyFieldsOnDeserialization() throws Exception {
        String body = "{" +
                "\"start\": \"2024-01-01T12:00:00\"," +
                "\"end\": \"2024-01-02T12:00:00\"," +
                "\"itemId\": 2," +
                "\"item\": {\"id\": 99}," +
                "\"booker\": {\"id\": 100}" +
                "}";

        BookingDto parsed = json.parseObject(body);

        assertThat(parsed.getItem()).isNull();
        assertThat(parsed.getBooker()).isNull();
        assertThat(parsed.getItemId()).isEqualTo(2L);
    }
}
