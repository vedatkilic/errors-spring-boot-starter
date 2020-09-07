package tech.vedlabs.errors.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.vedlabs.errors.ErrorMessage;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class TemplateAwareMessageSource {

    private final ErrorMessageSource errorMessageSource;
    private final TemplateParser templateParser = new TemplateParser();

    public String interpolate(ErrorMessage errorMessage, Locale locale) {
        try {
            String template = errorMessageSource.getMessage(errorMessage.getErrorCode(),errorMessage.getDefaultMessage(), locale);
            return templateParser.parse(template, errorMessage.getArguments());
        } catch (Exception e) {
            log.warn("Failed to interpolate a message", e);
            return null;
        }
    }
}
