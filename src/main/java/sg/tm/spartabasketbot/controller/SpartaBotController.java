package sg.tm.spartabasketbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import sg.tm.spartabasketbot.dto.BotInfo;
import sg.tm.spartabasketbot.dto.StartCollectionResponse;
import sg.tm.spartabasketbot.service.SpartaBotService;

/**
 * Контроллер обработки запросов пользователей
 */
@RestController
class SpartaBotController {

    @Autowired
    private SpartaBotService spartaBotService;

    @GetMapping("/info")
    public BotInfo botInfo() {
        System.out.println("SpartaBotController: Вызвана команда /info");

        return BotInfo.builder()
            .name("SpartaBasketBot")
            .description("Это бот для облегчения сбора боллеров на трени на Спартаке.")
            .build();
    }

    @GetMapping("/training/collect")
    public StartCollectionResponse startCollection() {

        System.out.println("SpartaBotController: Вызвана команда на старт сбора на тренировку");

        this.spartaBotService.startTrainingCollect();

        return StartCollectionResponse.builder()
            .build();
    }

    @PostMapping("/update")
    public BotApiMethod update(@RequestBody Update update) {
        System.out.println("SpartaBotController: On update method: update = " + update);

        return this.spartaBotService.recievedUpdate(update);
    }

}