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
