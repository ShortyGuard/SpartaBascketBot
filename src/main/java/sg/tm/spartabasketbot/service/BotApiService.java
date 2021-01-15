package sg.tm.spartabasketbot.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import sg.tm.spartabasketbot.model.TelegramUser;
import sg.tm.spartabasketbot.model.Training;
import sg.tm.spartabasketbot.model.TrainingParticipant;
import sg.tm.spartabasketbot.repository.TrainingParticipantRepository;
import sg.tm.spartabasketbot.repository.TrainingRepository;
import sg.tm.spartabasketbot.repository.UserRepository;

@Service
public class BotApiService extends TelegramWebhookBot implements IBotApiService {
    private static final String HELP_COMMAND = "/help";
    private static final String PLUS_COMMAND = "/plus";
    private static final String MINUS_COMMAND = "/minus";
    private static final String LATER_COMMAND = "/later";
    private static final String STAT_COMMAND = "/stat";
    private static final String START_COLLECTION_COMMAND = "/start_collection";
    private static final String BOT_COMMAND = "bot_command";

    private static final String CALLBACK_PLUS = "20a6615f-e44a-42c4-9cab-9326eef38b9c";
    private static final String CALLBACK_MINUS = "a9c677fa-1083-4615-a1e0-0ef3932af210";
    private static final String CALLBACK_LATER = "b2e7e540-051c-4ed1-b8d4-52834ccf8dee";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TrainingParticipantRepository trainingParticipantRepository;

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
                .url("https://stormy-oasis-06121.herokuapp.com/update")
                .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        System.out.println("onWebhookUpdateReceived, botGroupId= " + this.botGroupId);

