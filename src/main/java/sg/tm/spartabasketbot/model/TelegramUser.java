package sg.tm.spartabasketbot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.checkerframework.checker.signature.qual.Identifier;

@Data
@Entity
@Table(name = "telegram_user")
public class TelegramUser {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;
}
