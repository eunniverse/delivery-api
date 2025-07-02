package com.barogo.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "배달 조회 요청 DTO")
public class DeliverySearchRequest {

    @NotBlank(message = "조회일자는 필수입니다.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "조회 시자가일자는 yyyy-MM-dd 형식이어야 합니다.")
    @Schema(description = "조회 시작일 (yyyy-MM-dd)", example = "2024-07-01")
    private String startDate;

    @NotBlank(message = "조회일자는 필수입니다.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "조회 종료일자는 yyyy-MM-dd 형식이어야 합니다.")
    @Schema(description = "조회 종료일 (yyyy-MM-dd)", example = "2024-07-03")
    private String endDate;

    @Schema(description = "배달 상태 (PENDING, ONGOING, COMPLETE, CANCELLED)", example = "COMPLETE")
    private String status;

    @Schema(description = "주소 (부분 검색)", example = "서울시")
    private String address;

    @Min(1)
    @Schema(description = "페이지 번호 (1부터 시작)", example = "1")
    private int page = 1;

    @Min(1)
    @Schema(description = "페이지 크기", example = "10")
    private int size = 10;

    @Pattern(regexp = "asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "정렬 기준이 올바르지 않습니다.")
    @Schema(description = "정렬 기준 (asc 또는 desc)", example = "desc")
    private String sort = "desc";

    public LocalDate getStart() {
        return LocalDate.parse(startDate);
    }

    public LocalDate getEnd() {
        return LocalDate.parse(endDate);
    }
}

