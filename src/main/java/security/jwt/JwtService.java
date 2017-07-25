package security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import security.JwtAuthenticationException;
import security.model.AccountCredentials;

import javax.naming.TimeLimitExceededException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kasra on 5/27/2017.
 */
@Service(value = "jwtService")
public class JwtService {
    public static final String SECRET = "IM NOT IN DANGER, I AM THE DANGER -> KasraJ75";
    public static final String TOKEN_PREFIX = "Bearer";
    private static final long EXPIRATION_TIME = 3600000; // 60 min
    private static final String ISSUER = "GENO Co";
    private static final String HEADER_STRING = "Authorization";

    public static String addAuthentication(AccountCredentials accountCredentials) {
        if (accountCredentials.getUsername().equals("") || accountCredentials.getUsername() == null) {
            throw new IllegalArgumentException("Cannot create JWT Token without username");
        }
        if (accountCredentials.getRole().equals("") || accountCredentials.getRole() == null) {
            throw new IllegalArgumentException("Cannot create JWT Token without role");
        }
        Claims claims = Jwts.claims().setSubject(accountCredentials.getUsername());
        claims.put("role", accountCredentials.getRole());
        String JWT = Jwts.builder()
                .setClaims(claims)
                .setIssuer(ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        return TOKEN_PREFIX + " " + JWT;
    }


    static Authentication getAuthentication(HttpServletRequest request) throws JwtAuthenticationException, IOException, TimeLimitExceededException {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
            if (claims.getIssuedAt().getTime() + EXPIRATION_TIME < System.currentTimeMillis()) {
                throw new TimeLimitExceededException("TOKEN EXPIRED");
            }
            String username = claims.getSubject();
            String role = claims.get("role").toString();
            AccountCredentials accountCredentials = null;
            if (username != null && !username.equals("") && role != null && !role.equals("")) {
                accountCredentials = new AccountCredentials(username, role);

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(accountCredentials.getRole()));
                return new UsernamePasswordAuthenticationToken(accountCredentials.getUsername(), null, authorities);
            }
        }
        return null;
    }
}
