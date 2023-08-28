package io.project.SpringTgBot.config;

import io.project.SpringTgBot.service.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class BotInitializer {

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private TelegramBot telegramBot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(botConfig.getPort()))) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}