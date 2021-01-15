package sg.tm.spartabasketbot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "training_participant")
public class TrainingParticipant {

    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String LATER = "?";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trainingParticipantSequence")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "training_id")
    private Long trainingId;

    @Column(name = "decision")
    private String decision;
}
