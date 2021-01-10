package sg.tm.spartabasketbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class SpartaBasketBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpartaBasketBotApplication.class, args);
    }

    @Bean
    public SpartaBasketBot getSpartaBasketBot() {
        SpartaBasketBot spartaBasketBot = new SpartaBasketBot();

        try {
            // Instantiate Telegram Bots API
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            // Register our bot
            telegramBotsApi.registerBot(spartaBasketBot,  SetWebhook.builder()
                .url("https://stormy-oasis-06121.herokuapp.com/update")
                .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return spartaBasketBot;
    }
}
