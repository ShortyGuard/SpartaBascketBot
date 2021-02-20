package sg.tm.spartabasketbot.service.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class HelpCommandHandler implements BotCommandHandler {

    @Override
    public boolean handleCommand(AbsSender sender, Message updateMessage) throws TelegramApiException {
        SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
        message.setChatId(String.valueOf(updateMessage.getChatId()));
        message.enableHtml(true);
        message.setText("Вас приветствует <b>SpartaBasketBot</b>" +
            "\n<u>Я умею обрабатывать следующие команды</u>:\n" +
            "<b>/help</b> - получить это сообщение о помощи\n" +
            "<b>/plus</b> - отметиться 'я играю' в сборах на сегодняшнюю тренировку\n" +
            "<b>/minus</b> - отметиться 'я НЕ играю' в сборах на сегодняшнюю тренировку\n" +
            "<b>/later</b> - отметиться 'отвечу позже' в сборах на сегодняшнюю тренировку\n" +
            "<b>/stat</b> - получить статистику по сборам на тренировку\n");

        sender.execute(message);

        return true;
    }
}
