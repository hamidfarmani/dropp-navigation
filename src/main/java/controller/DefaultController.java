package controller;

import model.enums.Status;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import security.jwt.JwtService;
import security.model.AccountCredentials;
import service.OperatorService;
import service.SystemServiceImpl;
import util.IOCContainer;
import util.ResponseProvider;

import javax.servlet.http.HttpServletRequest;


@RestController
@CrossOrigin
public class DefaultController {
    private OperatorService operatorService;

    @Autowired
    public DefaultController(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity handleCross() {
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/token/refresh", method = RequestMethod.POST)
    public ResponseEntity<String> refreshToken(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String oldToken = jsonObjectRequest.getString("token").replace(JwtService.TOKEN_PREFIX, "");
        Object object = ((SystemServiceImpl) IOCContainer.getBean("systemService")).refreshToken(oldToken);
        if (object instanceof Status) {
            return returnResponse((Status) object);
        }
        if (object instanceof String) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", (String) object);
            headers.add("Access-Control-Expose-Headers", "Authorization");
            return returnResponse(Status.OK, headers);
        }
        throw new java.lang.IllegalStateException("Unknown" + object);
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> login(@RequestBody String request,HttpServletRequest httpRequest) {
        JSONObject jsonObjectRequest = new JSONObject(request);

        try {
            String username = jsonObjectRequest.getString("username");
            String password = jsonObjectRequest.getString("password");
            Object object = operatorService.operatorLogin(username,password, httpRequest.getRemoteAddr());
            if (object instanceof AccountCredentials) {
                String authToken = JwtService.addAuthentication((AccountCredentials) object);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", authToken);
                headers.add("Access-Control-Expose-Headers","Authorization");
                return returnResponse(Status.OK, headers);
            }
            return returnResponse((Status) object);

        } catch (JSONException e) {
            return returnResponse(Status.BAD_JSON);
        }

    }

    private ResponseEntity<String> returnResponse(Status status) {
        return ResponseProvider.getInstance().getResponse(status);
    }

    private ResponseEntity<String> returnResponse(Status status, JSONObject jsonObject) {
        return ResponseProvider.getInstance().getResponse(status, jsonObject);
    }

    private ResponseEntity<String> returnResponse(Status status, HttpHeaders headers) {
        return ResponseProvider.getInstance().getResponse(status, headers);
    }

    private ResponseEntity<String> returnResponse(Status status, JSONObject jsonObject, HttpHeaders headers) {
        return ResponseProvider.getInstance().getResponse(status, jsonObject, headers);
    }

}
