package com.viettel.solution.extraction_service.service;

import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.DocumentTemplate;

import java.util.List;

public interface ReportService {
    static List<String> columnHeadersOfTable = List.of("Tên trường", "Kiểu dữ liệu và độ dài", "Nullable", "Auto Increment", "P/K Key", "Mặc định", "Mô tả");
    static List<String> columnHeadersOfConstraint = List.of("Tên khóa", "Tên trường", "Kiểu", "Bảng tham chiếu", "Cột tham chiếu");
    static List<String> columnHeadersOfIndex = List.of("Tên Index", "Cột tham chiếu");
    static List<String> columnHeadersOfTrigger = List.of("Tên Trigger", "Event", "Timing", "Action", "Condition");
    static List<String> columnHeadersOfOutline = List.of("Tên bảng", "Mô tả");

    public byte[] exportDatabase(RequestDto requestDto);
    public byte[] exportDatabase(DocumentTemplate documentTemplate);



}
