package com.viettel.solution.extraction_service.generator;

import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.math.BigInteger;
import java.util.List;

public class DocxGenerator {
        public static XWPFDocument addTableToDocument(XWPFDocument document, int numRows, int numCols, List<List<String>> tableData) {
            // Tạo bảng
            XWPFTable table = document.createTable(numRows, numCols);

            // Định dạng và thêm dữ liệu vào bảng
            for (int row = 0; row < tableData.size(); row++) {
                XWPFTableRow tableRow = table.getRow(row);
                for (int col = 0; col < numCols; col++) {
                    XWPFTableCell cell = tableRow.getCell(col);
                    setCellText(cell, tableData.get(row).get(col), row == 0);
                }
            }

            // Định dạng bảng
            CTTblPr tblPr = table.getCTTbl().getTblPr();
            CTTblWidth tblW = tblPr.addNewTblW();
            tblW.setType(STTblWidth.DXA);
            tblW.setW(BigInteger.valueOf(10072)); // Độ rộng của bảng

            // Thêm đoạn văn trống để tạo khoảng cách giữa các bảng
            addEmptyParagraph(document);
            return document;
        }

        private static void setCellText(XWPFTableCell cell, String text, boolean isHeader) {
            XWPFParagraph paragraph = cell.getParagraphs().get(0);
            XWPFRun run = paragraph.createRun();
            run.setText(text);
            run.setFontFamily("Times New Roman"); // Đặt kiểu chữ là Times New Roman
            if (isHeader) {
                run.setItalic(true); // Đặt văn bản in nghiêng
                cell.setColor("F6C5AC"); // Màu nền giống như trong hình (mã màu hex)
                run.setBold(true);
            }
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        }

    public static void addTitleToDocument(XWPFDocument document, String titleText, int titleLevel, boolean bold) {
        // Tạo tiêu đề và căn giữa
        XWPFParagraph title = document.createParagraph();

        // Đặt kiểu tiêu đề theo cấp độ
        title.setStyle("Heading" + titleLevel);

        // Tạo và định dạng văn bản cho tiêu đề
        XWPFRun titleRun = title.createRun();
        titleRun.setText(titleText);
        titleRun.setFontFamily("Times New Roman"); // Đặt kiểu chữ là Times New Roman
        titleRun.setBold(bold);
        titleRun.setFontSize(14);  // Bạn có thể thay đổi kích thước font theo ý muốn
    }

    public static void addTitleToDocument(XWPFDocument document, String titleText, int titleLevel) {
        addTitleToDocument(document, titleText, titleLevel, true);
    }

    static public void addEmptyParagraph(XWPFDocument document) {
        XWPFParagraph emptyParagraph = document.createParagraph();
        XWPFRun run = emptyParagraph.createRun();
        run.setText(""); // Đoạn văn trống
    }

    public static void addHeader(XWPFDocument document, String leftText, String rightText) {
        // Tạo phần header
        XWPFHeader header = document.createHeader(HeaderFooterType.DEFAULT);

        // Tạo bảng với một hàng và hai cột
        XWPFTable table = header.createTable(1, 2);

        // Đặt độ rộng của bảng để chiếm hết chiều rộng trang
        table.setWidth("100%");

        // Định dạng ô bên trái
        XWPFTableCell leftCell = table.getRow(0).getCell(0);
        XWPFParagraph leftParagraph = leftCell.getParagraphs().get(0);
        leftParagraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun leftRun = leftParagraph.createRun();
        leftRun.setText(leftText);
        leftRun.setFontFamily("Times New Roman");
        leftRun.setFontSize(12);

        // Định dạng ô bên phải
        XWPFTableCell rightCell = table.getRow(0).getCell(1);
        XWPFParagraph rightParagraph = rightCell.getParagraphs().get(0);
        rightParagraph.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun rightRun = rightParagraph.createRun();
        rightRun.setText(rightText);
        rightRun.setFontFamily("Times New Roman");
        rightRun.setFontSize(12);

        // Xóa đường viền bảng để trông giống như không có bảng
        table.getCTTbl().getTblPr().unsetTblBorders();
    }

    public static void addFooter(XWPFDocument document, String footerText) {
        // Tạo phần footer
        XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);

        // Tạo đoạn văn và thêm vào footer
        XWPFParagraph paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setText(footerText);
        run.setFontFamily("Times New Roman");
        run.setFontSize(12);
    }

    public static void addFooterWithPageNumbers(XWPFDocument document) {
        // Tạo phần footer
        XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);

        // Tạo đoạn văn và căn giữa
        XWPFParagraph paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);

        // Tạo đoạn văn bản cho số trang
        XWPFRun run = paragraph.createRun();
        run.setText("Trang ");

        // Thêm trường đánh số trang tự động
        paragraph.getCTP().addNewFldSimple().setInstr("PAGE");

        run = paragraph.createRun();
        run.setText(" / ");

        // Thêm trường tổng số trang
        paragraph.getCTP().addNewFldSimple().setInstr("NUMPAGES");
    }
}