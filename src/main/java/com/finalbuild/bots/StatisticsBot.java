package com.finalbuild.bots;

import com.finalbuild.services.PdfConverterService;
import com.itextpdf.text.DocumentException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class StatisticsBot extends TelegramLongPollingBot {

    private final PdfConverterService pdfConverter = new PdfConverterService();
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
        }else{
            if (update.getMessage().getText().equals("Stats")){
                try {
                    File file = pdfConverter.createPdf();
                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setDocument(new InputFile(file));
                    sendDocument.setChatId(update.getMessage().getChatId());
                    execute(sendDocument);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (DocumentException e) {
                    throw new RuntimeException(e);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Сервер работает, ожидайте получения статистики.");
                sendMessage.setChatId(update.getMessage().getChatId());
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    // Creating a pdf file with all activities from database and sending it to all users who have ever sent a message to
    // the bot.
    public void sendStatistics() throws TelegramApiException, IOException, DocumentException {
        File file = pdfConverter.createPdf();
        SendDocument sendDocument = new SendDocument();
        sendDocument.setDocument(new InputFile(file));
        for (Long chatId: chatIdList){
            sendDocument.setChatId(chatId);
            execute(sendDocument);
        }

    }
}
