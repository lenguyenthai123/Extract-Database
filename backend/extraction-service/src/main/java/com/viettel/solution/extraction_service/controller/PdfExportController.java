package com.viettel.solution.extraction_service.controller;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/export-pdf")
public class PdfExportController {

    private ReportService reportService;

    @Autowired
    public PdfExportController(@Qualifier("pdfExportServiceImpl") ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/database")
    public ResponseEntity<byte[]> exportDatabase(@ModelAttribute RequestDto requestDto) {
        // Tạo phản hồi HTTP
        byte[] pdfFile = reportService.exportDatabase(requestDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Báo cáo.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfFile);
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPdf() {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            // Tạo tài liệu PDF
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Tạo tiêu đề
            Paragraph title = new Paragraph("Constraint")
                    .setBold()
                    .setFontSize(14);
            document.add(title);

            // Tạo bảng
            float[] columnWidths = {2, 2, 2, 2, 2};
            Table table = new Table(columnWidths);

            // Định dạng tiêu đề của bảng
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
            Color headerColor = new DeviceRgb(217, 234, 211);

            table.addHeaderCell(new Cell().add(new Paragraph("Tên khóa").setFont(boldFont)).setBackgroundColor(headerColor));
            table.addHeaderCell(new Cell().add(new Paragraph("Tên trường").setFont(boldFont)).setBackgroundColor(headerColor));
            table.addHeaderCell(new Cell().add(new Paragraph("Kiểu").setFont(boldFont)).setBackgroundColor(headerColor));
            table.addHeaderCell(new Cell().add(new Paragraph("Bảng tham chiếu").setFont(boldFont)).setBackgroundColor(headerColor));
            table.addHeaderCell(new Cell().add(new Paragraph("Cột tham chiếu").setFont(boldFont)).setBackgroundColor(headerColor));

            // Định dạng dòng dữ liệu
            table.addCell(new Cell().add(new Paragraph("PRIMARY")));
            table.addCell(new Cell().add(new Paragraph("ID"))) ;
            table.addCell(new Cell().add(new Paragraph("PK"))) ;
            table.addCell(new Cell());
            table.addCell(new Cell());

            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell());
            table.addCell(new Cell());

            document.add(table);

            // Đóng tài liệu PDF
            document.close();

            // Tạo phản hồi HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Bao cao.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
