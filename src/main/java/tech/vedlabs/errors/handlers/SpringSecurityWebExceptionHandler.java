package tech.vedlabs.errors.handlers;


import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tech.vedlabs.errors.ErrorCode;
import tech.vedlabs.errors.ErrorMessage;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;
import tech.vedlabs.errors.codes.GenericErrorCode;
import tech.vedlabs.errors.codes.SecurityErrorCode;

import java.util.Locale;

@Order(6)
public class SpringSecurityWebExceptionHandler implements ExceptionHandler {
    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof AccessDeniedException ||
            exception instanceof AccountExpiredException ||
            exception instanceof AuthenticationCredentialsNotFoundException ||
            exception instanceof AuthenticationServiceException ||
            exception instanceof BadCredentialsException ||
            exception instanceof UsernameNotFoundException ||
            exception instanceof InsufficientAuthenticationException ||
            exception instanceof LockedException ||
            exception instanceof DisabledException;
    }

    @Override
    public HandledException handle(Throwable exception, Locale locale) {
        ErrorCode errorCode = GenericErrorCode.UNKNOWN_ERROR;
        if (exception instanceof AccessDeniedException)
            errorCode = SecurityErrorCode.ACCESS_DENIED;
        if (exception instanceof AccountExpiredException)
            errorCode = SecurityErrorCode.ACCOUNT_EXPIRED;

        if (exception instanceof AuthenticationCredentialsNotFoundException)
            errorCode = SecurityErrorCode.AUTH_REQUIRED;

        if (exception instanceof AuthenticationServiceException)
            errorCode = SecurityErrorCode.INTERNAL_ERROR;

        if (exception instanceof BadCredentialsException)
            errorCode = SecurityErrorCode.BAD_CREDENTIALS;

        if (exception instanceof UsernameNotFoundException)
            errorCode = SecurityErrorCode.BAD_CREDENTIALS;

        if (exception instanceof InsufficientAuthenticationException)
            errorCode = SecurityErrorCode.AUTH_REQUIRED;

        if (exception instanceof LockedException) errorCode = SecurityErrorCode.USER_LOCKED;
        if (exception instanceof DisabledException) errorCode = SecurityErrorCode.USER_DISABLED;

        return new HandledException(errorCode.getHttpStatus(), new ErrorMessage(errorCode.getCode(), null, exception.getMessage()));
    }
}