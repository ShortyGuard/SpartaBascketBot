package sg.tm.bots;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class StartBot {

    public static void main(String[] args) {
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
