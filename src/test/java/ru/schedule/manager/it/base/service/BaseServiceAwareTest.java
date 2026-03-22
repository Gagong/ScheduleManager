package ru.schedule.manager.it.base.service;

import org.junit.jupiter.api.Test;
import ru.schedule.manager.infrastructure.base.dto.BaseResponseDto;
import ru.schedule.manager.infrastructure.base.entity.BaseEntity;
import ru.schedule.manager.infrastructure.base.service.BaseServiceAware;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BaseServiceAwareTest {

    @Test
    void constants_ShouldBeDefined() {
        assertNotNull(BaseServiceAware.UNSUPPORTED_OPERATION);
        assertNotNull(BaseServiceAware.UNSUPPORTED_UPDATE_ALREADY_EXISTS_VALUES_ERROR);
        assertNotNull(BaseServiceAware.UNSUPPORTED_CREATE_ALREADY_EXISTS_DICTIONARY_VALUES_ERROR);
    }

    @Test
    void testService_ShouldImplementMethods() {
        final TestService service = new TestService();
        final TestEntity entity = new TestEntity(123L);

        final TestDto dto = service.fromEntity(entity);

        assertNotNull(dto);
        assertEquals(123L, dto.getId());
    }

    private static class TestEntity extends BaseEntity {
        TestEntity(final Long id) {
            super(id, 1L, null, null, null, null, 1L);
        }
    }

    private static class TestDto extends BaseResponseDto {
        TestDto(final Long id) {
            super(id, null, null, null, null);
        }
    }

    private static class TestService implements BaseServiceAware<TestEntity, TestDto> {
        @Override
        public TestDto fromEntity(final TestEntity entity) {
            return new TestDto(entity.getId());
        }

        @Override
        public java.util.List<TestDto> fromEntity(final java.util.List<TestEntity> entities) {
            return null;
        }

        @Override
        public TestDto findById(final Long id) {
            return null;
        }

        @Override
        public TestDto update(final TestDto dto) {
            return null;
        }

        @Override
        public TestDto create(final TestDto dto) {
            return null;
        }

        @Override
        public void delete(final TestDto dto) {
        }
    }

}
