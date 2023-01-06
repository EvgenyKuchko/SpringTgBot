package io.project.SpringTgBot.service;

import io.project.SpringTgBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig botConfig;

    @Autowired
    private UserService userService;

    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            var firstname = update.getMessage().getChat().getFirstName();
            var chatId = update.getMessage().getChatId();

            if(message.equals("/start")) {
                userService.addNewUser(chatId, firstname);
                startCommandAnswer(chatId, firstname);
            }else if(message.equals("/get")) {
                var answer = userService.getAllWords(chatId);
                sendAnswer(chatId, answer);
            }
        }
    }

    private void startCommandAnswer(long chatId, String name) {
        var answer = "Hello, " + name + "! Welcome to Dictionary Bot.";
        sendAnswer(chatId, answer);
    }

    private void sendAnswer(long chatId, String answer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(answer);
        try {
            execute(sendMessage);
        }catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }
}