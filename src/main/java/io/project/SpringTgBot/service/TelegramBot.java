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

    static final String ADD_COMMAND = "/add";

    @Autowired
    private UserService userService;

    @Autowired
    private WordService wordService;

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
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            var firstname = update.getMessage().getChat().getFirstName();
            var chatId = update.getMessage().getChatId();

            if (message.equals("/start")) {
                userService.addNewUser(chatId, firstname);
                startCommandAnswer(chatId, firstname);
            } else if (message.equals("/get")) {
                var answer = userService.getAllWords(chatId);
                sendAnswer(chatId, answer);
            } else if (message.contains("/add")) {
                String[] words = parseMessage(message, ADD_COMMAND);
                String answer = addNewWordToDictionary(words, chatId);
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
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    private String[] parseMessage(String message, String command) {
        message = message.toLowerCase()
                .replaceAll(" ", "")
                .replace(command, "")
                .replace("(", "")
                .replace(")", "");
        String[] words = message.split("-");
        for (String s : words) {
            System.out.println(s);
        }
        return words;
    }

    private String addNewWordToDictionary(String[] words, long chatId) {
        String answer = "The word was successfully added";
        if (userService.isWordInUserDictionary(chatId, words[0], words[1])) {
            return "This word is already exist in your dictionary";
        }
        if (wordService.isWordInBotDictionary(words[0], words[1])) {
            userService.addNewWordToDictionary(chatId, wordService.getWordFromBotDictionary(words[0], words[1]));
            return answer;
        }
        wordService.addNewWordToDictionary(words[0], words[1]);
        userService.addNewWordToDictionary(chatId, wordService.getWordFromBotDictionary(words[0], words[1]));
        return answer;
    }
}