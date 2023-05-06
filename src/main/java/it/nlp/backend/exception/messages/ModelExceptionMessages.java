package it.nlp.backend.exception.messages;

public enum ModelExceptionMessages {
    MODEL_ZIP_NAME_IS_NULL("Model zip name is null or not defined"),
    MODEL_ZIP_IS_NOT_ZIP("Entered model file is not an archived zip file"),
    FILE_CANNOT_BE_UNZIPPED("An issue occurred while trying to unzip the file"),
    PATH_IS_NOT_DIRECTORY("Defined model destination path is not a directory: "),
    UNEXPECTED_ERROR_EXTRACTING("An unexpected IO error occurred while extracting zip fil"),
    UNEXPECTED_ERROR_MOVING_FILE("An unexpected IO error occurred while moving file"),
    UNEXPECTED_ERROR_CREATING_TEMP("An unexpected IO error occurred while creating temp file"),
    UNEXPECTED_ERROR_READING_MULTIPART("An unexpected IO error occurred while reading multipart file"),
    CANNOT_READ_CONFIG_FILE("Cannot read config file"),
    CANNOT_SAVE_CONFIG_FILE("Cannot save config file"),
    MODEL_NAME_IS_NULL("Model name cannot be null"),
    MODEL_ALREADY_EXISTS("Model with entered name already exists: "),
    MODEL_DOES_NOT_EXIST("Model with entered name does not exist: "),
    DIRECTORY_REMOVAL_FAILED("Cannot remove directory with name: ");

    private final String message;

    ModelExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

