package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.DocumentTemplate;
import com.viettel.solution.extraction_service.service.ReportService;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

@RestController
@RequestMapping("/export-docx")
public class DocxExportController {


    private ReportService reportService;

    @Autowired
    public DocxExportController(@Qualifier("docxExportServiceImpl") ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/database")
    public ResponseEntity<byte[]> exportDatabase(@ModelAttribute DocumentTemplate documentTemplate) {
        // Tạo phản hồi HTTP
        byte[] docxfile = reportService.exportDatabase(documentTemplate);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Báo cáo.docx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(docxfile);
    }

    @GetMapping
    public ResponseEntity<byte[]> exportDocx() {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            // Tạo tiêu đề
            XWPFParagraph title = document.createParagraph();
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Constraint");
            titleRun.setBold(true);
            titleRun.setFontSize(14);

            // Tạo bảng
            XWPFTable table = document.createTable(3, 5);

            // Định dạng tiêu đề của bảng
            XWPFTableRow headerRow = table.getRow(0);
            setCellText(headerRow.getCell(0), "Tên khóa", true);
            setCellText(headerRow.getCell(1), "Tên trường", true);
            setCellText(headerRow.getCell(2), "Kiểu", true);
            setCellText(headerRow.getCell(3), "Bảng tham chiếu", true);
            setCellText(headerRow.getCell(4), "Cột tham chiếu", true);

            // Định dạng dòng dữ liệu
            XWPFTableRow dataRow = table.getRow(1);
            setCellText(dataRow.getCell(0), "PRIMARY", false);
            setCellText(dataRow.getCell(1), "ID", false);
            setCellText(dataRow.getCell(2), "PK", false);
            setCellText(dataRow.getCell(3), "", false);
            setCellText(dataRow.getCell(4), "", false);

            // Định dạng bảng
            CTTblPr tblPr = table.getCTTbl().getTblPr();
            CTTblWidth tblW = tblPr.addNewTblW();
            tblW.setType(STTblWidth.DXA);
            tblW.setW(BigInteger.valueOf(9072)); // Độ rộng của bảng

            // Ghi tài liệu vào ByteArrayOutputStream
            document.write(byteArrayOutputStream);

            // Tạo phản hồi HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample.docx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    private void setCellText(XWPFTableCell cell, String text, boolean isHeader) {
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        if (isHeader) {
            run.setBold(true);
            cell.setColor("D9EAD3"); // Màu nền của tiêu đề
        }
    }

}
