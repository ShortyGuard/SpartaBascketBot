package sg.tm.spartabasketbot.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sg.tm.spartabasketbot.service.ParametersService;
import sg.tm.spartabasketbot.service.SpartaBotService;

@Service
public class SchedulerService {

    private final RestTemplate restTemplate;

    @Autowired
    private SpartaBotService spartaBotService;

    @Autowired
    private ParametersService parametersService;

    @Value("${bot.group.id}")
    private String botGroupId;

    @Value("${deploy.service.url}")
    private String deployServiceUrl;

    public SchedulerService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    // запускаем пинг через каждые 15 минут, а первый раз через 10 минут после инициализации
    @Scheduled(fixedDelay = 900000, initialDelay = 600000)
    private void ping() {
        String url = deployServiceUrl + "/info";
        System.out.println("SchedulerService: PING!!! on URL: " + url);
        String response = restTemplate.getForObject(url, String.class);
        System.out.println("SchedulerService: Got. Pong response: " + response);
    }

    // метод отправки напоминания о сдаче денег (cron формат second, minute, hour, day of month, month, day(s) of week)
    @Scheduled(cron = "0 0 12,20 21-31 * *", zone = "Asia/Novosibirsk")
    private void sendMonthlyPaymentReminderMessage() {
        System.out.println("SchedulerService: sendMonthlyPaymentReminderMessage. ");

        SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
        message.setChatId(botGroupId);
        message.enableHtml(true);
        message.setText(parametersService.getStringValue(1l));

        spartaBotService.sendMessage(message);
    }

    // метод запускающий каждый понедельник и среду собранние на тренировку в 00:01:00.
    // (cron формат second, minute, hour, day of month, month, day(s) of week)
    @Scheduled(cron = "0 1 0 * * MON,WED", zone = "Asia/Novosibirsk")
    private void startTrainingCollect() {
        System.out.println("SchedulerService: startTrainingCollect. ");

        spartaBotService.startTrainingCollect();
    }

}
