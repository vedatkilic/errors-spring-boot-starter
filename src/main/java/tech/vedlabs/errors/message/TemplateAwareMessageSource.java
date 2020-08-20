package tech.vedlabs.errors.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.StringUtils;
import tech.vedlabs.errors.HandledException;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class TemplateAwareMessageSource {

    private final ErrorMessageReader errorMessageReader;
    private final TemplateParser templateParser = new TemplateParser();

    public String interpolate(HandledException handled, Locale locale) {
        try {
            String template = errorMessageReader.readMessage(handled.getErrorCode(), locale);
            template = !StringUtils.isEmpty(template) ? template : handled.getErrorCode().getMessage();
            template = !StringUtils.isEmpty(template) ? template : resolveExceptionMessage(handled.getException());
            return templateParser.parse(template, handled.getArguments());
        } catch (NoSuchMessageException e) {
            return null;
        } catch (Exception e) {
            log.debug("Failed to interpolate a message", e);
            return null;
        }
    }

    private String resolveExceptionMessage(Throwable exception) {
        if(exception != null)
            return exception.getMessage();
        return null;
    }
}
