package com.finalbuild.services;

import com.finalbuild.entities.ActivityEntity;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class PdfConverterService {
    private final DatabaseService databaseService = new DatabaseService();

    public File createPdf() throws FileNotFoundException, DocumentException {
        List<ActivityEntity> list = databaseService.getAllActivities();
        File file = new File("/opt/tomcat/latest/activities.pdf");
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        try {
            document.open();
            document.add(new Paragraph("List of the activities on " + LocalDate.now() + "\n\n"));
            PdfPTable table = new PdfPTable(3);
            addTableHeader(table);
            document.add(new Paragraph("Activity for employee " + list.get(0).getName() + " " + list.get(0).getSurname() + ":" + "\n\n"));
            table.addCell(list.get(0).getActivity());
            table.addCell(String.valueOf(list.get(0).getDuration()));
            table.addCell(String.valueOf(list.get(0).getDate()));
            for (int i = 1; i < list.size(); i++) {
                if (!list.get(i).getSurname().equals(list.get(i - 1).getSurname())){
                    document.add(table);
                    document.add(new Paragraph("\n"));
                    document.add(new Paragraph("Activity for employee " + list.get(i).getName() + " " + list.get(i).getSurname() + ":" + "\n\n"));
                    table = new PdfPTable(3);
                    addTableHeader(table);
                }
                table.addCell(list.get(i).getActivity());
                table.addCell(String.valueOf(list.get(i).getDuration()));
                table.addCell(String.valueOf(list.get(i).getDate()));
                //document.add((new Paragraph("Id: " + activity.getId() + "\n" + "Name: " + activity.getName() + "\n" + "Surname: " + activity.getSurname() + "\n" + "Activity: " + activity.getActivity() + "\n" + "Duration: " + activity.getDuration() + " часов" + "\n" + "Date of publish: " + activity.getDate() + "\n- - - - - - - - - - - - -")));
            }
            document.add(table);
            document.close();
        }catch(DocumentException e){
            throw new RuntimeException(e);
        }
        return file;
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Activity", "Duration", "Date of publishing").forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }
}
