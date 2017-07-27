package util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import security.jwt.JwtService;

/**
 * Created by kasra on 3/14/2017.
 */
public class HTTPAuthParser {
    public String returnUsername(String auth) {
        if (auth != null && !auth.equals("")) {
            Claims claims = Jwts.parser()
                    .setSigningKey(JwtService.SECRET)
                    .parseClaimsJws(auth.replace(JwtService.TOKEN_PREFIX, ""))
                    .getBody();
            return claims.getSubject();
        }
        return null;
    }
}
