package tech.vedlabs.errors.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import tech.vedlabs.errors.ErrorCode;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class DefaultErrorMessageReader implements ErrorMessageReader {

    public static final String PREFIX = "error.%s";
    private final MessageSource messageSource;

    @Override
    public String readMessage(ErrorCode errorCode, Locale locale) {
        try {
            return messageSource.getMessage(String.format(PREFIX, errorCode.getCode()), null, locale);
        } catch (Exception ex) {
            log.debug("Failed to read message", ex);
            return null;
        }
    }
}
