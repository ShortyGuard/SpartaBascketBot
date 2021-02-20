package sg.tm.spartabasketbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpartaBasketBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpartaBasketBotApplication.class, args);
    }

}
