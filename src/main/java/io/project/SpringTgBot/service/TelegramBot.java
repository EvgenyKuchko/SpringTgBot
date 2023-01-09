package io.project.SpringTgBot.service;

import io.project.SpringTgBot.config.BotConfig;
import io.project.SpringTgBot.exception.BadWordFormat;
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
    static final String REMOVE_COMMAND = "/remove";

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
            var message = update.getMessage().getText();
            var firstname = update.getMessage().getChat().getFirstName();
            var chatId = update.getMessage().getChatId();

            if (message.equals("/start")) {
                userService.addNewUser(chatId, firstname);
                startCommandAnswer(chatId, firstname);
            } else if (message.equals("/get")) {
                var answer = userService.getAllWords(chatId);
                sendAnswer(chatId, answer);
            } else if (message.contains("/add")) {
                try {
                    var words = parseMessage(message, ADD_COMMAND);
                    var answer = addNewWordToDictionary(words, chatId);
                    sendAnswer(chatId, answer);
                } catch (BadWordFormat ex) {
                    sendAnswer(chatId, ex.getMessage());
                }
            } else if (message.contains("/remove")) {
                try {
                    var words = parseMessage(message, REMOVE_COMMAND);
                    var answer = removeWordFromUserDictionary(words, chatId);
                    sendAnswer(chatId, answer);
                } catch (BadWordFormat ex) {
                    sendAnswer(chatId, ex.getMessage());
                }
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

    private String[] parseMessage(String message, String command) throws BadWordFormat {
        message = message.toLowerCase()
                .replaceAll(" ", "")
                .replace(command, "");
        String[] words = message.split("-");
        checkWords(words);
        return words;
    }

    public String[] checkWords(String[] words) throws BadWordFormat {
        if (words[0].matches("[a-z]+") && words[1].matches("[а-я]+")) {
            return words;
        } else if (words[1].matches("[a-z]+") && words[0].matches("[а-я]+")) {
            String x = words[0];
            words[0] = words[1];
            words[1] = x;
            return words;
        }
        throw new BadWordFormat("Wrong format for entering words. Check if the command with the pattern is entered correctly and try again.");
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

    private String removeWordFromUserDictionary(String[] words, long chatId) {
        String answer = "The word was successfully removed";
        if (!userService.isWordInUserDictionary(chatId, words[0], words[1])) {
            return "There is no such word in your dictionary.";
        }
        userService.removeWord(chatId, words[0], words[1]);
        return answer;
    }
}