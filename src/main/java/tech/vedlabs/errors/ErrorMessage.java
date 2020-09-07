package tech.vedlabs.errors;

import lombok.Data;

import java.util.List;

@Data
public class ErrorMessage {
    private final String errorCode;
    private final List<Argument> arguments;
    private final String defaultMessage;

    public ErrorMessage(String errorCode, List<Argument> arguments) {
        this(errorCode, arguments, null);
    }

    public ErrorMessage(String errorCode, String defaultMessage) {
        this(errorCode, null, defaultMessage);
    }

    public ErrorMessage(String errorCode, List<Argument> arguments, String defaultMessage) {
        this.errorCode = errorCode;
        this.arguments = arguments;
        this.defaultMessage = defaultMessage;
    }
}
