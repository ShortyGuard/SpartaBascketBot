package sg.tm.spartabasketbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import sg.tm.spartabasketbot.service.PingBotService;

@RestController
public class PingBotController {

    @Autowired
    private PingBotService pingBotService;

    @PostMapping("/ping/update")
    public BotApiMethod update(@RequestBody Update update) {
        System.out.println("On update method: update = " + update);

        return this.pingBotService.onWebhookUpdateReceived(update);
    }
}
