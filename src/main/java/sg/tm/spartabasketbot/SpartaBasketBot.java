package sg.tm.spartabasketbot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static java.lang.Thread.sleep;

public class SpartaBasketBot extends TelegramWebhookBot {
    @Override
    public String getBotUsername() {
        return "SpartaBasketBot";
    }

    @Override
    public String getBotToken() {
        return "1519301677:AAECGH5XheyP40YPH1m1rm8m_lEGq9WDS3s";
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        System.out.println("onWebhookUpdateReceived: update = " + update);

        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            message.setText(update.getMessage().getText());
            System.out.println("Sent answer");

            new Thread(() -> {
                try {
                    sleep(5000);
                    SendMessage newMessage = new SendMessage(); // Create a SendMessage object with mandatory fields
                    newMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
                    newMessage.setText("@" + update.getMessage().getFrom().getUserName() + " "
                        + update.getMessage().getText());
                    System.out.println("Sent personal answer");

                    execute(newMessage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }).start();

            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getBotPath() {
        return "update";
    }

    /*public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        System.out.println("update = " + update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            message.setText(update.getMessage().getText());
            System.out.println("Sent answer");

            new Thread(() -> {
                try {
                    sleep(5000);
                    SendMessage newMessage = new SendMessage(); // Create a SendMessage object with mandatory fields
                    newMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
                    newMessage.setText("@" + update.getMessage().getFrom().getUserName() + " "
                        + update.getMessage().getText());
                    System.out.println("Sent personal answer");

                    execute(newMessage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }).start();

            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBotUsername() {
        return "SpartaBasketBot";
    }

    @Override
    public String getBotToken() {
        return "1519301677:AAECGH5XheyP40YPH1m1rm8m_lEGq9WDS3s";
    }*/
}