        if (update.hasCallbackQuery()) {
            return handleCallbackQuery(update.getCallbackQuery());

        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasEntities()) {
                return handleEntities(message.getEntities(), message);
            }
        } else {
            System.out.println("Recieved update without Message or CallbackQuery: " + update);
        }
        return null;
    }

    private BotApiMethod handleCallbackQuery(CallbackQuery callbackQuery) {
        Optional<TelegramUser> telegramUser = this.userRepository.findById(
            Long.valueOf(callbackQuery.getFrom().getId()));
        if (telegramUser.isPresent()) {
            if (CALLBACK_PLUS.equals(callbackQuery.getData())) {
                saveUserCollectionDecision(telegramUser.get(), TrainingParticipant.PLUS);
            } else if (CALLBACK_MINUS.equals(callbackQuery.getData())) {
                saveUserCollectionDecision(telegramUser.get(), TrainingParticipant.MINUS);
            } else if (CALLBACK_LATER.equals(callbackQuery.getData())) {
                saveUserCollectionDecision(telegramUser.get(), TrainingParticipant.LATER);
            }
        } else {
            System.out.println("Coudn't find user by id in CallbackQuery: " + callbackQuery);
        }

        return null;
    }

    private void saveUserCollectionDecision(TelegramUser telegramUser, String decision) {
        String currentDate = getCurrentDate();

        Training training = this.trainingRepository.findOneByDate(currentDate);
        if (training != null) {
            TrainingParticipant trainingParticipant =
                this.trainingParticipantRepository.findOneByuserIdAndTrainingId(telegramUser.getId(), training.getId());

            if (trainingParticipant == null) {
                trainingParticipant = new TrainingParticipant();
            }
            if (decision.equals(trainingParticipant.getDecision())) {
                System.out.println("Already VOTED!!!  telegramUser = " + telegramUser + ", decision = " + decision);
                SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
                message.setChatId(String.valueOf(telegramUser.getId()));
                String decisionText = TrainingParticipant.PLUS.equals(decision) ? "'Я иду'" : (
                    TrainingParticipant.MINUS.equals(decision) ? "'Я НЕ иду'" :
                        "'Отвечу позже'");
                message.setText("Ответ " + decisionText + " уже учтен ранее. (я понял с первого раза.)");

                sendMessage(message);
                return;
            }
            trainingParticipant.setUserId(telegramUser.getId());
            trainingParticipant.setTrainingId(training.getId());
            trainingParticipant.setDecision(decision);

            this.trainingParticipantRepository.save(trainingParticipant);

            System.out.println("GET VOTE!!!  telegramUser = " + telegramUser + ", decision = " + decision);
            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(String.valueOf(telegramUser.getId()));
            String decisionText = TrainingParticipant.PLUS.equals(decision) ? "'Я иду'" :
                (TrainingParticipant.MINUS.equals(decision) ? "'Я НЕ иду'" :
                    "'Отвечу позже'");
            message.setText("Ответ " + decisionText + " учтен. Спасибо за вашу отзывчивость.");

            sendMessage(message);

            message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(botGroupId);
            message.enableHtml(true);
            String everyBodyNotificationText = telegramUser.getUserNameForMention() +
                " ответил " +
                (TrainingParticipant.PLUS.equals(decision) ? "'Я иду'" : (
                    TrainingParticipant.MINUS.equals(decision) ? "'Я НЕ иду'" :
                        "'Отвечу позже'"));

            everyBodyNotificationText += "\nТекущее состояние по сборам на тренировку:\n" +getStatInfo();
            message.setText(everyBodyNotificationText);

            sendMessage(message);

        } else {
            System.out.println("GET VOTE!!!  telegramUser = " + telegramUser + ", decision = " + decision);
            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(String.valueOf(telegramUser.getId()));

            message.setText("Сбор на тренировку еще не начался.\n" +
                "Ждем когда кто-то из админов соизволит мне об этом сказать.");

            sendMessage(message);
        }
    }

    private BotApiMethod handleEntities(List<MessageEntity> entities, Message updateMessage) {
        for (MessageEntity entity : entities) {
            if (BOT_COMMAND.equals(entity.getType())) {
                if (entity.getText().startsWith(HELP_COMMAND)) {

                    SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
                    message.setChatId(botGroupId);
                    message.enableHtml(true);
                    message.setText("Вас приветствует " + getBotUsername() +
                        "\nЯ умею обрабатывать следующие команды:\n" +
                            "<b>/help</b> - получть это сообщение о помощи\n" +
                            "<b>/plus</b> - отметиться 'я играю' в сборах на сегодняшнюю тренировку\n" +
                            "<b>/minus</b> - отметиться 'я НЕ играю' в сборах на сегодняшнюю тренировку\n" +
                            "<b>/later</b> - отметиться 'отвечу позже' в сборах на сегодняшнюю тренировку\n" +
                            "<b>/stat</b> - получть статистику по сборам на тренировку\n");

                    sendMessage(message);
                } else if (entity.getText().startsWith(PLUS_COMMAND) ||
                    entity.getText().startsWith(MINUS_COMMAND) ||
                    entity.getText().startsWith(LATER_COMMAND)) {

                    Optional<TelegramUser> telegramUser = this.userRepository.findById(
                        Long.valueOf(updateMessage.getFrom().getId()));
                    if (telegramUser.isPresent()) {
                        if (entity.getText().startsWith(PLUS_COMMAND)) {
                            saveUserCollectionDecision(telegramUser.get(), TrainingParticipant.PLUS);
                        } else if (entity.getText().startsWith(MINUS_COMMAND)) {
                            saveUserCollectionDecision(telegramUser.get(), TrainingParticipant.MINUS);
                        } else if (entity.getText().startsWith(LATER_COMMAND)) {
                            saveUserCollectionDecision(telegramUser.get(), TrainingParticipant.LATER);
                        }
                    } else {
                        System.out.println("Coudn't find user by id in Command: " + entity.getText());
                    }

                    return null;
                } else if (entity.getText().startsWith(STAT_COMMAND)) {
                    handleStatCommand(updateMessage.getChatId());
                } else if (entity.getText().startsWith(START_COLLECTION_COMMAND)) {
                    startTrainingCollect();
                }
            } else {
                System.out.println("Unexpecting entity.getType() == " + entity.getType());
            }
        }
        return null;
    }

    private void handleStatCommand(Long chatId) {
        String statInfo = getStatInfo();
        SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
        message.setChatId(String.valueOf(chatId));
        message.enableHtml(true);
        message.setText("Текущее состояние по сборам на тренировку:\n" + statInfo);

        sendMessage(message);
    }

    private String getStatInfo() {

        String currentDate = getCurrentDate();

        Training training = this.trainingRepository.findOneByDate(currentDate);
        if (training != null) {
            //получим список всех пользователей
            List<TelegramUser> allUsers = userRepository.findAll();

            //получим список уже проголосовавших пользователей
            List<TrainingParticipant> allByTrainingId = trainingParticipantRepository.findAllByTrainingId(training.getId());

            List<TelegramUser> plusUsers = new ArrayList<>();
            List<TelegramUser> minusUsers = new ArrayList<>();
            List<TelegramUser> laterUsers = new ArrayList<>();

            for (TrainingParticipant trainingParticipant : allByTrainingId) {
                for (int i = 0; i < allUsers.size(); i++) {
                    TelegramUser user = allUsers.get(i);
                    if (user.getId().equals(trainingParticipant.getUserId())) {
                        System.out.println("Нашли пользователя");
                        if (TrainingParticipant.PLUS.equals(trainingParticipant.getDecision())) {
                            plusUsers.add(user);
                            allUsers.remove(user);
                            break;
                        } else if (TrainingParticipant.MINUS.equals(trainingParticipant.getDecision())) {
                            minusUsers.add(user);
                            allUsers.remove(user);
                            break;
                        } else if (TrainingParticipant.LATER.equals(trainingParticipant.getDecision())) {
                            laterUsers.add(user);
                            allUsers.remove(user);
                            break;
                        }
                    }
                }
            }

            String answer = "Идут на тренировку:\n";
            for (int i = 0; i < plusUsers.size(); i++) {
                TelegramUser user = plusUsers.get(i);
                answer += " " + (i + 1) + ". " + user.getUserNameForMention() + "\n";
            }
            answer += "НЕ идут на тренировку:\n";
            for (int i = 0; i < minusUsers.size(); i++) {
                TelegramUser user = minusUsers.get(i);
                answer += " " + (i + 1) + ". " + user.getUserNameForMention() + "\n";
            }
            answer += "Ответят позже:\n";
            for (int i = 0; i < laterUsers.size(); i++) {
                TelegramUser user = laterUsers.get(i);
                answer += " " + (i + 1) + ". " + user.getUserNameForMention() + "\n";
            }
            answer += "НЕ ПРОГОЛОСОВАЛИ (ай-яй-яй):\n";
            for (int i = 0; i < allUsers.size(); i++) {
                TelegramUser user = allUsers.get(i);
                answer += " " + (i + 1) + ". " + user.getUserNameForMention() + "\n";
            }

            return answer;

        }
        return "Сбор на тренировку еще не начался.\n" +
            "Ждем когда кто-то из админов соизволит мне об этом сказать.";
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BotApiMethod recievedUpdate(Update update) {

        // сначала сохраним пользователя
        if (update.hasMessage() || update.hasCallbackQuery()) {
            User from = update.hasCallbackQuery() ? update.getCallbackQuery().getFrom() : update.getMessage().getFrom();

            Optional<TelegramUser> userEntity = this.userRepository.findById(Long.valueOf(from.getId()));
            TelegramUser telegramUser = new TelegramUser();
            telegramUser.setId(Long.valueOf(from.getId()));
            telegramUser.setFirstName(from.getFirstName());
            telegramUser.setLastName(from.getLastName());
            telegramUser.setUserName(from.getUserName());
            telegramUser.setMessagesCount(userEntity.map(user -> user.getMessagesCount() + 1).orElse(1L));

            this.userRepository.save(telegramUser);
        }

        return this.onWebhookUpdateReceived(update);
    }

    @Override
    public void startTrainingCollect() {
        String currentDate = getCurrentDate();

        //сначала проверить, что текущего открытого сбора нет
        Training training = this.trainingRepository.findOneByDate(currentDate);

        if (training == null) {
            //создать сбор на сегодня
            training = new Training();
            training.setDate(currentDate);
            this.trainingRepository.save(training);

            //оповестить всех пользователей кто еще не ответил на сбор
            notifyAllNotAnsweredUsers(currentDate);

            //оповестить в общий чат, что сбор начался
        }

    }

    private void notifyAllNotAnsweredUsers(String date) {
        //получим неответивших пользователей
        List<TelegramUser> users = this.userRepository.findAllNotAnsweredUsers(date);

        StringBuffer stringBuffer = new StringBuffer("Сегодня тренировка. Ждем ответа об участии от ");
        for (TelegramUser user : users) {
            System.out.println("не ответил: " + user.getId());
            // TODO: 12.01.2021 тут надо вставить личную нотификацию
            notifyNotAnsweredUser(String.valueOf(user.getId()), "Сегодня тренировка. Ждем ответа об участии. " +
                "\nОтветь боту (см. /help) лично или нажмите на одну из кнопок ниже.");

            stringBuffer.append(user.getUserNameForMention());
            stringBuffer.append(" ");
        }
        stringBuffer.append("\n Ответье боту (см. /help) лично или нажмите на одну из кнопок ниже.");

        notifyNotAnsweredUser(botGroupId, stringBuffer.toString());
    }

    private void notifyNotAnsweredUser(String chatId, String text) {
        SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
        message.setChatId(chatId);
        message.setText(text);

        //  setButtons(message); // это кнопки в ответе
        setInlineForCollection(message); // это кнопки в сообщении

        sendMessage(message);
    }

    private void setInlineForCollection(SendMessage message) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        // кнопка иду
        InlineKeyboardButton plusButton = new InlineKeyboardButton();
        plusButton.setText("Иду");
        plusButton.setCallbackData(CALLBACK_PLUS);
        firstRow.add(plusButton);
        // кнопка НЕ иду
        InlineKeyboardButton minusButton = new InlineKeyboardButton();
        minusButton.setText("Не иду");
        minusButton.setCallbackData(CALLBACK_MINUS);
        firstRow.add(minusButton);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        // кнопка иду
        InlineKeyboardButton laterButton = new InlineKeyboardButton();
        laterButton.setText("Отвечу позже");
        laterButton.setCallbackData(CALLBACK_LATER);
        secondRow.add(laterButton);

        buttons.add(firstRow);
        buttons.add(secondRow);

        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);

        message.setReplyMarkup(markupKeyboard);
    }

    private String getCurrentDate() {
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.of("UTC+7"));

        Instant instant = Instant.now();
        return formatter.format(instant);
    }
}
