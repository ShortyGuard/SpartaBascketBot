package sg.tm.spartabasketbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.tm.spartabasketbot.repository.UserRepository;

@Service
public class BotApiService implements IBotApiService {

    @Autowired
    private UserRepository userRepository;
}
