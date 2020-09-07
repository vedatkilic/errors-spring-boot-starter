package tech.vedlabs.errors.message;

import java.util.Locale;

public interface ErrorMessageSource {
    String getMessage(String errorCode, String defaultMessage, Locale locale);
}
