package sg.tm.spartabasketbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import sg.tm.spartabasketbot.SpartaBasketBot;
import sg.tm.spartabasketbot.repository.UserRepository;

@Service
public class BotApiService implements IBotApiService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpartaBasketBot spartaBasketBot;

    @Override
    public BotApiMethod recievedUpdate(Update update) {
        return spartaBasketBot.onWebhookUpdateReceived(update);
    }
}
