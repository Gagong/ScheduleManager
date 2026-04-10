package ru.schedule.manager.business.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.schedule.manager.business.dataholder.ScheduleDataHolder;
import ru.schedule.manager.infrastructure.configuration.annotations.TestAvoidGenerated;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@TestAvoidGenerated
@RequiredArgsConstructor
public class ExcelService {

    @SneakyThrows
    public Workbook getSingleSchedule(final ScheduleDataHolder holder) {
        final Workbook wb = WorkbookFactory.create(ExcelService.class.getClassLoader().getResourceAsStream("templates/SingleTemplate.xlsx"));
        final Sheet sheet = wb.getSheetAt(0);
        holder.getRows()
                .forEach(week -> week.getCols()
                        .forEach(day -> day.getItems()
                                .forEach(lesson -> {
                                    final int col = lesson.getCol() + 2;
                                    final int row = lesson.getRow() == 0 ? lesson.getTimes().getDisplayOrder() + 2 : lesson.getTimes().getDisplayOrder() + 9;
                                    final Cell cell = sheet.getRow(row).getCell(col);
                                    cell.setCellValue(lesson.getValue());
                                })
                        )
                );
        return wb;
    }

    @SneakyThrows
    public static ResponseEntity<StreamingResponseBody> buildFileHttpEntity(
            final StreamingResponseBody content,
            final String fileName,
            final MediaType contentType
    ) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.builder("attachment")
                        .filename(fileName, StandardCharsets.UTF_8)
                        .build()
                        .toString()
        );
        headers.setContentType(contentType);
        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }

}
