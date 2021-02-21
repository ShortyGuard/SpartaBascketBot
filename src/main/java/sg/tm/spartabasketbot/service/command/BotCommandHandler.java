package sg.tm.spartabasketbot.service.command;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface BotCommandHandler {
    // публичные команды
    static final String HELP_COMMAND = "/help";
    static final String PLUS_COMMAND = "/plus";
    static final String MINUS_COMMAND = "/minus";
    static final String LATER_COMMAND = "/later";
    static final String STAT_COMMAND = "/stat";

    // команды для администраторов
    static final String START_COLLECTION_COMMAND = "/start_collection";
    static final String UPDATE_PARAMETER_ONE_COMMAND = "/parameter_one_update";

    static final String BOT_COMMAND = "bot_command";

    boolean handleCommand(AbsSender sender, Message updateMessage) throws TelegramApiException;
}
