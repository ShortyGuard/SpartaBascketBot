package sg.tm.spartabasketbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class SpartaBasketBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpartaBasketBotApplication.class, args);

        try {
            // Instantiate Telegram Bots API
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            // Register our bot
            telegramBotsApi.registerBot(new SpartaBasketBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
