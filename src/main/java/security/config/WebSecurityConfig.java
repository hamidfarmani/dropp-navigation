package security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import security.jwt.JWTAuthenticationFilter;

/**
 * Created by kasra on 5/27/2017.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().sameOrigin();
        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/api/rest/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/rest/token/refresh").permitAll()
                .antMatchers(HttpMethod.POST, "/api/rest/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/rest/*/register").permitAll()
                .antMatchers(HttpMethod.POST, "/api/rest/*/activate").permitAll()
                .antMatchers(HttpMethod.POST, "/api/rest/user/device/token/fcm").permitAll()
                .antMatchers(HttpMethod.POST, "/api/rest/user").permitAll()
                .antMatchers(HttpMethod.GET, "/api/rest/user").permitAll()
                .antMatchers("/api/rest/driver/**").hasAuthority("D")
                .antMatchers("/api/rest/passenger/**").hasAuthority("P")
                .antMatchers("/api/rest/operator/**").hasAnyAuthority("O", "M", "A")
                .antMatchers("/api/rest/master/**").hasAnyAuthority("M", "A")
                .antMatchers("/api/rest/cop/**").hasAnyAuthority("C","A")
                .antMatchers("/api/rest/admin/**").hasAuthority("A")
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().accessDeniedHandler(new security.exceptions.AccessDeniedHandlerImpl());
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

}
