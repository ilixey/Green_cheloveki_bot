package com.finalbuild.bots;

import com.finalbuild.entities.ActivityEntity;
import com.finalbuild.services.DatabaseService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class StatisticsBot extends TelegramLongPollingBot {

    DatabaseService databaseService = new DatabaseService();
    private List<Long> chatIdList = new LinkedList<>();

    @Override
    public String getBotToken() {
        return "5754259982:AAGPAIbnLkP0hsgClgrvXqGuIvVw4Yj1Czw";
    }

    @Override
    public String getBotUsername() {
        return "statistics_green_cheloveki_bot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!chatIdList.contains(update.getMessage().getChatId())){
            chatIdList.add(update.getMessage().getChatId());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Вы успешно добавлены в список рассылки! Ожидайте получения pdf-файла с активностью сотрудников за день.");
            sendMessage.setChatId(update.getMessage().getChatId());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            try {
                sendStatistics();
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
        File file = new File("opt/tomcat/latest/bin/test.pdf");
        Document document = new Document();
        try{
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            document.add(new Paragraph("List of the activities on " + LocalDate.now() + "\n\n\n"));
            for (ActivityEntity activity: list){
                document.add((new Paragraph("Id: " + activity.getId() + "\n" + "Name: " + activity.getName() + "\n" + "Surname: " + activity.getSurname() + "\n" + "Activity: " + activity.getActivity() + "\n" + "Duration: " + activity.getDuration() + " часов" + "\n" + "Date of publish: " + activity.getDate() + "\n- - - - - - - - - - - - -")));
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
