package it.nlp.backend.exception.messages;

public enum ChannelExceptionMessages {
    CONTAINS_NULL_CHANNEL_ID("Entered list contains null ID"),
    NULL_CHANNEL_ID("Entered channel ID is null"),
    NO_CHANNEL_WITH_ENTERED_ID("There is no channel with entered ID: ");

    private final String message;

    ChannelExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
