package com.viettel.solution.extraction_service.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.properties.TextAlignment;
import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.*;
import com.viettel.solution.extraction_service.generator.PdfGenerator;
import com.viettel.solution.extraction_service.service.DatabaseService;
import com.viettel.solution.extraction_service.service.ExportService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.viettel.solution.extraction_service.generator.PdfGenerator.font;
import static com.viettel.solution.extraction_service.generator.PdfGenerator.tabStops;

@Service
@Qualifier("pdfExportServiceImpl")
public class PdfExportServiceImpl implements ExportService {


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
                document.add(new Paragraph()
                        .add("N/A")
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
                        true,0);

                if (schema.getTables().isEmpty()) {
                    Paragraph notFound = new Paragraph()
                            .add("N/A")
                            .setFont(font)
                            .setFontSize(PdfGenerator.getFontSizeForTitleLevel(4));
                    // Thêm tiêu đề vào tài liệu PDF
                    document.add(notFound);
                    PdfGenerator.addEmptyParagraph(document);
                    continue;
                }

                // Add empty space
                PdfGenerator.addEmptyParagraph(document);


                // Danh sách outline các bảng
                PdfGenerator.addTitleToDocument(
                        document,
                        "Danh sách các bảng".toUpperCase(),
                        2,
                        true,1);

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
                    PdfGenerator.addTitleToDocument(document, headingTable + " " + table.getName().toUpperCase(), 3, false, 1);
                    addTableOfColumns(document, table);

                    // Generate table for constraints
                    PdfGenerator.addTitleToDocument(document, headingTable + ".1 Constraint", 4, true, 2);
                    addConstraintTable(document, table);

                    // Generate table for indexes
                    PdfGenerator.addTitleToDocument(document, headingTable + ".2 Index", 4, true, 2);
                    addIndexTable(document, table);

                    // Generate table for triggers
                    PdfGenerator.addTitleToDocument(document, headingTable + ".3 Trigger", 4, true, 2);
                    addTriggerTable(document, table);

                    PdfGenerator.addEmptyParagraph(document);
                }
                PdfGenerator.addEmptyParagraph(document);
            }

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
            Paragraph notFound = new Paragraph()
                    .addTabStops(tabStops)
                    .add(new Tab())
                    .add("N/A")
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

    private void addConstraintTable(Document document, Table table) throws IOException {
        if (table.getConstraints().isEmpty()) {
            Paragraph notFound = new Paragraph()
                    .addTabStops(tabStops)
                    .add(new Tab())
                    .add(new Tab())
                    .add("N/A")
                    .setFont(font)
                    .setFontSize(PdfGenerator.getFontSizeForTitleLevel(4));
            // Thêm tiêu đề vào tài liệu PDF
            document.add(notFound);
            PdfGenerator.addEmptyParagraph(document);
            return;
        }

        List<List<String>> constraintData = new ArrayList<>(List.of(columnHeadersOfConstraint));
        for (Constraint constraint : table.getConstraints()) {
            String name = constraint.getName() == null ? "" : constraint.getName();
            String columnName = constraint.getColumnName() == null ? "" : constraint.getColumnName();
            String constraintType = constraint.getConstraintType() == null ? "" : constraint.getConstraintType();
            String refTableName = constraint.getRefTableName() == null ? "" : constraint.getRefTableName();
            String refColumnName = constraint.getRefColumnName() == null ? "" : constraint.getRefColumnName();

            constraintData.add(
                    List.of(name,
                            columnName,
                            constraintType,
                            refTableName,
                            refColumnName));
        }
        PdfGenerator.addTableToDocument(document, constraintData);
    }

    private void addIndexTable(Document document, Table table) throws IOException {
        if (table.getIndexs().isEmpty()) {
            Paragraph notFound = new Paragraph()
                    .addTabStops(tabStops)
                    .add(new Tab())
                    .add(new Tab())
                    .add("N/A")
                    .setFont(font)
                    .setFontSize(PdfGenerator.getFontSizeForTitleLevel(4));
            // Thêm tiêu đề vào tài liệu PDF
            document.add(notFound);
            PdfGenerator.addEmptyParagraph(document);
            return;
        }

        List<List<String>> indexData = new ArrayList<>(List.of(columnHeadersOfIndex));
        for (Index index : table.getIndexs()) {
            String columnNames = String.join(", ", index.getColumns());
            indexData.add(
                    List.of(String.valueOf(index.getName()),
                            String.valueOf(columnNames)));
        }
        PdfGenerator.addTableToDocument(document, indexData);
    }

    private void addTriggerTable(Document document, Table table) throws IOException {
        if (table.getTriggers().isEmpty()) {
            Paragraph notFound = new Paragraph()
                    .addTabStops(tabStops)
                    .add(new Tab())
                    .add(new Tab())
                    .add("N/A")
                    .setFont(font)
                    .setFontSize(PdfGenerator.getFontSizeForTitleLevel(4));
            // Thêm tiêu đề vào tài liệu PDF
            document.add(notFound);
            PdfGenerator.addEmptyParagraph(document);
            return;
        }

        List<List<String>> triggerData = new ArrayList<>(List.of(columnHeadersOfTrigger));
        for (Trigger trigger : table.getTriggers()) {
            String name = trigger.getName() == null ? "" : trigger.getName();
            String event = trigger.getEvent() == null ? "" : trigger.getEvent();
            String timing = trigger.getTiming() == null ? "" : trigger.getTiming();
            String doAction = trigger.getDoAction() == null ? "" : trigger.getDoAction();
            String actionCondition = trigger.getActionCondition() == null ? "" : trigger.getActionCondition();

            triggerData.add(
                    List.of(name,
                            event,
                            timing,
                            doAction,
                            actionCondition));
        }
        PdfGenerator.addTableToDocument(document, triggerData);
    }

}
