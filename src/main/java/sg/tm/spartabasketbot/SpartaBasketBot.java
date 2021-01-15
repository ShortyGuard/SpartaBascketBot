package sg.tm.spartabasketbot;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static java.lang.Thread.sleep;

@Deprecated
@Component
public class SpartaBasketBot extends TelegramWebhookBot {

    @Value("${bot.group.id}")
    private String botGroupId;

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
        System.out.println("onWebhookUpdateReceived, botGroupId= " + this.botGroupId);

        if (update.hasMessage()){

        }else{

        }

/*        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            message.setText(update.getMessage().getText());

          //  setButtons(message); // это кнопки в ответе
            setInline(message); // это кнопки в сообщении

            System.out.println("Sent answer");
*//*
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
            }).start();*//*

            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }*/
        return null;
    }

    @Override
    public String getBotPath() {
        return "update";
    }

    private void setInline(SendMessage sendMessage) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Кнопка");
        inlineKeyboardButton.setCallbackData("17");
        buttons1.add(inlineKeyboardButton);
        buttons.add(buttons1);

        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);

        sendMessage.setReplyMarkup(markupKeyboard);
    }

    public synchronized void setButtons(SendMessage sendMessage) {

// Создаем клавиуатуру

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);


// Создаем список строк клавиатуры

        List<KeyboardRow> keyboard = new ArrayList<>();


// Первая строчка клавиатуры

        KeyboardRow keyboardFirstRow = new KeyboardRow();

// Добавляем кнопки в первую строчку клавиатуры

        keyboardFirstRow.add(new KeyboardButton("Привет"));


// Вторая строчка клавиатуры

        KeyboardRow keyboardSecondRow = new KeyboardRow();

// Добавляем кнопки во вторую строчку клавиатуры

        keyboardSecondRow.add(new KeyboardButton("Помощь"));


// Добавляем все строчки клавиатуры в список

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);

// и устанваливаем этот список нашей клавиатуре

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

}
