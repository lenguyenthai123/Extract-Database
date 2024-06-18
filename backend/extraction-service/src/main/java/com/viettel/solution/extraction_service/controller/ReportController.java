package com.viettel.solution.extraction_service.controller;

import com.viettel.solution.extraction_service.dto.MessageDto;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.dto.UserDto;
import com.viettel.solution.extraction_service.entity.DocumentTemplate;
import com.viettel.solution.extraction_service.entity.TemplateUser;
import com.viettel.solution.extraction_service.service.AwsService;
import com.viettel.solution.extraction_service.service.ReportService;
import com.viettel.solution.extraction_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    private ReportService reportServiceDoc;
    private ReportService reportServicePdf;

    @Autowired
    private AwsService awsService;

    @Autowired
    private UserService userService;

    @Autowired
    public ReportController(@Qualifier("docxExportServiceImpl") ReportService reportServiceDoc, @Qualifier("pdfExportServiceImpl") ReportService reportServicePdf) {
        this.reportServiceDoc = reportServiceDoc;
        this.reportServicePdf = reportServicePdf;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> addTemplate(@RequestParam("file") MultipartFile file, @RequestParam("usernameId") String usernameId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        Long id = null;
        try {
            id = Long.parseLong(usernameId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid usernameId");
        }

        TemplateUser template = userService.addTemplate(id, file);

        if (template == null) {
            return ResponseEntity.badRequest().body(new MessageDto("Template not added"));
        }

        return ResponseEntity.ok().body(new MessageDto("Template added successfully"));
    }

    @GetMapping("/list-template/{usernameId}")
    public ResponseEntity<?> listTemplate(@PathVariable String usernameId) {

        if (usernameId.isEmpty()) {
            return ResponseEntity.badRequest().body("Username is empty");
        }
        Long id = Long.parseLong(usernameId);

        UserDto userDto = userService.getUserById(id);

        if (userDto == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        List<String> listTemplate = userService.getListTemplate(id);
        return ResponseEntity.ok().body(listTemplate);
    }

    @GetMapping("/download-template/{usernameId}/{templateName}")
    public ResponseEntity<?> downloadTemplate(@PathVariable String usernameId, @PathVariable String templateName) throws IOException {
        if (usernameId.isEmpty() || templateName.isEmpty()) {
            return ResponseEntity.badRequest().body("Username or template name is empty");
        }

        Long id = Long.parseLong(usernameId);

        UserDto userDto = userService.getUserById(id);

        if (userDto == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        ByteArrayOutputStream template = awsService.downloadFile(id + "/" + templateName);

        if (template == null) {
            return ResponseEntity.badRequest().body(new MessageDto("Template not found"));
        }

        return ResponseEntity.ok().body(template.toByteArray());
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