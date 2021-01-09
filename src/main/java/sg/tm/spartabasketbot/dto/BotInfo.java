package sg.tm.spartabasketbot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BotInfo {

    private String name;

    private String description;
}
