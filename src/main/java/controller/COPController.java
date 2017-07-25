package controller;

import model.enums.Status;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.COPService;
import util.ResponseProvider;


@RestController
@CrossOrigin
public class COPController {

    private COPService copService;


    @Autowired
    public COPController(COPService copService) {
        this.copService = copService;
    }

    @RequestMapping(value = "/cop/cars", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> carRegister(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String name;
        Long manufactureID;
        try {
            name = jsonObjectRequest.getString("name").trim();
            manufactureID = jsonObjectRequest.getLong("manufactureID");
            if(name.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }
            return  returnResponse(copService.carRegister(name,manufactureID));

        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
    }

    @RequestMapping(value = "/cop/manufactures", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> manufactureRegister(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String name;
        try {

            name = jsonObjectRequest.getString("name").trim();
            if(name.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }
            return returnResponse(copService.manufactureRegister(name));

        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }

    }

    @RequestMapping(value = "/cop/cars/{vehicleID}/distance", method = RequestMethod.GET, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> carDistance(@PathVariable String vehicleID) {
        try {
            JSONObject distance = copService.carDistance(Long.valueOf(vehicleID));
            return returnResponse(Status.OK, distance);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
    }

    @RequestMapping(value = "/cop/manufactures", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getAllManufactures() {
        try {
            return returnResponse(Status.OK, (JSONObject) copService.getAllManufactures());
        } catch (JSONException e) {
            e.printStackTrace();
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
