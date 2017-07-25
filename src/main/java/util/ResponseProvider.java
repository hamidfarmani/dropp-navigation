package util;

import model.enums.Status;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by kasra on 2/6/2017.
 */
public class ResponseProvider {
    private static ResponseProvider ourInstance = new ResponseProvider();


    private ResponseProvider() {
    }

    public static ResponseProvider getInstance() {
        return ourInstance;
    }


    public ResponseEntity<String> getResponse(Status status) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("statusCode", status.getStatusCode());
        jsonObject.put("status", status.getStatusDesc());
        return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> getResponse(Status status, JSONObject jsonObjectData) {
        JSONObject jsonObjectResponse = new JSONObject();
        jsonObjectResponse.put("statusCode", status.getStatusCode());
        jsonObjectResponse.put("status", status.getStatusDesc());
        jsonObjectResponse.put("data", jsonObjectData);
        return new ResponseEntity<String>(jsonObjectResponse.toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> getResponse(Status status, HttpHeaders headers) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("statusCode", status.getStatusCode());
        jsonObject.put("status", status.getStatusDesc());
        return new ResponseEntity<String>(jsonObject.toString(), headers, HttpStatus.OK);
    }

    public ResponseEntity<String> getResponse(Status status, JSONObject jsonObjectData, HttpHeaders headers) {
        JSONObject jsonObjectResponse = new JSONObject();
        jsonObjectResponse.put("statusCode", status.getStatusCode());
        jsonObjectResponse.put("status", status.getStatusDesc());
        jsonObjectResponse.put("data", jsonObjectData);
        return new ResponseEntity<String>(jsonObjectResponse.toString(), headers, HttpStatus.OK);
    }



}
