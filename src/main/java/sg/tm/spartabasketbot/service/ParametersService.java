package sg.tm.spartabasketbot.service;

import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.tm.spartabasketbot.dto.ParameterDto;
import sg.tm.spartabasketbot.model.Parameter;
import sg.tm.spartabasketbot.repository.ParameterRepository;

@Service
public class ParametersService {

    @Autowired
    private ParameterRepository parameterRepository;

    public List<Parameter> getAllParameters() {
        return parameterRepository.findAll();
    }

    public Parameter updateParameter(ParameterDto update) {
        Parameter parameter = this.parameterRepository.findById(update.getId())
            .orElseThrow(() -> new EntityNotFoundException("Parameter with id=" + update.getId() + " not found"));

        parameter.setName(update.getName());
        parameter.setStringValue(update.getStringValue());

        this.parameterRepository.save(parameter);

        return parameter;
    }

    public String getStringValue(Long parameterId) {
        Parameter parameter = this.parameterRepository.findById(parameterId)
            .orElseThrow(() -> new EntityNotFoundException("Parameter with id=" + parameterId + " not found"));

        return parameter.getStringValue();
    }
}
