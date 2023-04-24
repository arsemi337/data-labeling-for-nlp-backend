package it.nlp.backend.emotionAnalysis.service.impl;

import io.micrometer.common.util.StringUtils;
import it.nlp.backend.emotionAnalysis.dto.TextEmotionInput;
import it.nlp.backend.emotionAnalysis.dto.TextEmotionOutput;
import it.nlp.backend.emotionAnalysis.dto.EmotionDto;
import it.nlp.backend.emotionAnalysis.exception.EmotionAnalysisException;
import it.nlp.backend.emotionAnalysis.service.EmotionAnalysisService;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.buffer.FloatDataBuffer;
import org.tensorflow.types.TString;

import java.util.*;

import static it.nlp.backend.exception.messages.EmotionAnalysisExceptionMessages.*;

@Service
public class EmotionAnalysisServiceImpl implements EmotionAnalysisService {

    @Override
    public TextEmotionOutput classifyTextEmotion(TextEmotionInput textEmotionInput) {
        validateTextInput(textEmotionInput);
        ResourceLoader loader = new DefaultResourceLoader();
        Resource resourceModel = loader.getResource("classpath:/model/");
        try (SavedModelBundle model = SavedModelBundle.load(resourceModel.getFile().getPath(), "serve")) {
            String feedOperation = "serving_default_text_vectorization_input";
            String fetchOperation = "StatefulPartitionedCall_1";
            try (Tensor input = TString.vectorOf(textEmotionInput.text());
                 Tensor output = model.session()
                         .runner()
                         .feed(feedOperation, input)
                         .fetch(fetchOperation)
                         .run()
                         .get(0)
            ) {

                Map<EmotionDto, Float> emotionToProbabilityMap = new HashMap<>();
                extractProbabilitiesFromOutputTensor(output, emotionToProbabilityMap);

                Map.Entry<EmotionDto, Float> maxProbability = Collections.max(emotionToProbabilityMap.entrySet(), Map.Entry.comparingByValue());

                return TextEmotionOutput.builder()
                        .mostProbableEmotion(maxProbability.getKey())
                        .emotionToProbabilityMap(emotionToProbabilityMap)
                        .build();
            } catch (Exception e) {
                throw new EmotionAnalysisException(FAILED_TO_INFER_EMOTION.getMessage(), e);
            }
        } catch (Exception e) {
            throw new EmotionAnalysisException(FAILED_TO_LOAD_NLP_MODEL.getMessage(), e);
        }
    }

    private static void validateTextInput(TextEmotionInput textEmotionInput) {
        if (StringUtils.isBlank(textEmotionInput.text())) {
            throw new IllegalArgumentException(INPUT_TEXT_EMPTY.getMessage());
        }
    }

    private void extractProbabilitiesFromOutputTensor(Tensor output, Map<EmotionDto, Float> emotionToProbabilityMap) {
        FloatDataBuffer floatBuffer = output.asRawTensor().data().asFloats();
        for (int i = 0; i < floatBuffer.size(); i++) {
            String emotion = EmotionDto.values()[i].toString();
            emotionToProbabilityMap.put(EmotionDto.valueOf(emotion), floatBuffer.getFloat(i));
        }
    }
}
