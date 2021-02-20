package sg.tm.spartabasketbot.service.command;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
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
public class StatCommandHandler implements BotCommandHandler {
    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainingParticipantRepository trainingParticipantRepository;

    @Override
    public boolean handleCommand(AbsSender sender, Message updateMessage) throws TelegramApiException {
            String statInfo = getStatInfo();
            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(String.valueOf(updateMessage.getChatId()));
            message.enableHtml(true);
            message.setText("Текущее состояние по сборам на тренировку:\n" + statInfo);

            sender.execute(message);

            return true;
    }

    public String getStatInfo() {

        String currentDate = DateUtil.getCurrentDate();

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

            String answer = "<b>Идут на тренировку</b>\n<pre>";
            for (int i = 0; i < plusUsers.size(); i++) {
                TelegramUser user = plusUsers.get(i);
                answer += " " + (i + 1) + ". " + user.getShorName() + "\n";
            }
            answer += "</pre><b>НЕ идут на тренировку</b>\n<pre>";
            for (int i = 0; i < minusUsers.size(); i++) {
                TelegramUser user = minusUsers.get(i);
                answer += " " + (i + 1) + ". " + user.getShorName() + "\n";
            }
            answer += "</pre><b>Ответят позже</b>\n<pre>";
            for (int i = 0; i < laterUsers.size(); i++) {
                TelegramUser user = laterUsers.get(i);
                answer += " " + (i + 1) + ". " + user.getShorName() + "\n";
            }
            answer += "</pre><b><u>НЕ ПРОГОЛОСОВАЛИ (ай-яй-яй)</u></b>\n<pre>";
            for (int i = 0; i < allUsers.size(); i++) {
                TelegramUser user = allUsers.get(i);
                answer += " " + (i + 1) + ". " + user.getUserNameForMention() + "\n";
            }
            answer += "</pre>";

            return answer;

        }
        return "Сбор на тренировку еще не начался.\n" +
            "Ждем когда кто-то из админов соизволит мне об этом сказать.";
    }

}
