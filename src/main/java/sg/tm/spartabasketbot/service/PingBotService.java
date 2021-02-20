package sg.tm.spartabasketbot.service;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Service
public class PingBotService extends TelegramWebhookBot {

    @Override
    public String getBotUsername() {
        return "PingSpartaBot";
    }

    @Override
    public String getBotToken() {
        return "1557992844:AAHODRfok11EPX1iWOPdTlpZKp6QlB57S8c";
    }

    @Override
    public String getBotPath() {
        return "/ping/update";
    }

    @Value("${pingbot.group.id}")
    private String botGroupId;

    @PostConstruct
    public void postConstruct() {
        try {
            // Instantiate Telegram Bots API
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            // Register our bot
            telegramBotsApi.registerBot(this, SetWebhook.builder()
                .url("https://stormy-oasis-06121.herokuapp.com/ping/update")
                .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Scheduled(fixedRate = 900000)
    private void ping() {
        SendMessage message = new SendMessage();
        message.setChatId(botGroupId);
        message.setText("Не спать!!!");

        try {
            this.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
