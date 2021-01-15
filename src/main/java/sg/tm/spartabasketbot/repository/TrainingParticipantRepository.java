package sg.tm.spartabasketbot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.tm.spartabasketbot.model.Training;
import sg.tm.spartabasketbot.model.TrainingParticipant;

@Repository
public interface TrainingParticipantRepository extends JpaRepository<TrainingParticipant, Long> {

    TrainingParticipant findOneByuserIdAndTrainingId(Long userId, Long trainingId);

    List<TrainingParticipant> findAllByTrainingId(Long trainingId);
}