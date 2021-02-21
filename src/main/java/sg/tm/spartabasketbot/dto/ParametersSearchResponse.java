package sg.tm.spartabasketbot.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParametersSearchResponse {

    private List<ParameterDto> parameters;
}
