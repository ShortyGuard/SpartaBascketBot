package sg.tm.spartabasketbot.service.command;

import java.util.Arrays;
import org.glassfish.jersey.model.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import sg.tm.spartabasketbot.dto.ParameterDto;
import sg.tm.spartabasketbot.service.ParametersService;

@Component
public class ParameterUpdateCommandHandler implements BotCommandHandler {

    @Autowired
    private ParametersService parameterService;

    @Override
    public boolean handleCommand(AbsSender sender, Message updateMessage) throws TelegramApiException {
        SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
        message.setChatId(String.valueOf(updateMessage.getChatId()));
        message.enableHtml(true);
        if (updateMessage.getText().startsWith(BotCommandHandler.UPDATE_PARAMETER_ONE_COMMAND))
        {
            updateParameterOne(updateMessage.getText());
            message.setText("Параметр обновлен");
        } else {
            message.setText("Получена неизвестная команда на обновление параметра");
        }

        sender.execute(message);

        return true;
    }

    private void updateParameterOne(String text) {
        String[] params = text.split(" ");
        String newParamStringValue = String.join(" ", Arrays.copyOfRange(params, 1, params.length));

        this.parameterService.updateParameter(ParameterDto.builder()
            .id(1L)
            .name("monthly_payment_reminder_message")
            .stringValue(newParamStringValue)
            .build());
    }
}
