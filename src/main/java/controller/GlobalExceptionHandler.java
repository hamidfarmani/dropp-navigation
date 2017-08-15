package controller;

import model.enums.Status;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import util.ResponseProvider;

/**
 * Created by kasra on 7/10/2017.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JSONException.class)
    public ResponseEntity<String> jsonException() {
        return returnResponse(Status.BAD_JSON);
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