package sg.tm.spartabasketbot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "telegram_user")
public class TelegramUser {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_name")
    @NotBlank
    private String userName;

    @Column(name = "personal_chat_id")
    private String personalChatId;

    @Column(name = "messages_count")
    private Long messagesCount;

    public String getUserNameForMention(){
        if (userName != null && userName.trim().length() > 0){
            return "@" + userName;
        }
        StringBuffer builder   = new StringBuffer();
        builder.append(firstName);
        if (lastName != null){
            builder.append(".");
            builder.append(lastName);
        }
        return builder.toString();
    }

    public String getShorName() {
        StringBuffer builder   = new StringBuffer();
        builder.append(firstName);
        if (lastName != null){
            builder.append(".");
            builder.append(lastName);
        }
        return builder.toString();
    }
}
