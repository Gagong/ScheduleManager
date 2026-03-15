package ru.schedule.manager.it.base.dto;

import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;
import ru.schedule.manager.infrastructure.base.dto.BaseResponseDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class BaseResponseDtoTest {

    @Test
    void builder_ShouldCreateDto() {
        final LocalDateTime now = LocalDateTime.now();

        final TestDto dto = TestDto.builder()
                .id(1L)
                .createdDateTime(now)
                .updateDateTime(now)
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(now, dto.getCreatedDateTime());
        assertEquals(now, dto.getUpdateDateTime());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyDto() {
        final TestDto dto = new TestDto(null, null, null);

        assertNull(dto.getId());
        assertNull(dto.getCreatedDateTime());
        assertNull(dto.getUpdateDateTime());
    }

    @Test
    void setters_ShouldUpdateValues() {
        final TestDto dto = new TestDto(null, null, null);
        final LocalDateTime now = LocalDateTime.now();

        dto.setId(1L);
        dto.setCreatedDateTime(now);
        dto.setUpdateDateTime(now);

        assertEquals(1L, dto.getId());
        assertEquals(now, dto.getCreatedDateTime());
        assertEquals(now, dto.getUpdateDateTime());
    }

    @Test
    void equals_ShouldUseId() {
        final LocalDateTime now = LocalDateTime.now();
        final TestDto dto1 = new TestDto(1L, now, now);
        final TestDto dto2 = new TestDto(1L, now, now);
        final TestDto dto3 = new TestDto(2L, now, now);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, new Object());
    }

    @SuperBuilder
    private static class TestDto extends BaseResponseDto {
        TestDto(final Long id, final LocalDateTime createdDateTime, final LocalDateTime updateDateTime) {
            super(id, createdDateTime, updateDateTime);
        }
    }

}
