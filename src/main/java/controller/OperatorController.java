package controller;

import model.entity.persistent.Address;
import model.enums.FileTypeState;
import model.enums.Status;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.OperatorService;
import util.HTTPAuthParser;
import util.IOCContainer;
import util.ResponseProvider;
import util.converter.FileTypeConverter;

import javax.servlet.http.HttpServletResponse;
import java.io.*;


@RestController
@CrossOrigin
public class OperatorController {

    private OperatorService operatorService;

    @Autowired
    public OperatorController(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @RequestMapping(value = "/operator/subscribes", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> subscribeRegister(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String firstName, lastName, phoneNumber;
        String line1,line2,postalCode,city;
        try {

            firstName = jsonObjectRequest.getString("firstName");
            lastName = jsonObjectRequest.getString("lastName");
            phoneNumber = jsonObjectRequest.getString("phoneNumber");
            line1 = jsonObjectRequest.getString("line1");
            line2 = jsonObjectRequest.getString("line2");
            postalCode = jsonObjectRequest.getString("postalCode");
            city = jsonObjectRequest.getString("city");

            if(firstName.isEmpty()
                    || lastName.isEmpty()
                    || phoneNumber.isEmpty()
                    || city.isEmpty()
                    || line1.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }

            Address address = (Address)IOCContainer.getBean("address");
            address.setLine1(line1);
            address.setLine2(line2);
            address.setPostalCode(postalCode);

            if (!operatorService.isSubscribePhoneNumberExist(phoneNumber)){
                Object subscribe = operatorService.subscribeRegister(firstName, lastName, phoneNumber, address, city);
                if(subscribe instanceof JSONObject){
                    return returnResponse(Status.OK,(JSONObject) subscribe);
                }else {
                    return returnResponse(Status.UNKNOWN_ERROR);
                }
            }else{
                return ResponseProvider.getInstance().getResponse(Status.PHONE_NUMBER_EXIST);
            }
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/confirmDriver/{driverUsername}", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> confirmUser(@PathVariable String driverUsername, @RequestHeader(value = "Authorization") String auth) {
        String op;
        try {
            HTTPAuthParser httpAuthParser = (HTTPAuthParser)IOCContainer.getBean("httpAuthParser");
            op = httpAuthParser.returnUsername(auth);
            Status status = operatorService.confirmUser(driverUsername, op);

            return ResponseProvider.getInstance().getResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/drivers/count", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewNumberOfAllDrivers() {
        Object allDrivers = operatorService.viewNumberOfAllDrivers();
        if (allDrivers == null) {
            return returnResponse(Status.UNKNOWN_ERROR);
        }
        return returnResponse(Status.OK, (JSONObject) allDrivers);
    }

    @RequestMapping(value = "/operator/drivers/online", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewOnlineAllDrivers() {
        Object allOnlineDrivers = operatorService.viewOnlineAllDrivers();
        if (allOnlineDrivers == null) {
            return returnResponse(Status.UNKNOWN_ERROR);
        }
        return returnResponse(Status.OK, (JSONObject) allOnlineDrivers);
    }

    @RequestMapping(value = "/operator/drivers/online/count", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewNumberOfOnlineDrivers() {
        Object numberOfOnlineDrivers = operatorService.viewNumberOfOnlineDrivers();
        if (numberOfOnlineDrivers == null) {
            return returnResponse(Status.UNKNOWN_ERROR);
        }
        return returnResponse(Status.OK, (JSONObject) numberOfOnlineDrivers);
    }

    @RequestMapping(value = "/operator/drivers/lowRate", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewLowRateDrivers() {
        Object lowRateDrivers=(operatorService.viewLowRateDrivers());
        if(lowRateDrivers instanceof JSONObject){
            return returnResponse(Status.OK,new JSONObject().put("users",lowRateDrivers));
        }
        return returnResponse((Status)lowRateDrivers);

    }

    @RequestMapping(value = "/operator/drivers", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> searchDriver(@RequestParam(value = "q") String q,@RequestParam(value = "count") String count,@RequestParam(value = "offset") String pageIndex) {
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
            Object driver = operatorService.searchDriver(q,c,p);

            if(driver instanceof JSONObject){
                return returnResponse(Status.OK,(JSONObject) driver);
            }else {
                return returnResponse(Status.NOT_FOUND);
            }
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/passengers", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> searchPassenger(@RequestParam(value = "q") String q,@RequestParam(value = "count") String count,@RequestParam(value = "offset") String pageIndex) {
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
            Object object = operatorService.searchPassenger(q,c,p);

            if(object instanceof JSONObject){
                return returnResponse(Status.OK,(JSONObject) object);
            }else {
                return returnResponse(Status.BAD_DATA);
            }
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/organizations", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> organizationRegister(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String name, username, password, email, phoneNumber, workNumber;
        String employees;
        int employeeCount;
        try {
            name = jsonObjectRequest.getString("name");
            username = jsonObjectRequest.getString("username");
            password = jsonObjectRequest.getString("password");
            email = jsonObjectRequest.getString("email");
            phoneNumber = jsonObjectRequest.getString("phoneNumber");
            workNumber = jsonObjectRequest.getString("workNumber");
            employees = jsonObjectRequest.getString("employeeCount");
            if(!name.isEmpty()
                    && !username.isEmpty()
                    && !password.isEmpty()
                    && !phoneNumber.isEmpty()
                    && !employees.isEmpty()) {
                employeeCount = Integer.valueOf(employees);
                if(!operatorService.isOrganizationUsernameExist(username)) {
                    if (!operatorService.isOrganizationPhoneNumberExist(phoneNumber)) {
                        Status status = operatorService.organizationRegister(name, username, password, email, phoneNumber, workNumber, employeeCount);
                        return ResponseProvider.getInstance().getResponse(status);
                    } else {
                        return returnResponse(Status.PHONE_NUMBER_EXIST);
                    }
                }else{
                    return returnResponse(Status.USERNAME_EXIST);
                }
            }else{
                return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
            }
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/banPassenger", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> banPassenger(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String username;
        try {
            username = jsonObjectRequest.getString("username");
            Status status = operatorService.banPassenger(username);

            return ResponseProvider.getInstance().getResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/unBanPassenger", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> unBanPassenger(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String username;
        try {
            username = jsonObjectRequest.getString("username");
            Status status = operatorService.unBanPassenger(username);

            return ResponseProvider.getInstance().getResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/banDriver", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> banDriver(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String username;
        try {
            username = jsonObjectRequest.getString("username");
            Status status = operatorService.banDriver(username);

            return ResponseProvider.getInstance().getResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/unBanDriver", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> unBanDriver(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String username;
        try {
            username = jsonObjectRequest.getString("username");
            Status status = operatorService.unBanDriver(username);

            return ResponseProvider.getInstance().getResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/drivers/banned", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewBanDrivers() {
        try {
            Object bans = operatorService.viewBanDrivers();
            return ResponseProvider.getInstance().getResponse(Status.OK, (JSONObject) bans);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/passengers/banned", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewBanPassengers() {
        try {
            Object bans = operatorService.viewBanPassengers();
            return ResponseProvider.getInstance().getResponse(Status.OK, (JSONObject)bans);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/organizations", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewOrganization(@RequestParam(value = "q") String q,@RequestParam(value = "count") String count,@RequestParam(value = "offset") String pageIndex) {
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
            Object object = operatorService.searchOrganization(q,c,p);
            if(object instanceof JSONObject){
                return returnResponse(Status.OK,(JSONObject) object);
            }else {
                return returnResponse((Status) object);
            }
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/organizations/{username}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewOrganization(@PathVariable String username) {
        try {
            Object object = operatorService.viewOrganization(username);
            if(object instanceof JSONObject){
                return returnResponse(Status.OK,(JSONObject) object);
            }else {
                return returnResponse((Status) object);
            }
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/confirmOrganization/{username}", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> confirmOrganization(@PathVariable String username) {
        try {
            Status status = operatorService.confirmOrganization(username);
            return returnResponse(status);

        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/organizations/{username}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> removeOrganization(@PathVariable String username) {
        try {
            Status status = operatorService.removeOrganization(username);
            return returnResponse(status);

        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/drivers/{username}/credit", method = RequestMethod.GET, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewDriverCredit(@PathVariable String username) {
        try {
            Object object = operatorService.viewDriverCredit(username);
            if(object instanceof JSONObject){
                return returnResponse(Status.OK,(JSONObject) object);
            }else {
                return returnResponse((Status) object);
            }
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/drivers/credit", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewAllDriverCredit() {
        try {
            Object object = operatorService.viewAllDriverCredit();
            if(object instanceof JSONObject){
                return returnResponse(Status.OK,(JSONObject) object);
            }else {
                return returnResponse((Status) object);
            }
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/drivers/{driverUsername}/tickets/passengers", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewPassengersTicket(@PathVariable String driverUsername) {
        try {
            Object object = operatorService.viewPassengersTicket(driverUsername);
            if(object instanceof JSONObject){
                return returnResponse(Status.OK,(JSONObject) object);
            }else {
                return returnResponse((Status) object);
            }
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    //send Message
//    @RequestMapping(value = "/operator/sendMessage", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
//    public ResponseEntity<String> sendMessage(@RequestBody String request, @RequestHeader(value = "Authorization") String auth) {
//        JSONObject jsonObjectRequest = new JSONObject(request);
//        String username,message;
//        try {
//            String operatorUsername = ((HTTPAuthParser)IOCContainer.getBean("httpAuthParser")).returnUsername(auth);
//            username = jsonObjectRequest.getString("username");
//            message = jsonObjectRequest.getString("message");
//            Status status = operatorService.sendMessage(username,message, operatorUsername);
//
//            return returnResponse(status);
//
//        } catch (JSONException e) {
//            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
//        }
//    }

    @RequestMapping(value = "/operator/tickets/{ticketID}/resolve", method = RequestMethod.PATCH, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> resolveTicket(@PathVariable String ticketID) {
        try {
            if(ticketID.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }
            Long id = Long.valueOf(ticketID);
            Status status = operatorService.resolveTicket(id);
            return returnResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/tickets", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewTickets() {
        try {
            Object object = operatorService.viewTickets();
            if(object instanceof JSONObject){
                return returnResponse(Status.OK,(JSONObject) object);
            }else {
                return returnResponse((Status) object);
            }
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/subscribes", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> searchSubscribe(@RequestParam(value = "q") String q,@RequestParam(value = "count") String count,@RequestParam(value = "offset") String pageIndex) {
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
            Object subscribes = operatorService.searchSubscribe(q,c,p);

            if(subscribes instanceof JSONObject){
                return returnResponse(Status.OK,(JSONObject) subscribes);
            }else {
                return returnResponse(Status.NOT_FOUND);
            }
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/trips/online", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewOnlineTrips() {
        Object allOnlinetrips = operatorService.viewOnlineTrips();
        if (allOnlinetrips == null) {
            return returnResponse(Status.UNKNOWN_ERROR);
        }
        return returnResponse(Status.OK, (JSONObject) allOnlinetrips);
    }

    @RequestMapping(value = "/operator/passengers/count", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewNumberOfPassengers() {
        Object numberOfPassengers = operatorService.viewNumberOfPassengers();
        if (numberOfPassengers == null) {
            return returnResponse(Status.UNKNOWN_ERROR);
        }
        return returnResponse(Status.OK, (JSONObject) numberOfPassengers);
    }

    @RequestMapping(value = "/operator/passengers/new/count", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewNumberNewOfPassengers() {
        Object numberOfPassengers = operatorService.viewNumberOfNewPassengers();
        if (numberOfPassengers == null) {
            return returnResponse(Status.UNKNOWN_ERROR);
        }
        return returnResponse(Status.OK, (JSONObject) numberOfPassengers);
    }

    @RequestMapping(value = "/operator/trips/online/count", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewNumberOfOnlineTrips() {
        Object onlineTripsCount = operatorService.viewNumberOfOnlineTrips();
        if (onlineTripsCount == null) {
            return returnResponse(Status.UNKNOWN_ERROR);
        }
        return returnResponse(Status.OK, (JSONObject) onlineTripsCount);
    }

    @RequestMapping(value = "/operator/trips/count", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewNumberOfTrips() {
        Object onlineTripsCount = operatorService.viewNumberOfTrips();
        if (onlineTripsCount == null) {
            return returnResponse(Status.UNKNOWN_ERROR);
        }
        return returnResponse(Status.OK, (JSONObject) onlineTripsCount);
    }

    @RequestMapping(value = "/operator/organizations/count", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewNumberOfOrganizations() {
        Object numberOfOrganizaions = operatorService.viewNumberOfOrganization();
        if (numberOfOrganizaions == null) {
            return returnResponse(Status.UNKNOWN_ERROR);
        }
        return returnResponse(Status.OK, (JSONObject) numberOfOrganizaions);
    }

    @RequestMapping(value = "/operator/organizations/new/count", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewNumberOfNewOrganizations() {
        Object numberOfOrganizaions = operatorService.viewNumberOfNewOrganization();
        if (numberOfOrganizaions == null) {
            return returnResponse(Status.UNKNOWN_ERROR);
        }
        return returnResponse(Status.OK, (JSONObject) numberOfOrganizaions);
    }

    @RequestMapping(value = "/operator/drivers/{driverUsername}/payment", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> paymentRequestRegister(@PathVariable String driverUsername) {
        try {
            if(driverUsername.isEmpty()){
                return  returnResponse(Status.BAD_DATA);
            }
            Status subscribe = operatorService.paymentRequestRegister(driverUsername);
            return returnResponse(subscribe);
        } catch(JSONException e){
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch(IllegalArgumentException e){
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/operator/states", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewAllStates() {
        Object allStates = operatorService.viewAllStates();
        if (allStates == null) {
            return returnResponse(Status.UNKNOWN_ERROR);
        }
        return returnResponse(Status.OK, (JSONObject) allStates);
    }

    @RequestMapping(value = "/operator/cities/{stateID}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewAllCities(@PathVariable String stateID) {
        if(stateID.isEmpty()){
            return returnResponse(Status.BAD_DATA);
        }
        Long state = Long.valueOf(stateID);
        Object allStates = operatorService.viewCityByStateID(state);
        if (allStates == null) {
            return returnResponse(Status.UNKNOWN_ERROR);
        }
        return returnResponse(Status.OK, (JSONObject) allStates);
    }

    @RequestMapping(value = "/operator/vouchers", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewAllVouchers() {
        Object tickets = operatorService.viewAllVouchers();
        if (tickets instanceof JSONObject) {
            return returnResponse(Status.OK,(JSONObject) tickets);
        }else{
            return returnResponse((Status)tickets);
        }
    }

    @RequestMapping(value = "/operator/drivers/files/{fileType}/{driverUsername}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody void downloadFile(HttpServletResponse response, @PathVariable String driverUsername, @PathVariable String fileType) throws IOException {
        try {
            FileTypeConverter fileTypeConverter = (FileTypeConverter)IOCContainer.getBean("fileTypeConverter");
            FileTypeState fileTypeState = fileTypeConverter.convertToEntityAttribute(fileType.charAt(0));
            File file = operatorService.downloadFile(driverUsername,fileTypeState);
            InputStream inputStream = new FileInputStream(file);
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            response.setHeader("Content-Length", String.valueOf(file.length()));
            response.setHeader("isImageExist", "true");
            FileCopyUtils.copy(inputStream, response.getOutputStream());

        } catch (FileNotFoundException e) {
            response.setHeader("isImageExist", "false");
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/operator/drivers/files/{fileType}/{driverUsername}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String driverUsername, @PathVariable String fileType) {
        try {
            FileTypeConverter fileTypeConverter = (FileTypeConverter)IOCContainer.getBean("fileTypeConverter");
            if(fileType.isEmpty()
                    || driverUsername.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }
            FileTypeState fileTypeState = fileTypeConverter.convertToEntityAttribute(fileType.charAt(0));
            return returnResponse(operatorService.uploadFile(file, driverUsername,fileTypeState));
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return returnResponse(Status.BAD_DATA);
        }

    }

    @RequestMapping(value = "/operator/trips", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> searchTrip(@RequestParam(value = "q") String q,@RequestParam(value = "count") String count,@RequestParam(value = "offset") String pageIndex) {
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
            Object driver = operatorService.searchTrip(q,c,p);

            if(driver instanceof JSONObject){
                return returnResponse(Status.OK,(JSONObject) driver);
            }else {
                return returnResponse(Status.NOT_FOUND);
            }
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
