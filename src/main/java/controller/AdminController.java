package controller;

import model.entity.persistent.State;
import model.enums.City;
import model.enums.Gender;
import model.enums.ServiceType;
import model.enums.Status;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.AdminService;
import util.*;
import util.converter.CityConverter;
import util.converter.GenderConverter;
import util.converter.ServiceTypeConverter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
@CrossOrigin
public class AdminController {

    private AdminService adminManager;


    @Autowired
    public AdminController(AdminService adminManager) {
        this.adminManager = adminManager;
    }

    @RequestMapping(value = "/admin/masters", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> masterRegister(@RequestBody String request, @RequestHeader(value = "Authorization") String auth) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String username,creatorUsername, password, PhoneNumber, cityStr, genderStr,firstname,lastname,workNumber,email;
        City city;
        Gender gender;
        Date birthDate;
        int year,month,day;
        try {
            HTTPAuthParser httpAuthParser = (HTTPAuthParser)IOCContainer.getBean("httpAuthParser");
            creatorUsername = httpAuthParser.returnUsername(auth);
            firstname = jsonObjectRequest.getString("firstName").trim();
            lastname = jsonObjectRequest.getString("lastName").trim();
            username = jsonObjectRequest.getString("username").trim();
            email = jsonObjectRequest.getString("email").trim();
            PhoneNumber = jsonObjectRequest.getString("phoneNumber").trim().replace(" ", "");
            workNumber = jsonObjectRequest.getString("workNumber").trim().replace(" ", "");
            password = jsonObjectRequest.getString("password").trim();
            JSONObject birthD = jsonObjectRequest.getJSONObject("birthDate");
            year = Integer.valueOf(String.valueOf(birthD.get("year")));
            month = Integer.valueOf(String.valueOf(birthD.get("month")));
            day = Integer.valueOf(String.valueOf(birthD.get("day")));
            cityStr = jsonObjectRequest.getString("city").trim();
            genderStr = jsonObjectRequest.getString("gender").trim();
            GenderConverter genderConverter = (GenderConverter)IOCContainer.getBean("genderConverter");
            gender = genderConverter.convertToEntityAttribute(genderStr.charAt(0));
            CityConverter cityConverter = (CityConverter)IOCContainer.getBean("cityConverter");
            city = cityConverter.convertToEntityAttribute(cityStr);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
            birthDate = new Date();
            String dataStr = String.valueOf(year + "/" + month + "/" + day);
            birthDate = dateFormatter.parse(dataStr);

            if (username.isEmpty()
                    || PhoneNumber.isEmpty()
                    || password.isEmpty()
                    ) {
                return returnResponse(Status.BAD_DATA);
            }
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        } catch (ParseException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_DATA);
        }
        if ((!Validation.getInstance().validateUsername(username)) || username.length() > 20 || username.contains(":")) {
            return returnResponse(Status.BAD_USERNAME);
        }
        if ((!Validation.getInstance().validatePassword(password)) || password.length() > 30) {
            return returnResponse(Status.BAD_PASSWORD);
        }
        if (!Validation.getInstance().validatePhoneNumber(PhoneNumber)) {
            return returnResponse(Status.BAD_PHONE_NUMBER);
        }

