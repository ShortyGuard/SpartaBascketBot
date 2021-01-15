package sg.tm.spartabasketbot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sg.tm.spartabasketbot.model.TelegramUser;
import sg.tm.spartabasketbot.model.Training;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    Training findOneByDate(String date);
}