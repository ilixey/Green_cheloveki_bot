package com.finalbuild.bots;

import com.finalbuild.entities.ActivityEntity;
import com.finalbuild.services.DatabaseService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

public class StatisticsBot extends TelegramLongPollingBot {

    DatabaseService databaseService = new DatabaseService();
    private List<Long> chatIdList = new LinkedList<>();

    @Override
    public String getBotToken() {
        return "5754259982:AAFBs9hH0ihQwh8vzqsZltHIU26MmS3zXbo";
    }

    @Override
    public String getBotUsername() {
        return "statistics_green_cheloveki_bot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!chatIdList.contains(update.getMessage().getChatId())){
            chatIdList.add(update.getMessage().getChatId());
        }
    }

    // Creating a pdf file with all activities from database and sending it to all users who have ever sent a message to
    // the bot.
    public void sendStatistics() throws TelegramApiException, IOException {
        List<ActivityEntity> list = databaseService.getAllActivities();
//        StringBuilder stringBuilder = new StringBuilder();
//        for (ActivityEntity activity: list){
//            stringBuilder.append("Activity id: " + activity.getId() + "\n").append("User name: " + activity.getName() + "\n").append("User surname: " + activity.getSurname() + "\n").append("Activity: " + activity.getActivity() + "\n").append("Duration: " + activity.getDuration() + " часов" + "\n").append("Date: " + activity.getDate() + "\n\n\n");
//        }
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setText(stringBuilder.toString());
//        for (Long chatId: chatIdList){
//            sendMessage.setChatId(chatId);
//            execute(sendMessage);
//        }
        File file = new File("/opt/tomcat/latest/test.pdf");
        Document document = new Document();
        try{
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            document.add(new Paragraph("List of the activities on " + list.get(list.size()).getDate() + "\n\n\n"));
            for (ActivityEntity activity: list){
                long actDate = activity.getDate().getTime();
                Date date = new Date(actDate);
                document.add((new Paragraph("Id: " + activity.getId() + "\n" + "Name: " + activity.getName() + "\n" + "Surname: " + activity.getSurname() + "\n" + "Activity: " + activity.getActivity() + "\n" + "Duration: " + activity.getDuration() + " часов" + "\n" + "Date of publish: " + date.toString() + "-------------")));

            }
            document.close();
            pdfWriter.close();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        SendDocument sendDocument = new SendDocument();
        sendDocument.setDocument(new InputFile(file));
        for (Long chatId: chatIdList){
            sendDocument.setChatId(chatId);
            execute(sendDocument);
        }

    }
}
