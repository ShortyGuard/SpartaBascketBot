package sg.tm.spartabasketbot.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import sg.tm.spartabasketbot.SpartaBasketBot;
import sg.tm.spartabasketbot.model.TelegramUser;
import sg.tm.spartabasketbot.repository.UserRepository;

@Service
public class BotApiService implements IBotApiService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpartaBasketBot spartaBasketBot;

    @Override
    public BotApiMethod recievedUpdate(Update update) {

        // сначала сохраним пользователя
        if (update.hasMessage()) {
            Message message = update.getMessage();
            User from = message.getFrom();

            Optional<TelegramUser> userEntity = this.userRepository.findById(Long.valueOf(from.getId()));
            TelegramUser telegramUser = new TelegramUser();
            telegramUser.setId(Long.valueOf(from.getId()));
            telegramUser.setFirstName(from.getFirstName());
            telegramUser.setLastName(from.getLastName());
            telegramUser.setUserName(from.getUserName());
            telegramUser.setMessagesCount(userEntity.map(user -> user.getMessagesCount() + 1).orElse(1L));

            this.userRepository.save(telegramUser);
        }

        return spartaBasketBot.onWebhookUpdateReceived(update);
    }
}
