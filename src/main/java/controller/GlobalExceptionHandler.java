package controller;

import model.enums.Status;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import util.ResponseProvider;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by kasra on 7/10/2017.
 */
@Controller
public class GlobalExceptionHandler {

    @RequestMapping(value = "/errors")
    public ResponseEntity<String> errors(HttpServletRequest httpRequest) {
        int httpErrorCode = getErrorCode(httpRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-control-allow-origin", "*"); // TODO: 8/12/2017 سرور نهایی پاک شه
        switch (httpErrorCode) {
            case 400:
                return returnResponse(Status.BAD_REQUEST, headers);
            case 401:
                return returnResponse(Status.UNAUTHORIZED, headers);
            case 403:
                return returnResponse(Status.ACCESS_DENIED, headers);
            case 404:
                return returnResponse(Status.RESOURCE_NOT_FOUND, headers);
            case 500:
                return returnResponse(Status.UNKNOWN_ERROR, headers);
            case 405:
                return returnResponse(Status.METHOD_NOT_ALLOWED, headers);
            case 415:
                return returnResponse(Status.UNSUPPORTED_MEDIA_TYPE, headers);
            default:
                JSONObject jsonObject = new JSONObject()
                        .put("httpStatusCode", httpErrorCode);
                return returnResponse(Status.OK, jsonObject, headers);
        }
    }

    @RequestMapping(value = "/errors/maxUploadSizeExceeded")
    public ResponseEntity<String> uploadExceed() {
        return returnResponse(Status.MAX_UPLOAD_SIZE_EXCEEDED);
    }


    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
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