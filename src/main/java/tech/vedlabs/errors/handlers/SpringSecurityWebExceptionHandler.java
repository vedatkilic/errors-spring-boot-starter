package tech.vedlabs.errors.handlers;


import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;
import tech.vedlabs.errors.codes.GenericErrorCode;
import tech.vedlabs.errors.codes.SecurityErrorCode;

import javax.security.auth.login.AccountExpiredException;
import java.nio.file.AccessDeniedException;

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
    public HandledException handle(Throwable exception) {
        if (exception instanceof AccessDeniedException)
            return HandledException.builder().errorCode(SecurityErrorCode.ACCESS_DENIED).exception(exception).build();

        if (exception instanceof AccountExpiredException)
            return HandledException.builder().errorCode(SecurityErrorCode.ACCOUNT_EXPIRED).exception(exception).build();

        if (exception instanceof AuthenticationCredentialsNotFoundException)
            return HandledException.builder().errorCode(SecurityErrorCode.AUTH_REQUIRED).exception(exception).build();

        if (exception instanceof AuthenticationServiceException)
            return HandledException.builder().errorCode(SecurityErrorCode.INTERNAL_ERROR).exception(exception).build();

        if (exception instanceof BadCredentialsException)
            return HandledException.builder().errorCode(SecurityErrorCode.BAD_CREDENTIALS).exception(exception).build();

        if (exception instanceof UsernameNotFoundException)
            return HandledException.builder().errorCode(SecurityErrorCode.BAD_CREDENTIALS).exception(exception).build();

        if (exception instanceof InsufficientAuthenticationException)
            return HandledException.builder().errorCode(SecurityErrorCode.AUTH_REQUIRED).exception(exception).build();

        if (exception instanceof LockedException) return HandledException.builder().errorCode(SecurityErrorCode.USER_LOCKED).exception(exception).build();
        if (exception instanceof DisabledException) return HandledException.builder().errorCode(SecurityErrorCode.USER_DISABLED).exception(exception).build();

        return HandledException.builder().errorCode(GenericErrorCode.UNKNOWN_ERROR).exception(exception).build();
    }
}