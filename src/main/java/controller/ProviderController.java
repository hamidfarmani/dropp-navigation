package controller;

import model.enums.Status;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ProviderService;
import util.HTTPAuthParser;
import util.IOCContainer;
import util.ResponseProvider;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@CrossOrigin
public class ProviderController {

    private ProviderService providerService;

    @Autowired
    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @RequestMapping(value = "/provider/payment/{driverUsername}", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> driverPayment(@PathVariable String driverUsername,@RequestHeader(value = "Authorization") String auth) {
        try{
            HTTPAuthParser httpAuthParser = (HTTPAuthParser)IOCContainer.getBean("httpAuthParser");
            String providerUsername = httpAuthParser.returnUsername(auth);
            return returnResponse(providerService.payment(driverUsername,providerUsername));
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
    }

    @RequestMapping(value = "/provider/claim", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> providerClaim(@RequestHeader(value = "Authorization") String auth) {
        try{
            HTTPAuthParser httpAuthParser = (HTTPAuthParser)IOCContainer.getBean("httpAuthParser");
            String providerUsername = httpAuthParser.returnUsername(auth);
            Object debt = providerService.calculateClaim(providerUsername);
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
    public ResponseEntity<String> mostDebtDrivers(@RequestHeader(value = "Authorization") String auth) {
        try{
            HTTPAuthParser httpAuthParser = (HTTPAuthParser)IOCContainer.getBean("httpAuthParser");
            String providerUsername = httpAuthParser.returnUsername(auth);
            Object mostDebtDrivers = providerService.mostDebtDrivers(providerUsername);
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
    public ResponseEntity<String> customDebtDrivers(@RequestHeader(value = "Authorization") String auth, @PathVariable String value) {

        try{
            if(value.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }
            Long debtValue = Long.valueOf(value);
            HTTPAuthParser httpAuthParser = (HTTPAuthParser)IOCContainer.getBean("httpAuthParser");
            String providerUsername = httpAuthParser.returnUsername(auth);
            Object customDebtDrivers = providerService.customDebtDrivers(providerUsername, debtValue);
            if(customDebtDrivers instanceof JSONObject){
                return returnResponse(Status.OK, (JSONObject)customDebtDrivers);
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
    public ResponseEntity<String> banDriver(@RequestHeader(value = "Authorization") String auth,@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String username;
        try {
            username = jsonObjectRequest.getString("username");
            HTTPAuthParser httpAuthParser = (HTTPAuthParser)IOCContainer.getBean("httpAuthParser");
            String providerUsername = httpAuthParser.returnUsername(auth);
            Status status = providerService.banDriver(providerUsername,username);
            return returnResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/provider/deactiveDriver", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> deactiveDriver(@RequestHeader(value = "Authorization") String auth,@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String username;
        try {
            username = jsonObjectRequest.getString("username");
            HTTPAuthParser httpAuthParser = (HTTPAuthParser)IOCContainer.getBean("httpAuthParser");
            String providerUsername = httpAuthParser.returnUsername(auth);
            Status status = providerService.deactiveDriver(providerUsername,username);
            return ResponseProvider.getInstance().getResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/provider/drivers/gt/{credit}/ban", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> banDriverByCredit(@RequestHeader(value = "Authorization") String auth,@PathVariable String credit) {
        try {
            if(credit.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }
            Long value = Long.valueOf(credit);
            HTTPAuthParser httpAuthParser = (HTTPAuthParser)IOCContainer.getBean("httpAuthParser");
            String providerUsername = httpAuthParser.returnUsername(auth);
            Status status = providerService.banDriverByCredit(providerUsername,value);
            return ResponseProvider.getInstance().getResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/provider/drivers", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewDriverOfProvider(@RequestHeader(value = "Authorization") String auth,@RequestParam(value = "q") String q,@RequestParam(value = "count") String count,@RequestParam(value = "offset") String pageIndex) {
        int c, p=0;
        try {
            if(!count.isEmpty()){
                c = Integer.valueOf(count);
            }else{
                c = -1;
            }
            if(!pageIndex.isEmpty()){
                p = Integer.valueOf(pageIndex);
            }
            HTTPAuthParser httpAuthParser = (HTTPAuthParser)IOCContainer.getBean("httpAuthParser");
            String providerUsername = httpAuthParser.returnUsername(auth);
            Object driverOfProvider = providerService.viewDriverOfProvider(providerUsername, q, c, p);
            if(driverOfProvider instanceof JSONObject){
                return returnResponse(Status.OK, (JSONObject)driverOfProvider);
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

    @RequestMapping(value = "/provider/report/drivers", method = RequestMethod.GET, produces = "application/vnd.ms-excel;charset=UTF-8")
    public void driverOfProviderReport(@RequestHeader(value = "Authorization") String auth,HttpServletResponse response) throws IOException {
        String fileName = "Provider_Drivers_Report.xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        HTTPAuthParser httpAuthParser = (HTTPAuthParser)IOCContainer.getBean("httpAuthParser");
        String providerUsername = httpAuthParser.returnUsername(auth);
        providerService.driverOfProviderReport(response,providerUsername);
    }

    @RequestMapping(value = "/provider/report/claim", method = RequestMethod.GET, produces = "application/vnd.ms-excel;charset=UTF-8")
    public void providerClaim(@RequestHeader(value = "Authorization") String auth,HttpServletResponse response) throws IOException {
        String fileName = "Provider_Claim_Report.xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        HTTPAuthParser httpAuthParser = (HTTPAuthParser)IOCContainer.getBean("httpAuthParser");
        String providerUsername = httpAuthParser.returnUsername(auth);
        providerService.providerClaim(response,providerUsername);
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
