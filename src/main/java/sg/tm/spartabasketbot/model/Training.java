package sg.tm.spartabasketbot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "training")
public class Training {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    @NotBlank
    private String date;
}
