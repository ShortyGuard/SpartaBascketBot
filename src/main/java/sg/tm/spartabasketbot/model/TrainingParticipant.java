package sg.tm.spartabasketbot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "training_participant")
public class TrainingParticipant {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "training_id")
    private Long trainingId;
}
