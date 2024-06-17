package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.DocumentTemplate;
import com.viettel.solution.extraction_service.service.AwsService;
import com.viettel.solution.extraction_service.service.ReportService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.IOException;

@RestController
@RequestMapping("/report")
public class ReportController {

    private ReportService reportServiceDoc;
    private ReportService reportServicePdf;

    @Autowired
    private AwsService awsService;

    @Autowired
    public ReportController(@Qualifier("docxExportServiceImpl") ReportService reportServiceDoc, @Qualifier("pdfExportServiceImpl") ReportService reportServicePdf) {
        this.reportServiceDoc = reportServiceDoc;
        this.reportServicePdf = reportServicePdf;
    }

    @PostMapping("/upload")
    @SneakyThrows(IOException.class)
    public ResponseEntity<?> addTemplate(@RequestParam("file") MultipartFile file, @RequestParam("usernameId") String usernameId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String contentType = file.getContentType();
        long fileSize = file.getSize();
        InputStream inputStream = file.getInputStream();

        awsService.uploadFile(usernameId + "/" + fileName, fileSize, contentType, inputStream);

        return ResponseEntity.ok().body("File uploaded successfully");
    }


    @GetMapping("/export/{usernameId}")
    public ResponseEntity<byte[]> exportDatabase(
            @PathVariable String usernameId,
            @ModelAttribute DocumentTemplate documentTemplate) {
        // Tạo phản hồi HTTP
        byte[] fileBuffer = reportServiceDoc.exportDatabase(documentTemplate);

        String extension = documentTemplate.getExtension();


        HttpHeaders headers = new HttpHeaders();

        if (extension.equals("docx")) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Báo cáo.docx");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileBuffer);
        }
        if (extension.equals("pdf")) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Báo cáo.pdf");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(fileBuffer);
        }

        throw new IllegalArgumentException("Invalid extension");

    }

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportDatabase(@ModelAttribute RequestDto requestDto) {
        // Tạo phản hồi HTTP
        byte[] pdfFile = reportServicePdf.exportDatabase(requestDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Báo cáo.pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdfFile);
    }

}