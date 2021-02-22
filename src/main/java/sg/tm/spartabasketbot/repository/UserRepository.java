package sg.tm.spartabasketbot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sg.tm.spartabasketbot.model.TelegramUser;

@Repository
public interface UserRepository extends JpaRepository<TelegramUser, Long> {

    @Query(value = "SELECT * FROM telegram_user tu\n" +
        "WHERE tu.id not in (select tp.user_id from training t, training_participant tp \n" +
        "WHERE tp.training_id=t.id\n" +
        "and t.date =':date')", nativeQuery = true)
    List<TelegramUser> findAllNotAnsweredUsers(@Param("date") String date);

    @Query(value = "SELECT * FROM telegram_user tu\n" +
        "WHERE tu.id in (select tp.user_id from training t, training_participant tp \n" +
        "WHERE tp.training_id=t.id\n" +
        "and tp.decision='?'" +
        "and t.date =':date')", nativeQuery = true)
    List<TelegramUser> findAllWaitingUsers(String date);
}