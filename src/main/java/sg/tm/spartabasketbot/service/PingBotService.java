package sg.tm.spartabasketbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PingBotService {

    private final RestTemplate restTemplate;

    @Value("${deploy.service.url}")
    private String deployServiceUrl;

    public PingBotService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    // запускаем пинг через каждые 15 минут, а первый раз через 10 минут после инициализации
    @Scheduled(fixedDelay = 900000, initialDelay = 600000)
    private void ping() {
        String url = deployServiceUrl + "/info";
        System.out.println("PingBot: PING!!! on URL: " + url);
        String response = restTemplate.getForObject(url, String.class);
        System.out.println("Got. Pong response: " + response);
    }

}
