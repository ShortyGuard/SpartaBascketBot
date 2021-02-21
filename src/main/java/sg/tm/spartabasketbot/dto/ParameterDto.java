package sg.tm.spartabasketbot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParameterDto {
    private Long id;
    private String name;
    private String stringValue;
}
