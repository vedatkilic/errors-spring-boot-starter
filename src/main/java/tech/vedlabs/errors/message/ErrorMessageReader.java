package tech.vedlabs.errors.message;

import tech.vedlabs.errors.ErrorCode;

import java.util.Locale;

public interface ErrorMessageReader {
    String readMessage(ErrorCode code, Locale locale);
}
