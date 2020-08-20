package tech.vedlabs.errors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionControllerAdvice {

    /**
     * Responsible for handling exceptions and converting them to appropriate {@link java.lang.Error}s.
     */
    private final ExceptionHandlerRegistry exceptionHandlerRegistry;

    private final WebResponseExceptionTranslator webResponseExceptionTranslator;

    /**
     * Catches any exception and converts it to a HTTP response with appropriate status
     * code and error code-message combinations.
     *
     * @param exception  The caught exception.
     * @param request The current HTTP request.
     * @param locale     Determines the locale for message translation.
     * @return A HTTP response with appropriate error body and status code.
     */
    @ResponseBody
    @ExceptionHandler
    public ResponseEntity<?> handleException(Exception exception, HttpServletRequest request, Locale locale) throws Exception {
        ApiError apiError = exceptionHandlerRegistry.handle(exception, request, locale);
        return webResponseExceptionTranslator.translate(apiError);
    }
}
