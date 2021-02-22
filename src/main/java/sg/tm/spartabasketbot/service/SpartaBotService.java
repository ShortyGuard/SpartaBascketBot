package sg.tm.spartabasketbot.service;

import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import sg.tm.spartabasketbot.model.TelegramUser;
import sg.tm.spartabasketbot.model.TrainingParticipant;
import sg.tm.spartabasketbot.repository.UserRepository;
import sg.tm.spartabasketbot.service.command.BotCommandHandler;
import sg.tm.spartabasketbot.service.command.CollectionCommandHandler;
import sg.tm.spartabasketbot.service.command.HelpCommandHandler;
import sg.tm.spartabasketbot.service.command.ParameterUpdateCommandHandler;
import sg.tm.spartabasketbot.service.command.StatCommandHandler;

@Service
public class SpartaBotService extends TelegramWebhookBot {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HelpCommandHandler handleHelpCommand;

    @Autowired
    private ParameterUpdateCommandHandler parameterUpdateCommandHandler;

    @Autowired
    private StatCommandHandler statCommandHandler;

    @Autowired
    private CollectionCommandHandler collectionCommandHandler;

    @Value("${bot.group.id}")
    private String botGroupId;

    @Value("${deploy.service.url}")
    private String deployServiceUrl;

    @Override
    public String getBotUsername() {
        return "SpartaBasketBot";
    }

    @Override
    public String getBotToken() {
        return "1519301677:AAECGH5XheyP40YPH1m1rm8m_lEGq9WDS3s";
    }

    @Override
    public String getBotPath() {
        return "update";
    }

    @PostConstruct
    public void postConstruct() {
        try {
            // Instantiate Telegram Bots API
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            // Register our bot
            telegramBotsApi.registerBot(this, SetWebhook.builder()
                .url(deployServiceUrl + "/update")
                .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        System.out.println("onWebhookUpdateReceived, botGroupId= " + this.botGroupId);

        if (update.hasCallbackQuery()) {
            try {
                return collectionCommandHandler.handleCallbackQuery(this, update.getCallbackQuery());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasEntities()) {
                try {
                    return handleEntities(message.getEntities(), message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Recieved update without Message or CallbackQuery: " + update);
        }
        return null;
    }

    private BotApiMethod handleEntities(List<MessageEntity> entities, Message updateMessage) throws TelegramApiException {
        for (MessageEntity entity : entities) {
            if (BotCommandHandler.BOT_COMMAND.equals(entity.getType())) {
                if (entity.getText().startsWith(BotCommandHandler.HELP_COMMAND) &&
                    updateMessage.getText().startsWith(BotCommandHandler.HELP_COMMAND)) {
                    handleHelpCommand.handleCommand(this, updateMessage);
                } else if (entity.getText().startsWith(BotCommandHandler.UPDATE_PARAMETER_ONE_COMMAND) &&
                    updateMessage.getText().startsWith(BotCommandHandler.UPDATE_PARAMETER_ONE_COMMAND)) {

                    parameterUpdateCommandHandler.handleCommand(this, updateMessage);
                } else if (entity.getText().startsWith(BotCommandHandler.PLUS_COMMAND) ||
                    entity.getText().startsWith(BotCommandHandler.MINUS_COMMAND) ||
                    entity.getText().startsWith(BotCommandHandler.LATER_COMMAND)) {

                    Optional<TelegramUser> telegramUser = this.userRepository.findById(
                        Long.valueOf(updateMessage.getFrom().getId()));
                    if (telegramUser.isPresent()) {
                        if (entity.getText().startsWith(BotCommandHandler.PLUS_COMMAND)
                            && updateMessage.getText().startsWith(BotCommandHandler.PLUS_COMMAND)) {
                            collectionCommandHandler.saveUserCollectionDecision(this, telegramUser.get(), TrainingParticipant.PLUS);
                        } else if (entity.getText().startsWith(BotCommandHandler.MINUS_COMMAND)
                            && updateMessage.getText().startsWith(BotCommandHandler.MINUS_COMMAND)) {
                            collectionCommandHandler.saveUserCollectionDecision(this, telegramUser.get(), TrainingParticipant.MINUS);
                        } else if (entity.getText().startsWith(BotCommandHandler.LATER_COMMAND)
                            && updateMessage.getText().startsWith(BotCommandHandler.LATER_COMMAND)) {
                            collectionCommandHandler.saveUserCollectionDecision(this, telegramUser.get(), TrainingParticipant.LATER);
                        }
                    } else {
                        System.out.println("Coudn't find user by id in Command: " + entity.getText());
                    }

                    return null;
                } else if (entity.getText().startsWith(BotCommandHandler.STAT_COMMAND)
                    && updateMessage.getText().startsWith(BotCommandHandler.STAT_COMMAND)) {
                    statCommandHandler.handleCommand(this, updateMessage);
                } else if (entity.getText().startsWith(BotCommandHandler.START_COLLECTION_COMMAND)
                    && updateMessage.getText().startsWith(BotCommandHandler.START_COLLECTION_COMMAND)) {
                    startTrainingCollect();
                }
            } else {
                System.out.println("Unexpecting entity.getType() == " + entity.getType());
            }
        }
        return null;
    }

    public void sendMessage(SendMessage message) {
        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public BotApiMethod recievedUpdate(Update update) {

        // сначала сохраним пользователя
        ensureUser(update);

        return this.onWebhookUpdateReceived(update);
    }

    public void startTrainingCollect() {
        collectionCommandHandler.startTrainingCollect(this);
    }

    private void ensureUser(Update update) {
        if (update.hasMessage() || update.hasCallbackQuery()) {
            User from = update.hasCallbackQuery() ? update.getCallbackQuery().getFrom() : update.getMessage().getFrom();

            if (from.getIsBot() != null && !from.getIsBot()) {
                Optional<TelegramUser> userEntity = this.userRepository.findById(Long.valueOf(from.getId()));
                TelegramUser telegramUser = new TelegramUser();
                telegramUser.setId(Long.valueOf(from.getId()));
                telegramUser.setFirstName(from.getFirstName());
                telegramUser.setLastName(from.getLastName());
                telegramUser.setUserName(from.getUserName());
                telegramUser.setMessagesCount(userEntity.map(user -> user.getMessagesCount() + 1).orElse(1L));

                this.userRepository.save(telegramUser);
            }
        }
    }

    public void notifyAllNotAnsweredUsers() {
        this.collectionCommandHandler.notifyAllNotAnsweredUsers(this, DateUtil.getCurrentDate());
    }

    public void notifyAllWaitingUsers() {
        try {
            this.collectionCommandHandler.notifyAllWaitingUsers(this, DateUtil.getCurrentDate());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
