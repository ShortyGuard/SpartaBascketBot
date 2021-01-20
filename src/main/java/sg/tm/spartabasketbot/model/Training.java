package sg.tm.spartabasketbot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "training")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trainingSequence")
    @SequenceGenerator(name = "trainingSequence", sequenceName = "training_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    @NotBlank
    private String date;
}
