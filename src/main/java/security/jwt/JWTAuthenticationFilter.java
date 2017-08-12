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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws AuthenticationException, IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            Authentication authentication = JwtService.getAuthentication(httpRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (TimeLimitExceededException e) {
            httpResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            httpResponse.getOutputStream().write(JsonErrorProvider.getJsonError(Status.TOKEN_EXPIRED).getBytes());
            httpResponse.addHeader("access-control-allow-origin","*");// TODO: 8/12/2017  وقتی میخوای بذاری رو سرور اصلی، پاگش کن
            response=httpResponse;
        } catch (JwtAuthenticationException | JwtException e) {
            httpResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            httpResponse.getOutputStream().write(JsonErrorProvider.getJsonError(Status.UNAUTHORIZED).getBytes());
            httpResponse.addHeader("access-control-allow-origin","*"); // TODO: 8/12/2017  وقتی میخوای بذاری رو سرور اصلی، پاگش کن
            response=httpResponse;
        }
    }
}