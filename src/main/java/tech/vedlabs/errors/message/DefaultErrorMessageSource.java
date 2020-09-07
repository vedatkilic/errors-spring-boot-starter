package tech.vedlabs.errors.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class DefaultErrorMessageSource implements ErrorMessageSource {

    public static final String PREFIX = "error.%s";
    private final MessageSource messageSource;

    @Override
    public String getMessage(String code, String defaultMessage, Locale locale) {
        try {
            return messageSource.getMessage(String.format(PREFIX, code), null, locale);
        } catch (NoSuchMessageException ex) {
        }
        return defaultMessage;
    }
}
