package it.nlp.backend.textAnalysis.service.impl;

import it.nlp.backend.textAnalysis.dto.PredictionInput;
import it.nlp.backend.textAnalysis.dto.TextInput;
import it.nlp.backend.textAnalysis.dto.TextPredictionOutput;
import it.nlp.backend.textAnalysis.dto.TfPredictionOutput;
import it.nlp.backend.textAnalysis.exception.PredictionErrorResponse;
import it.nlp.backend.textAnalysis.service.TextAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static it.nlp.backend.exception.messages.TextAnalysisExceptionMessages.INPUT_TEXT_EMPTY;
import static it.nlp.backend.exception.messages.TextAnalysisExceptionMessages.UNEXPECTED_ERROR;

@Service
@Slf4j
public class TextAnalysisServiceImpl implements TextAnalysisService {
    private final WebClient client;

    @Value("${general.tf-serving-host}")
    private String tfServingHost;
    @Value("${general.tf-serving-port}")
    private String tfServingPort;

    public TextAnalysisServiceImpl(WebClient client) {
        this.client = client;
    }

    @Override
    public TextPredictionOutput getTextPredictions(TextInput textInput) {
        validateTextInput(textInput);

        String uri = String.format("http://%s:%s/v1/models/%s:predict",
                tfServingHost, tfServingPort, textInput.modelName());

        PredictionInput requestBody = PredictionInput.builder()
                .instances(List.of(textInput.text()))
                .build();

        TfPredictionOutput tfPredictionOutput = client.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.value() != 200,
                        clientResponse -> clientResponse.bodyToMono(PredictionErrorResponse.class)
                                .flatMap(e -> Mono.error(new IllegalStateException(e.error()))))
                .bodyToMono(TfPredictionOutput.class)
                .block();

        if (tfPredictionOutput == null ||
                tfPredictionOutput.predictions() == null ||
                tfPredictionOutput.predictions().isEmpty()) {
            throw new IllegalStateException(UNEXPECTED_ERROR + textInput.modelName());
        } else {
            return TextPredictionOutput.builder()
                    .predictions(tfPredictionOutput.predictions().get(0))
                    .build();
        }
    }

    private void validateTextInput(TextInput textInput) {
        if (StringUtils.isAnyBlank(textInput.modelName(), textInput.text())) {
            throw new IllegalArgumentException(INPUT_TEXT_EMPTY.getMessage());
        }
    }
}
