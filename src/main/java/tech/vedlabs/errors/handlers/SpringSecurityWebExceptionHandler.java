package tech.vedlabs.errors.handlers;


import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;
import tech.vedlabs.errors.codes.CommonErrorCode;
import tech.vedlabs.errors.codes.SpringSecurityErrorCode;

import javax.security.auth.login.AccountExpiredException;
import java.nio.file.AccessDeniedException;

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
            return HandledException.builder().errorCode(SpringSecurityErrorCode.ACCESS_DENIED).exception(exception).build();

        if (exception instanceof AccountExpiredException)
            return HandledException.builder().errorCode(SpringSecurityErrorCode.ACCOUNT_EXPIRED).exception(exception).build();

        if (exception instanceof AuthenticationCredentialsNotFoundException)
            return HandledException.builder().errorCode(SpringSecurityErrorCode.AUTH_REQUIRED).exception(exception).build();

        if (exception instanceof AuthenticationServiceException)
            return HandledException.builder().errorCode(SpringSecurityErrorCode.INTERNAL_ERROR).exception(exception).build();

        if (exception instanceof BadCredentialsException)
            return HandledException.builder().errorCode(SpringSecurityErrorCode.BAD_CREDENTIALS).exception(exception).build();

        if (exception instanceof UsernameNotFoundException)
            return HandledException.builder().errorCode(SpringSecurityErrorCode.BAD_CREDENTIALS).exception(exception).build();

        if (exception instanceof InsufficientAuthenticationException)
            return HandledException.builder().errorCode(SpringSecurityErrorCode.AUTH_REQUIRED).exception(exception).build();

        if (exception instanceof LockedException) return HandledException.builder().errorCode(SpringSecurityErrorCode.USER_LOCKED).exception(exception).build();
        if (exception instanceof DisabledException) return HandledException.builder().errorCode(SpringSecurityErrorCode.USER_DISABLED).exception(exception).build();

        return HandledException.builder().errorCode(CommonErrorCode.UNKNOWN_ERROR).exception(exception).build();
    }
}