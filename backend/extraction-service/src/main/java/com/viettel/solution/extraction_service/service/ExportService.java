package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.RequestDto;

import java.util.List;

public interface ExportService {
    static List<String> columnHeadersOfTable = List.of("Tên trường", "Kiểu dữ liệu và độ dài", "Nullable", "Auto Increment", "P/K Key", "Mặc định", "Mô tả");
    static List<String> columnHeadersOfConstraint = List.of("Tên khóa", "Tên trường", "Kiểu", "Bảng tham chiếu", "Cột tham chiếu");
    static List<String> columnHeadersOfIndex = List.of("Tên Index", "Cột tham chiếu");
    static List<String> columnHeadersOfTrigger = List.of("Tên Trigger", "Event", "Timing", "Action", "Condition");

    public byte[] exportDatabase(RequestDto requestDto);
}
