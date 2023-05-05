package it.nlp.backend.textAnalysis.service.impl;

import com.google.protobuf.TextFormat;
import it.nlp.backend.textAnalysis.protos.ModelConfig;
import it.nlp.backend.textAnalysis.protos.ModelConfigList;
import it.nlp.backend.textAnalysis.protos.ModelServerConfig;
import it.nlp.backend.textAnalysis.service.TfServingConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.List;

import static it.nlp.backend.exception.messages.ModelExceptionMessages.*;

@Service
public class TfServingConfigServiceImpl implements TfServingConfigService {

    private static final String CONFIG_FILE_NAME = "models.config";
    private static final String TENSORFLOW_MODEL_PLATFORM = "tensorflow";

    @Value("${general.tf-serving-models-base-path}")
    private String tfServingModelsBasePath;

    @Override
    public void addModelToConfig(String modelName, String modelDirPath) {
        File modelConfigFile = new File(modelDirPath, CONFIG_FILE_NAME);

        ModelServerConfig currentModelServerConfig = getConfigFromFile(modelConfigFile);

        List<ModelConfig> currentConfigList = currentModelServerConfig.getModelConfigList().getConfigList();

        ModelServerConfig newModelServerConfig = ModelServerConfig.newBuilder()
                .setModelConfigList(ModelConfigList.newBuilder()
                        .addAllConfig(currentConfigList)
                        .addConfig(ModelConfig.newBuilder()
                                .setName(modelName)
                                .setBasePath(tfServingModelsBasePath + "/" + modelName)
                                .setModelPlatform(TENSORFLOW_MODEL_PLATFORM)
                                .build())
                        .build())
                .build();

        saveConfigToFile(newModelServerConfig, modelConfigFile);
    }

    @Override
    public void removeModelFromConfig(String modelName, String modelDirPath) {
        File modelConfigFile = new File(modelDirPath, CONFIG_FILE_NAME);
        ModelServerConfig currentModelServerConfig = getConfigFromFile(modelConfigFile);

        List<ModelConfig> currentConfigList = currentModelServerConfig.getModelConfigList().getConfigList();
        List<ModelConfig> newConfigList = currentConfigList.stream()
                .filter(modelConfig -> !modelConfig.getName().equals(modelName))
                .toList();

        ModelServerConfig newModelServerConfig = ModelServerConfig.newBuilder()
                .setModelConfigList(ModelConfigList.newBuilder()
                        .addAllConfig(newConfigList)
                        .build())
                .build();

        saveConfigToFile(newModelServerConfig, modelConfigFile);
    }

    private ModelServerConfig getConfigFromFile(File file) {
        try {
            String text = Files.readString(file.toPath());
            ModelServerConfig.Builder builder = ModelServerConfig.newBuilder();
            TextFormat.getParser().merge(text, builder);
            return builder.build();
        } catch (NoSuchFileException e) {
            return ModelServerConfig.newBuilder()
                    .setModelConfigList(ModelConfigList.newBuilder().build())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(CANNOT_READ_CONFIG_FILE.getMessage());
        }
    }

    private void saveConfigToFile(ModelServerConfig config, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(config.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(CANNOT_SAVE_CONFIG_FILE.getMessage());
        }
    }
}
