package io.project.SpringTgBot.service;

import com.vdurmont.emoji.EmojiParser;
import io.project.SpringTgBot.config.BotConfig;
import io.project.SpringTgBot.exception.BadWordFormat;
import io.project.SpringTgBot.model.Word;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig botConfig;

    private static final String START_COMMAND = "/start";
    private static final String ADD_COMMAND = "/add";
    private static final String REMOVE_COMMAND = "/remove";
    private static final String HELP_COMMAND = "/help";
    private static final String GET_COMMAND = "/get";
    private static final String QUIZ_COMMAND = "/quiz";
    private static final String HELP = ":robot_face: This bot knows the following commands :\n\n" +
            "'/get' with this command you will get a list of all the words in your dictionary \n\n" +
            "'/add play - играть'  with this command you can add a new word to your dictionary \n\n" +
            "'/remove play - играть' with this command you can remove a word from your dictionary \n\n" +
            "'/quiz' with this command the bot will create a quiz consisting of your vocabulary words with variants of answers\n\n" +
            "'/help' with this command you can get information about bot commands";

    private static final int NUM_OF_QUESTIONS = 5;
    private static String result;
    private static List<Word> wordsForQuiz;
    private static int countOfCorrectAnswers;
    private static int countOfQuestion;

    @Autowired
    private UserService userService;

    @Autowired
    private WordService wordService;

    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/get", "get your words"));
        commands.add(new BotCommand("/quiz", "take a quiz"));
        commands.add(new BotCommand("/help", "information about commands"));
        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
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
            log.info("Message from " + firstname + ", with text: " + message);

            if (message.equals(START_COMMAND)) {
                userService.addNewUser(chatId, firstname);
                startCommandAnswer(chatId, firstname);
            } else if (message.equals(HELP_COMMAND)) {
                sendAnswer(chatId, HELP);
            } else if (message.equals(GET_COMMAND)) {
                var answer = userService.getAllWords(chatId);
                sendAnswer(chatId, answer);
            } else if (message.contains(ADD_COMMAND)) {
                try {
                    var words = parseMessage(message, ADD_COMMAND);
                    var answer = addNewWordToDictionary(words, chatId);
                    sendAnswer(chatId, answer);
                } catch (BadWordFormat ex) {
                    log.warn(ex.getMessage());
                    sendAnswer(chatId, ex.getMessage());
                }
            } else if (message.contains(REMOVE_COMMAND)) {
                try {
                    var words = parseMessage(message, REMOVE_COMMAND);
                    var answer = removeWordFromUserDictionary(words, chatId);
                    sendAnswer(chatId, answer);
                } catch (BadWordFormat ex) {
                    log.warn(ex.getMessage());
                    sendAnswer(chatId, ex.getMessage());
                }
            } else if (message.equals(QUIZ_COMMAND)) {
                if (userService.getSizeOfDictionary(chatId) < 5) {
                    sendAnswer(chatId, "Your dictionary is too small. You need at least 5 words to complete the test.");
                } else {
                    result = "";
                    countOfQuestion = 0;
                    countOfCorrectAnswers = 0;
                    wordsForQuiz = new LinkedList<>(userService.getWordsForQuiz(chatId));
                    var questionWord = getQuestionWord();
                    var variants = getVariants(questionWord);
                    sendQuestion(chatId, questionWord, variants);
                    countOfQuestion++;
                }
            } else {
                sendAnswer(chatId, "The command is not supported.:sweat: Use the command '/help' to find out all available commands.");
            }
        } else if (update.hasCallbackQuery()) {
            //получаем id сообщение которое будет изменено
            log.info("Get answer to the question");
            var callBackData = update.getCallbackQuery().getData();
            var english = update.getCallbackQuery().getMessage().getText();
            var messageId = update.getCallbackQuery().getMessage().getMessageId();
            var idOfChat = update.getCallbackQuery().getMessage().getChatId();

            if (answerIsCorrect(english, callBackData)) {
                countOfCorrectAnswers++;
                log.info("Count of correct answers: " + countOfCorrectAnswers);
                result += english + " - " + callBackData + "  " + ":white_check_mark:\n";
            } else {
                result += english + " - " + callBackData + "  " + ":x:\n";
            }

            if (countOfQuestion < NUM_OF_QUESTIONS) {
                nextQuestion(idOfChat, messageId);
                countOfQuestion++;
            } else {
                sendResultOfQuiz(idOfChat, messageId);
            }
        }
    }

    private void startCommandAnswer(long chatId, String name) {
        var answer = "Hello, " + name + "! Welcome to Dictionary Bot.:wave:\n\n" +
                HELP;
        sendAnswer(chatId, answer);
        log.info(name + " started chat");
    }

    private void sendAnswer(long chatId, String answer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(EmojiParser.parseToUnicode(answer));
        try {
            execute(sendMessage);
            log.info("Send answer");
        } catch (TelegramApiException ex) {
            log.error("There was an error: " + ex.getMessage());
        }
    }

    private String[] parseMessage(String message, String command) throws BadWordFormat {
        message = message.toLowerCase()
                .replaceAll(" ", "")
                .replace(command, "");
        String[] words = message.split("-");
        checkWords(words);
        log.info("Get word(" + words[0] + " - " + words[1] + ") from user's message");
        return words;
    }

    public void checkWords(String[] words) throws BadWordFormat {
        log.info("Check word for accuracy");
        if (words.length == 2) {
            if (words[0].matches("[a-z]+") && words[1].matches("[а-я]+")) {
                return;
            } else if (words[1].matches("[a-z]+") && words[0].matches("[а-я]+")) {
                var x = words[0];
                words[0] = words[1];
                words[1] = x;
                return;
            }
        }
        throw new BadWordFormat("Wrong format for entering words. Check if the command with the pattern is entered correctly and try again.");
    }

    private String addNewWordToDictionary(String[] words, long chatId) {
        log.info("Add new word(" + words[0] + " - " + words[1] + ") to user dictionary");
        String answer = "The word was successfully added";
        if (userService.isWordInUserDictionary(chatId, words[0], words[1])) {
            log.info("Word(" + words[0] + " - " + words[1] + ") is already in the user dictionary");
            return "This word is already exist in your dictionary";
        }
        if (wordService.isWordInBotDictionary(words[0], words[1])) {
            userService.addNewWordToDictionary(chatId, wordService.getWordFromBotDictionary(words[0], words[1]));
            log.info("Word(" + words[0] + " - " + words[1] + ") added in the user dictionary");
            return answer;
        }
        wordService.addNewWordToDictionary(words[0], words[1]);
        userService.addNewWordToDictionary(chatId, wordService.getWordFromBotDictionary(words[0], words[1]));
        log.info("Word(" + words[0] + " - " + words[1] + ") added in the user dictionary");
        return answer;
    }

    private String removeWordFromUserDictionary(String[] words, long chatId) {
        log.info("Remove word(" + words[0] + " - " + words[1] + ") from dictionary");
        String answer = "The word was successfully removed";
        if (!userService.isWordInUserDictionary(chatId, words[0], words[1])) {
            log.info("Word(" + words[0] + " - " + words[1] + ") isn't in the dictionary");
            return "There is no such word in your dictionary.";
        }
        userService.removeWord(chatId, words[0], words[1]);
        return answer;
    }

    private Word getQuestionWord() {
        log.info("Get question word for quiz");
        return wordsForQuiz.get(countOfQuestion);
    }

    private String[] getVariants(Word questionWord) {
        log.info("Get variants for word " + questionWord);
        var words = new ArrayList<>(wordsForQuiz);
        words.remove(questionWord);
        Collections.shuffle(words);
        String[] variants = new String[3];
        for (int i = 0; i < 2; i++) {
            variants[i] = words.get(i).getRussian();
        }
        variants[2] = questionWord.getRussian();
        return shuffleVariants(variants);
    }

    private String[] shuffleVariants(String[] arr) {
        log.info("Shuffle variants: " + arr[0] + ", " + arr[1] + ", " + arr[2]);
        Random rnd = new Random();
        for (int i = 0; i < arr.length; i++) {
            int index = rnd.nextInt(i + 1);
            var a = arr[index];
            arr[index] = arr[i];
            arr[i] = a;
        }
        return arr;
    }

    private void sendQuestion(long chatId, Word questionWord, String[] variants) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(questionWord.getEnglish());

        InlineKeyboardMarkup keyboard = createKeyboard(variants);
        message.setReplyMarkup(keyboard);
        log.info("Add keyboard with answers and question to message and send");
        try {
            execute(message);
        } catch (TelegramApiException ex) {
            log.error("There was an error: " + ex.getMessage());
        }
    }

    private boolean answerIsCorrect(String english, String answer) {
        log.info("Check: " + english + " have translate - " + answer);
        for (Word w : wordsForQuiz) {
            if (w.getEnglish().equals(english) && w.getRussian().equals(answer)) {
                log.info("Answer is correct");
                return true;
            }
        }
        log.info("Answer isn't correct");
        return false;
    }

    private void nextQuestion(long chatId, int messageId) {
        var questionWord = getQuestionWord();
        var variants = getVariants(questionWord);
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(questionWord.getEnglish());
        message.setMessageId(messageId);

        InlineKeyboardMarkup keyboard = createKeyboard(variants);
        message.setReplyMarkup(keyboard);
        log.info("Add keyboard with answers and question to message and send");
        try {
            execute(message);
        } catch (TelegramApiException ex) {
            log.error("There was an error: " + ex.getMessage());
        }
    }

    private void sendResultOfQuiz(long chatId, int messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(EmojiParser.parseToUnicode("Number of correct answers: " + countOfCorrectAnswers + " out of " + NUM_OF_QUESTIONS + "\n\n" +
                result));
        message.setMessageId(messageId);
        log.info("Send result of quiz");
        try {
            execute(message);
        } catch (TelegramApiException ex) {
            log.error("There was an error: " + ex.getMessage());
        }
    }

    private InlineKeyboardMarkup createKeyboard(String[] variants) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (String variant : variants) {
            var button = new InlineKeyboardButton();
            button.setText(variant);
            button.setCallbackData(variant);
            row.add(button);
        }
        rows.add(row);
        keyboard.setKeyboard(rows);
        log.info("Create keyboard with variants: " + variants[0] + ", " + variants[1] + ", " + variants[2]);
        return keyboard;
    }
}