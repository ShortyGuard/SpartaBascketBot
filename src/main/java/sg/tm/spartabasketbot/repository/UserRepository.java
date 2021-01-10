package sg.tm.spartabasketbot.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.tm.spartabasketbot.model.TelegramUser;

@Repository
public interface UserRepository extends JpaRepository<TelegramUser, Long> {

}