        if (!adminManager.isUsernameExist(username)) {
            if(!adminManager.isPhoneNumberExist(PhoneNumber)) {
                Object object = adminManager.masterRegister(creatorUsername, firstname,lastname,birthDate,email,PhoneNumber,workNumber, username, password,gender,city);
                if (object == null) {
                    return returnResponse(Status.UNKNOWN_ERROR);
                }
                if (object instanceof Status) {
                    return returnResponse((Status) object);
                }
            }else{
                return returnResponse(Status.PHONE_NUMBER_EXIST);
            }
        }else{
            return returnResponse(Status.USERNAME_EXIST);
        }
        return returnResponse(Status.OK);
    }

    @RequestMapping(value = "/admin/operators/{operatorID}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> operatorRemove(@PathVariable String operatorID) {
        try{
            return returnResponse(adminManager.operatorRemove(Long.valueOf(operatorID)));
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
    }

    @RequestMapping(value = "/admin/services", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> activeServiceRegister(@RequestBody String request, @RequestHeader(value = "Authorization") String auth) {
        JSONObject jsonObjectRequest = new JSONObject(request);

        String cityStr, serviceTypeStr;
        ServiceType type;
        City city;
        try {
            serviceTypeStr = jsonObjectRequest.getString("serviceType");
            cityStr = jsonObjectRequest.getString("city").trim();

            if (serviceTypeStr.isEmpty()
                    || cityStr.isEmpty()
                    ) {
                return returnResponse(Status.BAD_DATA);
            }
            type = ((ServiceTypeConverter) IOCContainer.getBean("serviceTypeConverter")).convertToEntityAttribute(serviceTypeStr);
            city = ((CityConverter) IOCContainer.getBean("cityConverter")).convertToEntityAttribute(cityStr);

        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }

        Status serviceTypeRegisterationStatus = adminManager.serviceTypeRegister(city, type);
        if (serviceTypeRegisterationStatus == null) {
            return returnResponse(Status.UNKNOWN_ERROR);
        }
        Status reloadStatus;
        if(serviceTypeRegisterationStatus==Status.OK){
            Reloader r =(Reloader)IOCContainer.getBean("reloader");
            reloadStatus = r.reload(auth,'S');
            if(reloadStatus == Status.OK){
                return returnResponse(Status.OK);
            }else{
                return returnResponse(Status.RELOAD_NOT_OCCURRED);
            }
        }else{
            return returnResponse(serviceTypeRegisterationStatus);
        }
    }

    @RequestMapping(value = "/admin/services/{cityName}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewActiveServices(@PathVariable String cityName) {
        try {
            if (cityName.isEmpty()) {
                return returnResponse(Status.BAD_DATA);
            }
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
        City city = ((CityConverter) IOCContainer.getBean("cityConverter")).convertToEntityAttribute(cityName);
        Object object = adminManager.viewActiveServices(city);
        if (object == null) {
            return returnResponse(Status.UNKNOWN_ERROR);
        }
        if (object instanceof JSONObject) {
            return returnResponse(Status.OK, (JSONObject) object);
        }
        return returnResponse(Status.OK);
    }

    @RequestMapping(value = "/admin/searchRadiuses", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> searchRadiusRegister(@RequestBody String request, @RequestHeader(value = "Authorization") String auth) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String serviceTypeStr,radiusStr;
        double radius;
        try {
            radiusStr = jsonObjectRequest.getString("radius");
            serviceTypeStr = jsonObjectRequest.getString("serviceType");
            if(serviceTypeStr.isEmpty() || radiusStr.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }
            radius = Long.valueOf(radiusStr);
            ServiceTypeConverter serviceTypeConverter = (ServiceTypeConverter) IOCContainer.getBean("serviceTypeConverter");
            ServiceType serviceType = serviceTypeConverter.convertToEntityAttribute(serviceTypeStr);
            Object object = adminManager.radiusRegister(radius,serviceType);
            if(object instanceof JSONObject){
                Status reloadStatus;
                Reloader r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(auth,'R');
                if(reloadStatus == Status.OK){
                    return returnResponse(Status.OK,(JSONObject) object);
                }else{
                    return returnResponse(Status.RELOAD_NOT_OCCURRED,(JSONObject) object);
                }
            } else {
                return returnResponse((Status) object);
            }
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/admin/searchRadiuses/{serviceType}", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> searchRadiusUpdate(@PathVariable String serviceType, @RequestBody String request, @RequestHeader(value = "Authorization") String auth) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        double radius;
        try {
            radius = jsonObjectRequest.getDouble("radius");
            ServiceTypeConverter serviceTypeConverter = (ServiceTypeConverter) IOCContainer.getBean("serviceTypeConverter");
            ServiceType service = serviceTypeConverter.convertToEntityAttribute(serviceType);
            Object object = adminManager.radiusUpdate(radius,service);
            if(object instanceof JSONObject){
                Status reloadStatus;
                Reloader r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(auth,'R');
                if(reloadStatus == Status.OK){
                    return returnResponse(Status.OK,(JSONObject) object);
                }else{
                    return returnResponse(Status.RELOAD_NOT_OCCURRED,(JSONObject) object);
                }
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

    @RequestMapping(value = "/admin/searchRadiuses", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewSearchRadius() {
        try {
            Object object = adminManager.viewSearchRadius();
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

    @RequestMapping(value = "/admin/searchRadiuses/{serviceType}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewSearchRadiusByServiceType(@PathVariable String serviceType) {
        try {
            ServiceTypeConverter serviceTypeConverter = (ServiceTypeConverter) IOCContainer.getBean("serviceTypeConverter");
            ServiceType service = serviceTypeConverter.convertToEntityAttribute(serviceType);
            Object object = adminManager.viewSearchRadiusByServiceType(service);
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

    @RequestMapping(value = "/admin/tariffs", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> tariffRegister(@RequestBody String request, @RequestHeader(value = "Authorization") String auth) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        try {
            String cityName = jsonObjectRequest.getString("city").trim();
            City city = ((CityConverter) IOCContainer.getBean("cityConverter")).convertToEntityAttribute(cityName);
            String type = jsonObjectRequest.getString("serviceType");
            String before2KMStr = jsonObjectRequest.getString("before2KM");
            String after2KMStr = jsonObjectRequest.getString("after2KM");
            String perMinStr = jsonObjectRequest.getString("perMin");
            String waitingMinStr = jsonObjectRequest.getString("waitingMin");
            String entranceStr = jsonObjectRequest.getString("entrance");
            String twoWayCostStr = jsonObjectRequest.getString("twoWayCost");
            String coShareStr = jsonObjectRequest.getString("genoShare");

            if(type.isEmpty() || cityName.isEmpty()
                    || before2KMStr.isEmpty() || after2KMStr.isEmpty()
                    || perMinStr.isEmpty() || waitingMinStr.isEmpty()
                    || entranceStr.isEmpty() || twoWayCostStr.isEmpty()
                    || coShareStr.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }

            double before2KM = Double.valueOf(before2KMStr);
            double after2KM = Double.valueOf(after2KMStr);
            double perMin = Double.valueOf(perMinStr);
            double waitingMin = Double.valueOf(waitingMinStr);
            double entrance = Double.valueOf(entranceStr);
            int twoWayCost = Integer.valueOf(twoWayCostStr);
            int coShare = Integer.valueOf(coShareStr);

            ServiceType serviceType = ((ServiceTypeConverter) IOCContainer.getBean("serviceTypeConverter")).convertToEntityAttribute(type);
            Status tarifRegisterStatus = adminManager.tariffRegister(city, before2KM, after2KM, perMin, waitingMin, entrance, serviceType, twoWayCost, coShare);
            Status reloadStatus = null;
            if(tarifRegisterStatus==Status.OK){
                Reloader r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(auth,'T');
                if(reloadStatus == Status.OK){
                    return returnResponse(Status.OK);
                }else{
                    return returnResponse(Status.RELOAD_NOT_OCCURRED);
                }
            }else{
                return returnResponse(tarifRegisterStatus);
            }



        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
    }

    @RequestMapping(value = "/admin/tariffs", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> tariffUpdate(@RequestBody String request, @RequestHeader(value = "Authorization") String auth) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        try {
            String tariffIDStr = jsonObjectRequest.getString("tariffID");
            String before2KMStr = jsonObjectRequest.getString("before2KM");
            String after2KMStr = jsonObjectRequest.getString("after2KM");
            String perMinStr = jsonObjectRequest.getString("perMin");
            String waitingMinStr = jsonObjectRequest.getString("waitingMin");
            String entranceStr = jsonObjectRequest.getString("entrance");
            String twoWayCostStr = jsonObjectRequest.getString("twoWayCost");
            String coShareStr = jsonObjectRequest.getString("genoShare");

            if(tariffIDStr.isEmpty() || before2KMStr.isEmpty() || after2KMStr.isEmpty()
                    || perMinStr.isEmpty() || waitingMinStr.isEmpty()
                    || entranceStr.isEmpty() || twoWayCostStr.isEmpty()
                    || coShareStr.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }

            Long tariffID = Long.valueOf(tariffIDStr);
            double before2KM = Double.valueOf(before2KMStr);
            double after2KM = Double.valueOf(after2KMStr);
            double perMin = Double.valueOf(perMinStr);
            double waitingMin = Double.valueOf(waitingMinStr);
            double entrance = Double.valueOf(entranceStr);
            int twoWayCost = Integer.valueOf(twoWayCostStr);
            int coShare = Integer.valueOf(coShareStr);

            Status tarifUpdateStatus = adminManager.tariffUpdate(tariffID, before2KM,after2KM,perMin,waitingMin,entrance,twoWayCost,coShare);
            Status reloadStatus = null;
            if(tarifUpdateStatus==Status.OK){
                Reloader r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(auth,'T');
                if(reloadStatus == Status.OK){
                    return returnResponse(Status.OK);
                }else{
                    return returnResponse(Status.RELOAD_NOT_OCCURRED);
                }
            }else{
                return returnResponse(tarifUpdateStatus);
            }
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
    }

    @RequestMapping(value = "/admin/tariffs/{cityName}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> searchTariff(@PathVariable String cityName) {
        try {
            Object searchTariff = adminManager.searchTariff(cityName);
            if(searchTariff instanceof JSONObject){
                return returnResponse(Status.OK,(JSONObject) searchTariff);
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

    @RequestMapping(value = "/admin/banMaster", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> banOperator(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String username;
        try {
            username = jsonObjectRequest.getString("username");
            Status status = adminManager.banOperator(username);

            return ResponseProvider.getInstance().getResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/admin/unBanMaster", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> unBanOperator(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String username;
        try {
            username = jsonObjectRequest.getString("username");
            Status status = adminManager.unBanOperator(username);

            return ResponseProvider.getInstance().getResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/admin/services/{serviceID}/disable", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> disableService(@PathVariable String serviceID, @RequestHeader(value = "Authorization") String auth) {
        Long id;
        try {
            if(serviceID.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }
            id = Long.valueOf(serviceID);
            Status disableServiceStatus = adminManager.disableService(id);
            Status reloadStatus;
            if(disableServiceStatus==Status.OK){
                Reloader r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(auth,'S');
                if(reloadStatus == Status.OK){
                    return returnResponse(Status.OK);
                }else{
                    return returnResponse(Status.RELOAD_NOT_OCCURRED);
                }
            }else{
                return returnResponse(disableServiceStatus);
            }
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/admin/services/{serviceID}/enable", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> enableService(@PathVariable String serviceID, @RequestHeader(value = "Authorization") String auth) {
        Long id;
        try {
            if(serviceID.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }
            id = Long.valueOf(serviceID);
            Status enableServiceStatus = adminManager.enableService(id);
            Status reloadStatus;
            if(enableServiceStatus==Status.OK){
                Reloader r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(auth,'S');
                if(reloadStatus == Status.OK){
                    return returnResponse(Status.OK);
                }else{
                    return returnResponse(Status.RELOAD_NOT_OCCURRED);
                }
            }else{
                return returnResponse(enableServiceStatus);
            }
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/admin/bugs", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewBugs() {
        Object object = adminManager.viewBugs();
        if (object instanceof JSONObject) {
            return returnResponse(Status.OK, (JSONObject) object);
        }else{
            return returnResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/admin/bugs/{bugID}/resolve", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> resolveBug(@PathVariable String bugID) {
        if(bugID.isEmpty()){
            return returnResponse(Status.BAD_DATA);
        }
        Long id = Long.valueOf(bugID);
        Status status = adminManager.resolveBug(id);
        return returnResponse(status);
    }

    @RequestMapping(value = "/admin/settings", method = RequestMethod.GET,  produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewSystemSetting() {
        Object object = adminManager.viewSystemSetting();
        if (object instanceof JSONObject) {
            return returnResponse(Status.OK, (JSONObject) object);
        }
        return returnResponse(Status.UNKNOWN_ERROR);
    }

    @RequestMapping(value = "/admin/settings", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> systemSettingUpdate(@RequestBody String request, @RequestHeader(value = "Authorization") String auth) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        try {
            Boolean smsSender = jsonObjectRequest.getBoolean("smsSender");
            Boolean emailSender = jsonObjectRequest.getBoolean("emailSender");
            Boolean dailySmsReport = jsonObjectRequest.getBoolean("dailySmsReport");
            Boolean weeklySmsReport = jsonObjectRequest.getBoolean("weeklySmsReport");
            Boolean dailyEmailReport = jsonObjectRequest.getBoolean("dailyEmailReport");
            Boolean weeklyEmailReport = jsonObjectRequest.getBoolean("weeklyEmailReport");
            Boolean monthlyEmailReport = jsonObjectRequest.getBoolean("monthlyEmailReport");
            Boolean exceptionOccurrenceSms = jsonObjectRequest.getBoolean("exceptionOccurrenceSms");
            Boolean exceptionOccurrenceEmail = jsonObjectRequest.getBoolean("exceptionOccurrenceEmail");
            Boolean IOSUpdate = jsonObjectRequest.getBoolean("IOSUpdate");
            Boolean IOSUpdateCritical = jsonObjectRequest.getBoolean("IOSUpdateCritical");
            Boolean androidUpdate = jsonObjectRequest.getBoolean("androidUpdate");
            Boolean androidUpdateCritical = jsonObjectRequest.getBoolean("androidUpdateCritical");
            Boolean allowCompetitors = jsonObjectRequest.getBoolean("allowCompetitors");

            Status systemUpdateStatus = adminManager.settingUpdate(smsSender, emailSender,dailySmsReport,weeklySmsReport,
                    dailyEmailReport,weeklyEmailReport,monthlyEmailReport,exceptionOccurrenceSms,exceptionOccurrenceEmail,
                    IOSUpdate,IOSUpdateCritical,androidUpdate,androidUpdateCritical,allowCompetitors);
            Status reloadStatus = null;
            if(systemUpdateStatus==Status.OK){
                Reloader r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(auth,'Y');
                if(reloadStatus == Status.OK){
                    return returnResponse(Status.OK);
                }else{
                    return returnResponse(Status.RELOAD_NOT_OCCURRED);
                }
            }else{
                return returnResponse(systemUpdateStatus);
            }
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
    }

    @RequestMapping(value = "/admin/states", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> stateRegister(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String name;
        try {
            name = jsonObjectRequest.getString("name").trim();
            if (name.isEmpty()) {
                return returnResponse(Status.BAD_DATA);
            }
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }

        State state = adminManager.isStateExist(name);
        if (state == null) {
            Object object = adminManager.stateRegister(name);
            if (object == null) {
                return returnResponse(Status.UNKNOWN_ERROR);
            }
            if (object instanceof Status) {
                return returnResponse((Status) object);
            }
        }else{
            return returnResponse(Status.STATE_EXIST);
        }
        return returnResponse(Status.OK);
    }

    @RequestMapping(value = "/admin/cities/{stateID}", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> cityRegister(@RequestBody String request, @PathVariable String stateID) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String name;
        Long state;
        try {
            name = jsonObjectRequest.getString("name").trim();
            if (name.isEmpty() || stateID.isEmpty()) {
                return returnResponse(Status.BAD_DATA);
            }
            state = Long.valueOf(stateID);
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
        model.entity.persistent.City cityExist = adminManager.isCityExist(name, state);
        if (cityExist == null) {
            Object object = adminManager.cityRegister(name,state);
            if (object == null) {
                return returnResponse(Status.UNKNOWN_ERROR);
            }
            if (object instanceof Status) {
                return returnResponse((Status) object);
            }
        }else{
            return returnResponse(Status.CITY_EXIST);
        }
        return returnResponse(Status.OK);
    }

    @RequestMapping(value = "/admin/report/drivers/age", method = RequestMethod.GET, produces = "application/vnd.ms-excel;charset=UTF-8")
    public void driversAgeReport(HttpServletResponse response) throws IOException {
        String fileName = "Drivers_Age_Report.xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        adminManager.driversAgeReport(response);

//        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),CsvPreference.STANDARD_PREFERENCE);
//
//        String[] header = { "FirstName" , "LastName", "Username" };
//        csvWriter.write("invar");
//        csvWriter.writeHeader(header);
//
//        for (Operator car3 : listBooks) {
//            csvWriter.write(car3, header);
//        }
//
//        csvWriter.close();
    }

    @RequestMapping(value = "/admin/report/passengers/age", method = RequestMethod.GET, produces = "application/vnd.ms-excel;charset=UTF-8")
    public void passengersAgeReport(HttpServletResponse response) throws IOException {
        String fileName = "Passengers_Age_Report.xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        adminManager.passengersAgeReport(response);
    }

    @RequestMapping(value = "/admin/report/operators/age", method = RequestMethod.GET, produces = "application/vnd.ms-excel;charset=UTF-8")
    public void operatorsAgeReport(HttpServletResponse response) throws IOException {
        String fileName = "Operators_Age_Report.xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        adminManager.operatorsAgeReport(response);
    }

    @RequestMapping(value = "/admin/report/devices", method = RequestMethod.GET, produces = "application/vnd.ms-excel;charset=UTF-8")
    public void devicesReport(HttpServletResponse response) throws IOException {
        String fileName = "Devices_Report.xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        adminManager.OSReport(response);
    }

    @RequestMapping(value = "/admin/report/trips/{startDate}/{endDate}", method = RequestMethod.GET, produces = "application/vnd.ms-excel;charset=UTF-8")
    public void tripsReport(HttpServletResponse response,@PathVariable String startDate, @PathVariable String endDate) throws IOException {
        String fileName = "Trips_Report.xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date sDate = null;
        Date eDate = null;
        try {
            sDate = df.parse(startDate);
            eDate = df.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        adminManager.tripsReport(response,sDate,eDate);
    }

    @RequestMapping(value = "/admin/providers", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8",  produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> insertProvider(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        try {
            String name = jsonObjectRequest.getString("name");
            if(name.isEmpty()){
                return returnResponse(Status.BAD_DATA);
            }
            Status status = adminManager.insertProvider(name);
            return returnResponse(status);
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
    }

    @RequestMapping(value = "/admin/report/trips/cost", method = RequestMethod.GET, produces = "application/vnd.ms-excel;charset=UTF-8")
    public void costTripsReport(HttpServletResponse response) throws IOException {
        String fileName = "Trips_Cost_Report.xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        adminManager.costTripsReport(response);
    }


    @RequestMapping(value = "/admin/report/providers", method = RequestMethod.GET, produces = "application/vnd.ms-excel;charset=UTF-8")
    public void providersClaimReport(HttpServletResponse response) throws IOException {
        String fileName = "Providers_Claim_Report.xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        adminManager.providersClaimReport(response);
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
