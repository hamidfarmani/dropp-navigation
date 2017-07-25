package security.jwt;

/**
 * Created by kasra on 5/27/2017.
 */

import io.jsonwebtoken.JwtException;
import model.enums.Status;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import security.JwtAuthenticationException;
import security.exceptions.JsonErrorProvider;

import javax.naming.TimeLimitExceededException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JWTAuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws AuthenticationException, IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            Authentication authentication = JwtService.getAuthentication(httpRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (TimeLimitExceededException e) {
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getOutputStream().write(JsonErrorProvider.getJsonError(Status.TOKEN_EXPIRED).getBytes());
        } catch (JwtAuthenticationException | JwtException e) {
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getOutputStream().write(JsonErrorProvider.getJsonError(Status.UNAUTHORIZED).getBytes());
        }
    }
}
