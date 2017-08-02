package controller;

import model.enums.Status;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ProviderService;
import util.ResponseProvider;

@RestController
@CrossOrigin
public class ProviderController {

    private ProviderService providerService;

    @Autowired
    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @RequestMapping(value = "/provider/payment/{driverUsername}", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> driverPayment(@PathVariable String driverUsername) {
        try{

            return returnResponse(providerService.payment(driverUsername));
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
    }

    @RequestMapping(value = "/provider/claim", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> providerClaim() {
        try{
            Object debt = providerService.calculateClaim();
            if(debt instanceof JSONObject){
                return returnResponse(Status.OK, (JSONObject)debt);
            }else{
                return returnResponse(Status.UNKNOWN_ERROR);
            }

        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
    }

    @RequestMapping(value = "/provider/drivers/debt", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> providerClaimFromDrivers() {
        try{
            Object debt = providerService.driversDebt();
            if(debt instanceof JSONObject){
                return returnResponse(Status.OK, (JSONObject)debt);
            }else{
                return returnResponse(Status.UNKNOWN_ERROR);
            }

        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
    }

    @RequestMapping(value = "/provider/drivers/mostDebt", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> mostDebtDrivers() {
        try{
            Object mostDebtDrivers = providerService.mostDebtDrivers();
            if(mostDebtDrivers instanceof JSONObject){
                return returnResponse(Status.OK, (JSONObject)mostDebtDrivers);
            }else{
                return returnResponse(Status.UNKNOWN_ERROR);
            }

        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
    }

    @RequestMapping(value = "/provider/drivers/debt/gt/{value}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> customDebtDrivers(@PathVariable String value) {

        try{
            if(value.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }
            Long debtValue = Long.valueOf(value);
            Object mostDebtDrivers = providerService.customDebtDrivers(debtValue);
            if(mostDebtDrivers instanceof JSONObject){
                return returnResponse(Status.OK, (JSONObject)mostDebtDrivers);
            }else{
                return returnResponse(Status.UNKNOWN_ERROR);
            }

        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
    }

    @RequestMapping(value = "/provider/banDriver", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> banDriver(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String username;
        try {
            username = jsonObjectRequest.getString("username");
            Status status = providerService.banDriver(username);
            return returnResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/provider/deactiveDriver", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> deactiveDriver(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String username;
        try {
            username = jsonObjectRequest.getString("username");
            Status status = providerService.deactiveDriver(username);
            return ResponseProvider.getInstance().getResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/provider/drivers/gt/{credit}/ban", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> banDriverByCredit(@PathVariable String credit) {
        try {
            if(credit.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }
            Long value = Long.valueOf(credit);
            Status status = providerService.banDriverByCredit(value);
            return ResponseProvider.getInstance().getResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
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
