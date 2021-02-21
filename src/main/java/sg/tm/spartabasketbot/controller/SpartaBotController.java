package sg.tm.spartabasketbot.controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import sg.tm.spartabasketbot.dto.BotInfo;
import sg.tm.spartabasketbot.dto.ParameterDto;
import sg.tm.spartabasketbot.dto.ParametersSearchResponse;
import sg.tm.spartabasketbot.dto.StartCollectionResponse;
import sg.tm.spartabasketbot.model.Parameter;
import sg.tm.spartabasketbot.service.ParametersService;
import sg.tm.spartabasketbot.service.SpartaBotService;

/**
 * Контроллер обработки запросов пользователей
 */
@RestController
class SpartaBotController {

    @Autowired
    private SpartaBotService spartaBotService;

    @Autowired
    private ParametersService parametersService;

    @GetMapping("/info")
    public BotInfo botInfo() {
        System.out.println("SpartaBotController: Вызвана команда /info");

        return BotInfo.builder()
            .name("SpartaBasketBot")
            .description("Это бот для облегчения сбора боллеров на трени на Спартаке.")
            .build();
    }

    @PutMapping("/training/parameters")
    public ParameterDto updateParameter(
        @RequestBody
        @Valid ParameterDto parameterDto) {

        System.out.println("SpartaBotController: Вызвана команда обновления параметра");

        return toParameterDto(this.parametersService.updateParameter(parameterDto));
    }

    @GetMapping("/training/parameters")
    public ParametersSearchResponse searchParameters() {

        System.out.println("SpartaBotController: Вызвана команда получения всех конфигурируемых параметров");

        return toParametersSearchResponse(this.parametersService.getAllParameters());
    }

    private ParametersSearchResponse toParametersSearchResponse(List<Parameter> allParameters) {
        return ParametersSearchResponse.builder()
            .parameters(allParameters.stream().map(it -> toParameterDto(it)).collect(Collectors.toList()))
            .build();
    }

    private ParameterDto toParameterDto(Parameter parameter) {
        return ParameterDto.builder()
            .id(parameter.getId())
            .name(parameter.getName())
            .stringValue(parameter.getStringValue())
            .build();
    }


    @GetMapping("/training/collect")
    public StartCollectionResponse startCollection() {

        System.out.println("SpartaBotController: Вызвана команда на старт сбора на тренировку");

        this.spartaBotService.startTrainingCollect();

        return StartCollectionResponse.builder()
            .build();
    }

    /**
     * Это основной метод, на которыей по webHook приходят сообщения из телеги
     */
    @PostMapping("/update")
    public BotApiMethod update(@RequestBody Update update) {
        System.out.println("SpartaBotController: On update method: update = " + update);

        return this.spartaBotService.recievedUpdate(update);
    }

}