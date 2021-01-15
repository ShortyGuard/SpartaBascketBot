package sg.tm.spartabasketbot.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface IBotApiService {
    BotApiMethod recievedUpdate(Update update);

    void startTrainingCollect();
}
