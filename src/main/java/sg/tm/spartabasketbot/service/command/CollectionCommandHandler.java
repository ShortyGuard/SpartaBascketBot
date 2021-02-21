package sg.tm.spartabasketbot.service.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import sg.tm.spartabasketbot.model.TelegramUser;
import sg.tm.spartabasketbot.model.Training;
import sg.tm.spartabasketbot.model.TrainingParticipant;
import sg.tm.spartabasketbot.repository.TrainingParticipantRepository;
import sg.tm.spartabasketbot.repository.TrainingRepository;
import sg.tm.spartabasketbot.repository.UserRepository;
import sg.tm.spartabasketbot.service.DateUtil;

@Component
public class CollectionCommandHandler implements BotCommandHandler {

    private static final String CALLBACK_PLUS = "20a6615f-e44a-42c4-9cab-9326eef38b9c";
    private static final String CALLBACK_MINUS = "a9c677fa-1083-4615-a1e0-0ef3932af210";
    private static final String CALLBACK_LATER = "b2e7e540-051c-4ed1-b8d4-52834ccf8dee";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TrainingParticipantRepository trainingParticipantRepository;

    @Autowired
    private StatCommandHandler statCommandHandler;

    @Value("${bot.group.id}")
    private String botGroupId;

    @Override
    public boolean handleCommand(AbsSender sender, Message updateMessage) throws TelegramApiException {
        return false;
    }

    public void startTrainingCollect(AbsSender sender) throws TelegramApiException {
        String currentDate = DateUtil.getCurrentDate();

        //сначала проверить, что текущего открытого сбора нет
        Training training = this.trainingRepository.findOneByDate(currentDate);

        if (training == null) {
            //создать сбор на сегодня
            training = new Training();
            training.setDate(currentDate);
            this.trainingRepository.save(training);

            //оповестить всех пользователей кто еще не ответил на сбор
            notifyAllNotAnsweredUsers(sender, currentDate);

            //оповестить в общий чат, что сбор начался
        }

    }

    public void notifyAllNotAnsweredUsers(AbsSender sender, String date) throws TelegramApiException {
        //получим неответивших пользователей
        List<TelegramUser> users = this.userRepository.findAllNotAnsweredUsers(date);

        StringBuffer stringBuffer = new StringBuffer("Сегодня тренировка. Ждем ответа об участии от ");
        boolean needNotifyAll = false;
        for (TelegramUser user : users) {
            needNotifyAll = true;
            System.out.println("не ответил: " + user.getId());

            notifyNotAnsweredUser(sender, String.valueOf(user.getId()), "Сегодня тренировка. Ждем ответа об участии. " +
                "\nОтветь боту (см. /help) лично или нажмите на одну из кнопок ниже.");

            stringBuffer.append(user.getUserNameForMention());
            stringBuffer.append(", ");
        }
        stringBuffer.append("\n Ответье боту (см. /help) лично или нажмите на одну из кнопок ниже.");

        if (needNotifyAll) {
            notifyNotAnsweredUser(sender, botGroupId, stringBuffer.toString());
        }
    }

    private void notifyNotAnsweredUser(AbsSender sender, String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
        message.setChatId(chatId);
        message.setText(text);

        //  setButtons(message); // это кнопки в ответе
        setInlineForCollection(message); // это кнопки в сообщении

        sender.execute(message);
    }

    public void notifyAllWaitingUsers(AbsSender sender, String date) throws TelegramApiException {
        //получим ожидающих пользователей
        List<TelegramUser> users = this.userRepository.findAllWaitingUsers(date);

        StringBuffer stringBuffer = new StringBuffer("Пора бы уже определиться ");
        boolean needNotifyAll = false;
        for (TelegramUser user : users) {
            needNotifyAll = true;

            stringBuffer.append(user.getUserNameForMention());
            stringBuffer.append(", ");
        }

        if (needNotifyAll) {
            SendMessage message = new SendMessage();
            message.setChatId(botGroupId);
            message.setText(stringBuffer.toString());

            sender.execute(message);
        }
    }

    public BotApiMethod handleCallbackQuery(AbsSender sender, CallbackQuery callbackQuery) throws TelegramApiException {
        Optional<TelegramUser> telegramUser = this.userRepository.findById(
            Long.valueOf(callbackQuery.getFrom().getId()));
        if (telegramUser.isPresent()) {
            if (CALLBACK_PLUS.equals(callbackQuery.getData())) {
                saveUserCollectionDecision(sender, telegramUser.get(), TrainingParticipant.PLUS);
            } else if (CALLBACK_MINUS.equals(callbackQuery.getData())) {
                saveUserCollectionDecision(sender, telegramUser.get(), TrainingParticipant.MINUS);
            } else if (CALLBACK_LATER.equals(callbackQuery.getData())) {
                saveUserCollectionDecision(sender, telegramUser.get(), TrainingParticipant.LATER);
            }
        } else {
            System.out.println("Coudn't find user by id in CallbackQuery: " + callbackQuery);
        }

        return null;
    }

    public void saveUserCollectionDecision(AbsSender sender, TelegramUser telegramUser, String decision) throws TelegramApiException {
        String currentDate = DateUtil.getCurrentDate();

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

                sender.execute(message);
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

            sender.execute(message);

            message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(botGroupId);
            message.enableHtml(true);
            String everyBodyNotificationText = "<b>" + telegramUser.getShorName() +
                " ответил " +
                (TrainingParticipant.PLUS.equals(decision) ? "'Я иду'" : (
                    TrainingParticipant.MINUS.equals(decision) ? "'Я НЕ иду'" :
                        "'Отвечу позже'")) +
                "</b>";

            everyBodyNotificationText += "\nТекущее состояние по сборам на тренировку:\n" +
                statCommandHandler.getStatInfo();
            message.setText(everyBodyNotificationText);

            setInlineForCollection(message);

            sender.execute(message);

        } else {
            System.out.println("GET VOTE!!!  telegramUser = " + telegramUser + ", decision = " + decision);
            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(String.valueOf(telegramUser.getId()));

            message.setText("Сбор на тренировку еще не начался.\n" +
                "Ждем когда кто-то из админов соизволит мне об этом сказать.");

            sender.execute(message);
        }
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
        firstRow.add(laterButton);

        buttons.add(firstRow);
        // buttons.add(secondRow);

        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);

        message.setReplyMarkup(markupKeyboard);
    }


}
