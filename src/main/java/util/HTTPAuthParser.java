package util;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Created by kasra on 3/14/2017.
 */
public class HTTPAuthParser {

    public String returnUsername(String auth) {
        if (auth != null && auth.startsWith("Basic")) {
            String base64Credentials = auth.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
            String[] userPass = credentials.split(":");
            return userPass[0];
        }
        return null;
    }

    public String returnPassword(String auth) {
        if (auth != null && auth.startsWith("Basic")) {
            String base64Credentials = auth.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
            String[] userPass = credentials.split(":");
            return userPass[1];
        }
        return null;
    }
}
