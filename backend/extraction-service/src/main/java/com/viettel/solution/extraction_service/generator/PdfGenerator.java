package com.viettel.solution.extraction_service.generator;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PdfGenerator {

    public static PdfFont font;

    // Tạo danh sách các điểm dừng tab
    public static List<TabStop> tabStops;

    static {
        try {
            // Init  for tab stop
            tabStops = new ArrayList<>();
            tabStops.add(new TabStop(50)); // Điểm dừng tab đầu tiên tại vị trí 50 đơn vị
            tabStops.add(new TabStop(100)); // Điểm dừng tab thứ hai tại vị trí 100 đơn vị
            tabStops.add(new TabStop(150)); // Điểm dừng tab thứ hai tại vị trí 100 đơn vị
            tabStops.add(new TabStop(200)); // Điểm dừng tab thứ hai tại vị trí 100 đơn vị

            // Init for font.
            font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void addTableToDocument(Document document, List<List<String>> tableData) throws IOException, java.io.IOException {
        int numCols = tableData.get(0).size();
        int numRows = tableData.size() + 1;
        // Tạo bảng với số cột đã cho
        Table table = new Table(numCols);

        // Định dạng tiêu đề của bảng
        PdfFont font = PdfFontFactory.createFont("/static/font/NotoSerif_Condensed-Regular.ttf", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        PdfFont boldFont = PdfFontFactory.createFont("/static/font/NotoSerif_Condensed-MediumItalic.ttf", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        Color headerColor = new DeviceRgb(246, 197, 172);

        // Định dạng và thêm dữ liệu vào bảng
        for (int row = 0; row < tableData.size(); row++) {
            List<String> rowData = tableData.get(row);
            for (int col = 0; col < numCols; col++) {
                boolean isHeader = row == 0;
                Cell cell = new Cell().add(new Paragraph(rowData.get(col)).setFont(isHeader ? boldFont : font));
                if (isHeader) {
                    cell.setBackgroundColor(headerColor);
                    cell.setBold();
                }
                cell.setFontSize(12);
                cell.setTextAlignment(TextAlignment.CENTER);
                cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
                table.addCell(cell);
            }
        }

        // Đặt chiều rộng bảng
        table.setWidth(UnitValue.createPercentValue(100));

        // Thêm bảng vào tài liệu PDF
        document.add(table);

        // Thêm đoạn văn trống để tạo khoảng cách giữa các bảng
        addEmptyParagraph(document);
    }

    public static void addEmptyParagraph(Document document) {
        document.add(new Paragraph("\n"));
    }

    public static void addTitleToDocument(Document document, String titleText, int titleLevel, boolean bold, int numberTab) throws IOException {
        // Tạo font Times New Roman
        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

        // Tạo và định dạng đoạn văn cho tiêu đề
        Paragraph title = new Paragraph()
                .addTabStops(tabStops);

        for (int i = 0; i < numberTab; i++) {
            title.add(new Tab());
        }

        title.add(titleText)
                .setFont(font)
                .setFontSize(getFontSizeForTitleLevel(titleLevel))
                .setTextAlignment(TextAlignment.LEFT);

        // Đặt kiểu chữ đậm nếu cần
        if (bold) {
            title.setBold();
        }

        // Thêm tiêu đề vào tài liệu PDF
        document.add(title);
    }

    // Phương thức để xác định kích thước font dựa trên cấp độ tiêu đề
    public static float getFontSizeForTitleLevel(int titleLevel) {
        switch (titleLevel) {
            case 1:
                return 24;
            case 2:
                return 20;
            case 3:
                return 16;
            default:
                return 14; // Kích thước mặc định cho các cấp độ tiêu đề khác
        }
    }

}
