package security;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by kasra on 5/28/2017.
 */
public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String msg, Throwable t) {
        super(msg,t);
    }
}
