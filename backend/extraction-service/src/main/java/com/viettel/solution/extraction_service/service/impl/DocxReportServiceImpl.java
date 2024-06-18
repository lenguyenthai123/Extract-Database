package com.viettel.solution.extraction_service.service.impl;

import com.amazonaws.AmazonClientException;
import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.*;
import com.viettel.solution.extraction_service.generator.DocxGenerator;
import com.viettel.solution.extraction_service.service.AwsService;
import com.viettel.solution.extraction_service.service.DatabaseService;
import com.viettel.solution.extraction_service.service.ReportService;
import com.viettel.solution.extraction_service.utils.Utils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Qualifier("docxExportServiceImpl")
public class DocxReportServiceImpl implements ReportService {

    @Value("${nodejs.url}")
    private String nodejsUrl;

    @Autowired
    DatabaseService databaseService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AwsService awsService;

    @Override
    public byte[] exportDatabase(DocumentTemplate documentTemplateDetail) {
        String usernameId = documentTemplateDetail.getUsernameId();
        String type = documentTemplateDetail.getType();
        String fileName = documentTemplateDetail.getFileName();
        String dataJson = documentTemplateDetail.getDataJson();
        String extension = documentTemplateDetail.getExtension();

        dataJson = modifyJsonData(dataJson);

        SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
        if (sessionFactory == null) {
            return null;
        }

        Database database = databaseService.getDatabase(new RequestDto().builder().usernameId(usernameId).type(type).build());

        // Lấy rsource template path
        String resourcePath = "";
        URL resourceUrl = getClass().getClassLoader().getResource("static/font/template/");
        if (resourceUrl != null) {
            // Lấy đường dẫn tuyệt đối của tài nguyên và giải mã URL
            resourcePath = java.net.URLDecoder.decode(resourceUrl.getPath(), java.nio.charset.StandardCharsets.UTF_8);
            System.out.println("Absolute Path: " + resourcePath);

        } else {
            System.out.println("Resource not found");
        }


        String sourcePath = "";
        String defaultPath = "";

        if (fileName.equals("default")) {
            // Trường hợp mặc định không có template được gửi lên
            defaultPath = resourcePath.substring(1) + "default.docx";
        } else {
            // Trương hợp có template được gửi lên.
            defaultPath = resourcePath.substring(1) + fileName;
            try {
                System.out.println("Downloading file: " + usernameId + "/" + fileName);
                ByteArrayOutputStream outputStream = awsService.downloadFile(usernameId + "/" + fileName);

                // Lưu file vào thư mục tạm
                Utils.saveFile(outputStream, defaultPath);

            } catch (IOException | AmazonClientException e) {
                e.printStackTrace();
                throw new RuntimeException("Error downloading file");
            }
        }


        File file = new File(defaultPath);

        try (FileInputStream fis = new FileInputStream(file); XWPFDocument document = new XWPFDocument(fis); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            DocxGenerator.insertPageBreak(document);

            // Chỗ này cho nhập thông tin về tác giả, phiên bản, ngày tạo,...

            if (documentTemplateDetail.isDefault()) {
                DocxGenerator.addHeader(document, "id – Thiết kế chi tiết dữ liệu", "v1.2");
                DocxGenerator.addFooterWithPageNumbers(document);
                DocxGenerator.addFooter(document, "Bản quyền © 2024 Viettel Solution Công ty Cổ phần Giải pháp Viettel");
            } else {
                DocxGenerator.addHeader(document, documentTemplateDetail.getHeaderLeft(), documentTemplateDetail.getHeaderRight());
                DocxGenerator.addFooterWithPageNumbers(document);
                DocxGenerator.addFooter(document, documentTemplateDetail.getFooterLeft());
            }

            int heading1Count = DocxGenerator.countHeadings(document, "Heading1");
            System.out.println("Number of Heading 1: " + heading1Count);

            // Tạo chi tiết các tiểu tiết.
            if (fileName.equals("default")) {
                // Tạo mock một heading 1
                DocxGenerator.addTitleToDocument(document, "", 1);
            }
            DocxGenerator.addTitleToDocument(document, "CƠ SỞ DỮ LIỆU", 1);


            if (database.getSchemas().isEmpty()) {
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText("N/A");
                run.setFontFamily("Times New Roman");
                run.setFontSize(14);  // Bạn có thể thay đổi kích thước font theo ý muốn
                DocxGenerator.addEmptyParagraph(document);
            }


            for (int i = 0; i < database.getSchemas().size(); i++) {
                Schema schema = database.getSchemas().get(i);

                // Không hiển thị các schema mặc định
                if (schema.getName().equals("information_schema") || schema.getName().equals("sys") || schema.getName().equals("performance_schema"))
                    continue;

                // Divide heading for schema
                String headingShema = (i + 1) + "";

                DocxGenerator.addTitleToDocument(document, " Schema: " + schema.getName().toUpperCase(), 2);

                if (schema.getTables().isEmpty()) {
                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.setText("N/A");
                    run.setFontFamily("Times New Roman");
                    run.setFontSize(14);  // Bạn có thể thay đổi kích thước font theo ý muốn
                    DocxGenerator.addEmptyParagraph(document);
                    continue;
                }

                DocxGenerator.addEmptyParagraph(document);

                // Tạo outline cho moi dau schema
                List<List<String>> outlineData = new ArrayList<>(List.of(columnHeadersOfOutline));

                for (int j = 0; j < schema.getTables().size(); j++) {
                    Table table = schema.getTables().get(j);
                    String name = table.getName().toUpperCase();
                    String description = table.getDescription() == null ? "" : table.getDescription();
                    outlineData.add(List.of(name, description));

                }
                DocxGenerator.addTableToDocument(document, schema.getTables().size() + 1, columnHeadersOfOutline.size(), outlineData, ParagraphAlignment.LEFT);
                DocxGenerator.addEmptyParagraph(document);


                for (int j = 0; j < schema.getTables().size(); j++) {
                    Table table = schema.getTables().get(j);

                    // Divide heading for table
                    String headingTable = headingShema + "." + (j + 1);

                    // Generate table for columns
                    DocxGenerator.addTitleToDocument(document, "\t" + table.getName().toUpperCase(), 3, false);
                    addTableOfColumns(document, table);

                    // Generate table for constraints
                    DocxGenerator.addTitleToDocument(document, "\t\t" + "Constraint", 4);
                    addConstraintTable(document, table);

                    // Generate table for indexes
                    DocxGenerator.addTitleToDocument(document, "\t\t" + "Index", 4);
                    addIndexTable(document, table);

                    // Generate table for triggers
                    DocxGenerator.addTitleToDocument(document, "\t\t" + "Trigger", 4);
                    addTriggerTable(document, table);

                    DocxGenerator.addEmptyParagraph(document);
                }
                DocxGenerator.addEmptyParagraph(document);
            }

            document.write(byteArrayOutputStream);
            document.close();

            Utils.deleteFile(defaultPath);

            if (defaultPath.equals(resourcePath.substring(1) + "default.docx")) {

                // Xóa file tạm
                return byteArrayOutputStream.toByteArray();
            } else {

                // Trường hợp này có template được gửi lên => Gửi cho nodejs để Hanlde
                return sendFileToNodeJsServiceAndReturnFillingFile(byteArrayOutputStream, dataJson, fileName, extension);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String modifyJsonData(String dataJson) {
        dataJson.replace("\"", "\\\""); // Thay thế ký tự " thành \"
        return dataJson;
    }

    private byte[] sendFileToNodeJsServiceAndReturnFillingFile(ByteArrayOutputStream byteArrayOutputStream, String dataJson, String filename, String extension) {
        byte[] fileContent = byteArrayOutputStream.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Prepare body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file", new org.springframework.core.io.ByteArrayResource(fileContent) {
            @Override
            public String getFilename() {
                return filename;
            }
        });
        body.add("dataJson", dataJson);
        body.add("type", extension);

        // Create HTTP entity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Send request to Node.js server
        ResponseEntity<byte[]> response = restTemplate.exchange(nodejsUrl + "/generate-report", HttpMethod.POST, requestEntity, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }


    @Override
    public byte[] exportDatabase(RequestDto requestDto) {
        return new byte[0];
    }

    private void addTableOfColumns(XWPFDocument document, Table table) {
        if (table.getColumns().isEmpty()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("\t\t N/A");
            run.setFontFamily("Times New Roman");
            run.setFontSize(14);  // Bạn có thể thay đổi kích thước font theo ý muốn
            DocxGenerator.addEmptyParagraph(document);
            return;
        }

        List<List<String>> tableData = new ArrayList<>(List.of(columnHeadersOfTable));
        for (Column column : table.getColumns()) {
            String name = column.getName() == null ? "" : column.getName();
            String dataType = column.getDataType() == null ? "" : column.getDataType();
            String size = column.getSize() == null ? "" : String.valueOf(column.getSize());
            String isNullable = (!column.isNullable()) ? "" : "X";
            String isAutoIncrement = (!column.isAutoIncrement()) ? "" : "X";
            String isPrimaryKey = (!column.isPrimaryKey()) ? "" : "PK";
            String defaultValue = column.getDefaultValue() == null ? "" : column.getDefaultValue();
            String description = column.getDescription() == null ? "" : column.getDescription();

            tableData.add(List.of(name, dataType + " (" + size + ") ", isNullable, isAutoIncrement, isPrimaryKey, defaultValue, description));
        }
        DocxGenerator.addTableToDocument(document, table.getColumns().size() + 1, columnHeadersOfTable.size(), tableData, ParagraphAlignment.CENTER);
    }

    private void addConstraintTable(XWPFDocument document, Table table) {
        if (table.getConstraints().isEmpty()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("\t\t\t N/A");
            run.setFontFamily("Times New Roman");
            run.setFontSize(14);  // Bạn có thể thay đổi kích thước font theo ý muốn
            DocxGenerator.addEmptyParagraph(document);
            return;
        }

        List<List<String>> constraintData = new ArrayList<>(List.of(columnHeadersOfConstraint));
        for (Constraint constraint : table.getConstraints()) {
            String name = constraint.getName() == null ? "" : constraint.getName();
            String columnName = constraint.getColumnName() == null ? "" : constraint.getColumnName();
            String constraintType = constraint.getConstraintType() == null ? "" : constraint.getConstraintType();
            String refTableName = constraint.getRefTableName() == null ? "" : constraint.getRefTableName();
            String refColumnName = constraint.getRefColumnName() == null ? "" : constraint.getRefColumnName();

            constraintData.add(List.of(name, columnName, constraintType, refTableName, refColumnName));
        }
        DocxGenerator.addTableToDocument(document, table.getConstraints().size() + 1, columnHeadersOfConstraint.size(), constraintData, ParagraphAlignment.LEFT);
    }

    private void addIndexTable(XWPFDocument document, Table table) {
        if (table.getIndexs().isEmpty()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("\t\t\t N/A");
            run.setFontFamily("Times New Roman");
            run.setFontSize(14);  // Bạn có thể thay đổi kích thước font theo ý muốn
            DocxGenerator.addEmptyParagraph(document);
            return;
        }

        List<List<String>> indexData = new ArrayList<>(List.of(columnHeadersOfIndex));
        for (Index index : table.getIndexs()) {
            String columnNames = String.join(", ", index.getColumns());
            indexData.add(List.of(String.valueOf(index.getName()), String.valueOf(columnNames)));
        }
        DocxGenerator.addTableToDocument(document, table.getIndexs().size() + 1, columnHeadersOfIndex.size(), indexData, ParagraphAlignment.LEFT);
    }

    private void addTriggerTable(XWPFDocument document, Table table) {
        if (table.getTriggers().isEmpty()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("\t\t\t N/A");
            run.setFontFamily("Times New Roman");
            run.setFontSize(14);  // Bạn có thể thay đổi kích thước font theo ý muốn
            DocxGenerator.addEmptyParagraph(document);
            return;
        }

        List<List<String>> triggerData = new ArrayList<>(List.of(columnHeadersOfTrigger));
        for (Trigger trigger : table.getTriggers()) {
            String name = trigger.getName() == null ? "" : trigger.getName();
            String event = trigger.getEvent() == null ? "" : trigger.getEvent();
            String timing = trigger.getTiming() == null ? "" : trigger.getTiming();
            String doAction = trigger.getDoAction() == null ? "" : trigger.getDoAction();
            String actionCondition = trigger.getActionCondition() == null ? "" : trigger.getActionCondition();

            triggerData.add(List.of(name, event, timing, doAction, actionCondition));
        }
        DocxGenerator.addTableToDocument(document, table.getTriggers().size() + 1, columnHeadersOfTrigger.size(), triggerData, ParagraphAlignment.LEFT);
    }
}
