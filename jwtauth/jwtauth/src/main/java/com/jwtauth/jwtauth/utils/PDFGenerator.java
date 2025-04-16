package com.jwtauth.jwtauth.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.jwtauth.jwtauth.dto.EmployeePdfDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PDFGenerator {

    public static ByteArrayOutputStream generateEmployeePdf(EmployeePdfDTO employeePdfDTO) throws DocumentException, IOException {

        // Create a ByteArrayOutputStream to hold the PDF content
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Create a document
        Document document = new Document();

        // Create PdfWriter instance to write to the ByteArrayOutputStream
        PdfWriter.getInstance(document, byteArrayOutputStream);

        // Open the document
        document.open();

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

        document.add(new Paragraph("Employee Details", font));

        // Add employee details from EmployeePdfDTO to the document
        document.add(new Paragraph("ID: " + employeePdfDTO.getId(), font));
        document.add(new Paragraph("Name: " + employeePdfDTO.getName(), font));
        document.add(new Paragraph("Department: " + employeePdfDTO.getDepartment(), font));
        document.add(new Paragraph("Salary: " + employeePdfDTO.getSalary(), font));

        document.close();

        return byteArrayOutputStream;
    }
}



/*

package com.jwtauth.jwtauth.utils;

import com.itextpdf.text.*;
        import com.itextpdf.text.pdf.*;
        import com.itextpdf.text.pdf.draw.LineSeparator;
import com.jwtauth.jwtauth.dto.EmployeePdfDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PDFGenerator {

    public static ByteArrayOutputStream generateEmployeePdf(EmployeePdfDTO employeePdfDTO) throws DocumentException, IOException {
        final float DEFAULT_FONT_SIZE = 16;

        // Create a ByteArrayOutputStream to hold the PDF content
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Initialize the document and PdfWriter
        Document document = new Document(new Rectangle(560, 500));
        PdfWriter.getInstance(document, byteArrayOutputStream);

        // Open the document
        document.open();

        // Set up fonts and styles
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, DEFAULT_FONT_SIZE, Font.BOLD);
        Font contentFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLUE);

        // Add header section with styling
        addHeader(document, titleFont);

        // Add content to the PDF
        document.add(new Paragraph("Employee Details", titleFont));
        document.add(new Chunk(new LineSeparator()));

        addStyledEmployeeDetails(document, employeePdfDTO, labelFont, contentFont);

        // Add footer
        addFooter(document);

        // Close the document
        document.close();

        return byteArrayOutputStream;
    }

    private static void addHeader(Document document, Font titleFont) throws DocumentException {
        Paragraph header = new Paragraph("Company XYZ - Employee Record", titleFont);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);
        document.add(new Chunk(new LineSeparator()));
    }

    private static void addStyledEmployeeDetails(Document document, EmployeePdfDTO employeePdfDTO, Font labelFont, Font contentFont) throws DocumentException {
        document.add(new Paragraph("ID: ", labelFont));
        document.add(new Paragraph(String.valueOf(employeePdfDTO.getId()), contentFont));

        document.add(new Paragraph("Name: ", labelFont));
        document.add(new Paragraph(employeePdfDTO.getName(), contentFont));

        document.add(new Paragraph("Department: ", labelFont));
        document.add(new Paragraph(employeePdfDTO.getDepartment(), contentFont));

        document.add(new Paragraph("Salary: ", labelFont));
        document.add(new Paragraph(String.valueOf(employeePdfDTO.getSalary()), contentFont));
    }

    private static void addFooter(Document document) throws DocumentException {
        document.add(new Chunk(new LineSeparator()));
        Paragraph footer = new Paragraph("Generated on: " + java.time.LocalDate.now(), new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
}
*/

