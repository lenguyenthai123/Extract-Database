package com.viettel.solution.extraction_service.service.impl;

import com.viettel.solution.extraction_service.database.DatabaseConnection;
import com.viettel.solution.extraction_service.dto.RequestDto;
import com.viettel.solution.extraction_service.entity.*;
import com.viettel.solution.extraction_service.generator.DocxGenerator;
import com.viettel.solution.extraction_service.service.DatabaseService;
import com.viettel.solution.extraction_service.service.ExportService;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
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
@Qualifier("docxExportServiceImpl")
public class DocxExportServiceImpl implements ExportService {

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


        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            DocxGenerator.addHeader(document, "RS – Thiết kế chi tiết dữ liệu", "v1.2");
            DocxGenerator.addFooterWithPageNumbers(document);
            DocxGenerator.addFooter(document, "Bản quyền © 2024 Viettel Solution Công ty Cổ phần Giải pháp Viettel");

            // Tạo tiêu đề
            XWPFParagraph title = document.createParagraph();
            title.setStyle("Heading 1");
            XWPFRun titleRun = title.createRun();
            title.setAlignment(ParagraphAlignment.CENTER);  // Căn giữa tiêu đề
            titleRun.setText("Database: " + database.getName());
            titleRun.setFontFamily("Times New Roman"); // Đặt kiểu chữ là Times New Roman
            titleRun.setBold(true);
            titleRun.setFontSize(20);

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

                // Divide heading for schema
                String headingShema = (i + 1) + "";

                DocxGenerator.addTitleToDocument(
                        document,
                        (i + 1) + " Schema: " + schema.getName().toUpperCase(),
                        2);

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
                DocxGenerator.addTableToDocument(document, schema.getTables().size() + 1, columnHeadersOfOutline.size(), outlineData);
                DocxGenerator.addEmptyParagraph(document);


                for (int j = 0; j < schema.getTables().size(); j++) {
                    Table table = schema.getTables().get(j);

                    // Divide heading for table
                    String headingTable = headingShema + "." + (j + 1);

                    // Generate table for columns
                    DocxGenerator.addTitleToDocument(document, "\t" + headingTable + " " + table.getName().toUpperCase(), 3, false);
                    addTableOfColumns(document, table);

                    // Generate table for constraints
                    DocxGenerator.addTitleToDocument(document, "\t\t" + headingTable + ".1 Constraint", 4);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void addTableOfColumns(XWPFDocument document, Table table) {
        if (table.getColumns().isEmpty()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("\t N/A");
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

            tableData.add(
                    List.of(name,
                            dataType + " (" + size + ") ",
                            isNullable,
                            isAutoIncrement,
                            isPrimaryKey,
                            defaultValue,
                            description));
        }
        DocxGenerator.addTableToDocument(document, table.getColumns().size() + 1, columnHeadersOfTable.size(), tableData);
    }

    private void addConstraintTable(XWPFDocument document, Table table) {
        if (table.getConstraints().isEmpty()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("\t\t N/A");
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

            constraintData.add(
                    List.of(name,
                            columnName,
                            constraintType,
                            refTableName,
                            refColumnName));
        }
        DocxGenerator.addTableToDocument(document, table.getConstraints().size() + 1, columnHeadersOfConstraint.size(), constraintData);
    }

    private void addIndexTable(XWPFDocument document, Table table) {
        if (table.getIndexs().isEmpty()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("\t\t N/A");
            run.setFontFamily("Times New Roman");
            run.setFontSize(14);  // Bạn có thể thay đổi kích thước font theo ý muốn
            DocxGenerator.addEmptyParagraph(document);
            return;
        }

        List<List<String>> indexData = new ArrayList<>(List.of(columnHeadersOfIndex));
        for (Index index : table.getIndexs()) {
            String columnNames = String.join(", ", index.getColumns());
            indexData.add(
                    List.of(String.valueOf(index.getName()),
                            String.valueOf(columnNames)));
        }
        DocxGenerator.addTableToDocument(document, table.getIndexs().size() + 1, columnHeadersOfIndex.size(), indexData);
    }

    private void addTriggerTable(XWPFDocument document, Table table) {
        if (table.getTriggers().isEmpty()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("\t\t N/A");
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

            triggerData.add(
                    List.of(name,
                            event,
                            timing,
                            doAction,
                            actionCondition));
        }
        DocxGenerator.addTableToDocument(document, table.getTriggers().size() + 1, columnHeadersOfTrigger.size(), triggerData);
    }
}
