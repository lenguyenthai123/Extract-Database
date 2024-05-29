package com.viettel.solution.extraction_service.service.impl;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.Column;
import com.viettel.solution.extraction_service.entity.Database;
import com.viettel.solution.extraction_service.entity.Schema;
import com.viettel.solution.extraction_service.entity.Table;
import com.viettel.solution.extraction_service.generator.DocxGenerator;
import com.viettel.solution.extraction_service.generator.PdfGenerator;
import com.viettel.solution.extraction_service.service.DatabaseService;
import com.viettel.solution.extraction_service.service.ExportService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Qualifier("pdfExportServiceImpl")
public class PdfExportServiceImpl implements ExportService {

    static PdfFont font;

    static {
        try {
            font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    DatabaseService databaseService;

    @Override
    public byte[] exportDatabase(RequestDto requestDto) {
        String usernameId = requestDto.getUsernameId();
        String type = requestDto.getType();
        SessionFactory sessionFactory = DatabaseConnection.getSessionFactory(usernameId, type);
        if (sessionFactory == null) {
            return null;
        }
        Database database = databaseService.getDatabase(requestDto);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            // Tạo tài liệu PDF
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Dữ liệu bảng mẫu
            List<List<String>> tableData = List.of(List.of("Header 1", "Header 2", "Header 3"), List.of("Row 1, Col 1 with long text", "Row 1, Col 2", "Row 1, Col 3"), List.of("Row 2, Col 1", "Row 2, Col 2 with even longer text that should wrap", "Row 2, Col 3"));

            // Tạo và định dạng đoạn văn cho tiêu đề
            Paragraph title = new Paragraph("Database: " + database.getName())
                    .setFont(font)
                    .setFontSize(PdfGenerator.getFontSizeForTitleLevel(1))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold();

            // Thêm tiêu đề vào tài liệu PDF
            document.add(title);

            if (database.getSchemas().isEmpty()) {
                // Thêm đoạn văn thông báo không có dữ liệu vào tài liệu PDF
                document.add(new Paragraph("\nNo data found")
                        .setFont(font)
                        .setFontSize(PdfGenerator.getFontSizeForTitleLevel(4))
                        .setTextAlignment(TextAlignment.CENTER));
            }


            // Create table for Schema

            for (int i = 0; i < database.getSchemas().size(); i++) {
                Schema schema = database.getSchemas().get(i);

                // Divide heading for schema
                String headingShema = (i + 1) + "";

                PdfGenerator.addTitleToDocument(
                        document,
                        (i + 1) + " Schema: " + schema.getName().toUpperCase(),
                        2,
                        true);

                if (schema.getTables().isEmpty()) {
                    Paragraph notFound = new Paragraph("N/A")
                            .setFont(font)
                            .setFontSize(PdfGenerator.getFontSizeForTitleLevel(4));
                    // Thêm tiêu đề vào tài liệu PDF
                    document.add(notFound);
                    PdfGenerator.addEmptyParagraph(document);
                    continue;
                }

                // Add empty space
                PdfGenerator.addEmptyParagraph(document);

                // Tạo outline cho moi dau schema
                List<List<String>> outlineData = new ArrayList<>(List.of(columnHeadersOfOutline));

                for (int j = 0; j < schema.getTables().size(); j++) {
                    Table table = schema.getTables().get(j);
                    String name = table.getName().toUpperCase();
                    String description = table.getDescription() == null ? "" : table.getDescription();
                    outlineData.add(List.of(name, description));

                }
                PdfGenerator.addTableToDocument(document, outlineData);
                PdfGenerator.addEmptyParagraph(document);


                for (int j = 0; j < schema.getTables().size(); j++) {
                    Table table = schema.getTables().get(j);

                    // Divide heading for table
                    String headingTable = headingShema + "." + (j + 1);

                    // Generate table for columns
                    PdfGenerator.addTitleToDocument(document, "\t" + headingTable + " " + table.getName().toUpperCase(), 3, false);
                    addTableOfColumns(document, table);

                    // Generate table for constraints
                    PdfGenerator.addTitleToDocument(document, "\t\t" + headingTable + ".1 Constraint", 4, true);
                    addConstraintTable(document, table);

                    // Generate table for indexes
                    DocxGenerator.addTitleToDocument(document, "\t\t" + headingTable + ".2 Index", 4);
                    addIndexTable(document, table);

                    // Generate table for triggers
                    DocxGenerator.addTitleToDocument(document, "\t\t" + headingTable + ".3 Trigger", 4);
                    addTriggerTable(document, table);

                    DocxGenerator.addEmptyParagraph(document);
                }
                DocxGenerator.addEmptyParagraph(document);
            }

            document.write(byteArrayOutputStream);
            document.close();
            return byteArrayOutputStream.toByteArray();

            // Đóng tài liệu PDF
            document.close();

            // Lưu tài liệu PDF
            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void addTableOfColumns(Document document, Table table) throws IOException {
        if (table.getColumns().isEmpty()) {
            Paragraph notFound = new Paragraph("N/A")
                    .setFont(font)
                    .setFontSize(PdfGenerator.getFontSizeForTitleLevel(4));
            // Thêm tiêu đề vào tài liệu PDF
            document.add(notFound);
            PdfGenerator.addEmptyParagraph(document);
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
        PdfGenerator.addTableToDocument(document, tableData);
    }


}