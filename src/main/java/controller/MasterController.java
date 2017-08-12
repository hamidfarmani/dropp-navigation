package controller;

import model.entity.persistent.Operator;
import model.enums.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.MasterService;
import util.*;
import util.converter.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
@CrossOrigin
public class MasterController {

    private MasterService masterService;

    @Autowired
    public MasterController(MasterService masterService) {
        this.masterService = masterService;
    }

    @RequestMapping(value = "/master/operators", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> operatorRegister(@RequestBody String request, @RequestHeader(value = "Authorization") String auth) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String username,creatorUsername, password, PhoneNumber, cityStr, genderStr,firstname,lastname,workNumber,email;
        City city;
        Gender gender;
        Date birthDate;
        int year,month,day;
        Long providerID = null;
        try {
            HTTPAuthParser httpAuthParser = (HTTPAuthParser)IOCContainer.getBean("httpAuthParser");
            creatorUsername = httpAuthParser.returnUsername(auth);
            if(jsonObjectRequest.has("providerID")){
                providerID = jsonObjectRequest.getLong("providerID");
            }
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

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            birthDate = new Date();
            String dataStr = String.valueOf(year + "/" + month + "/" + day);
            birthDate = dateFormat.parse(dataStr);

            if (PhoneNumber.isEmpty()
                    || password.isEmpty()
                    || username.isEmpty()
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
        if(masterService.isPhoneNumberExist(PhoneNumber)!=null){
            return returnResponse(Status.PHONE_NUMBER_EXIST);
        }
        if (!masterService.isUsernameExist(username)) {
            Object object = masterService.operatorRegister(creatorUsername,firstname,lastname,birthDate,email,PhoneNumber,workNumber, username, password,gender,city,providerID);
            if (object == null) {
                return returnResponse(Status.UNKNOWN_ERROR);
            }
            if (object instanceof Status) {
                return returnResponse((Status) object);
            }
        }else{
            return returnResponse(Status.USERNAME_EXIST);
        }
        return returnResponse(Status.OK);
    }

    @RequestMapping(value = "/master/operators/{operatorID}", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> operatorUpdate(@RequestBody String request,@PathVariable String operatorID) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        Long id;
        String password = null, PhoneNumber = null, cityStr, genderStr,firstname = null,lastname = null,workNumber=null,email=null;
        City city=null;
        Gender gender=null;
        Date birthDate = null;
        int year,month,day;
        try {
            id= Long.valueOf(operatorID);
            if (jsonObjectRequest.has("firstName")){
                firstname = jsonObjectRequest.getString("firstName").trim();
            }
            if (jsonObjectRequest.has("lastName")) {
                lastname = jsonObjectRequest.getString("lastName").trim();
            }
            if (jsonObjectRequest.has("email")) {
                email = jsonObjectRequest.getString("email").trim();
            }
            if (jsonObjectRequest.has("phoneNumber")) {
                PhoneNumber = jsonObjectRequest.getString("phoneNumber").trim().replace(" ", "");
                if (!Validation.getInstance().validatePhoneNumber(PhoneNumber)) {
                    return returnResponse(Status.BAD_PHONE_NUMBER);
                }
            }
            if (jsonObjectRequest.has("workNumber")) {
                workNumber = jsonObjectRequest.getString("workNumber").trim().replace(" ", "");
            }
            if (jsonObjectRequest.has("password")) {
                password = jsonObjectRequest.getString("password").trim();
                if ((!Validation.getInstance().validatePassword(password)) || password.length() > 30) {
                    return returnResponse(Status.BAD_PASSWORD);
                }
            }
            if (jsonObjectRequest.has("birthDate")) {
                JSONObject birthD = jsonObjectRequest.getJSONObject("birthDate");
                year = Integer.valueOf(String.valueOf(birthD.get("year")));
                month = Integer.valueOf(String.valueOf(birthD.get("month")));
                day = Integer.valueOf(String.valueOf(birthD.get("day")));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                birthDate = new Date();
                String dataStr = String.valueOf(year + "/" + month + "/" + day);
                birthDate = dateFormat.parse(dataStr);
            }
            if (jsonObjectRequest.has("city")) {
                cityStr = jsonObjectRequest.getString("city").trim();
                CityConverter cityConverter = (CityConverter)IOCContainer.getBean("cityConverter");
                city = cityConverter.convertToEntityAttribute(cityStr);
            }
            if (jsonObjectRequest.has("gender")) {
                genderStr = jsonObjectRequest.getString("gender").trim();
                GenderConverter genderConverter = (GenderConverter)IOCContainer.getBean("genderConverter");
                gender = genderConverter.convertToEntityAttribute(genderStr.charAt(0));
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
        Operator op = null;
        if(PhoneNumber!=null) {
            op = masterService.isPhoneNumberExist(PhoneNumber);
        }
        if(PhoneNumber==null || op == null || op.getoId()==id) {
            Object object = masterService.operatorUpdate(id, firstname, lastname, birthDate, email, PhoneNumber, workNumber, password, gender, city);
            if (object == null) {
                return returnResponse(Status.UNKNOWN_ERROR);
            }
            if (object instanceof Status) {
                return returnResponse((Status) object);
            }else {
                return returnResponse(Status.UNKNOWN_ERROR);
            }
        }else{
            return returnResponse(Status.PHONE_NUMBER_EXIST);
        }
    }

    @RequestMapping(value = "/master/operators/{operatorID}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> operatorRemove(@PathVariable String operatorID) {
        Long id = null;
        if(operatorID.isEmpty()){
            return returnResponse(Status.BAD_JSON);
        }
        id = Long.valueOf(operatorID);
        return returnResponse(masterService.operatorRemove(id));
    }

    @RequestMapping(value = "/master/ticketSubjects", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> ticketSubjectRegister(@RequestBody String request, @RequestHeader(value = "Authorization") String auth) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String subject, parentID, roleStr;
        UserRole userRole;
        try {
            subject = jsonObjectRequest.getString("subject").trim();
            parentID = jsonObjectRequest.getString("parentID");
            roleStr = jsonObjectRequest.getString("role");
            UserRoleConverter userRoleConverter = (UserRoleConverter)IOCContainer.getBean("userRoleConverter");
            userRole = userRoleConverter.convertToEntityAttribute(roleStr.charAt(0));
            if (subject.isEmpty()) {
                return returnResponse(Status.BAD_DATA);
            }
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return returnResponse(Status.BAD_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
            return returnResponse(Status.BAD_JSON);
        }
        if (!masterService.isSubjectExist(subject,userRole)) {
            Status ticketSubjectRegisterStatus = masterService.ticketSubjectRegister(subject,parentID,userRole);
            Status reloadStatus = null;
            if(ticketSubjectRegisterStatus==Status.OK){
                Reloader r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(auth,'J');
                if(reloadStatus == Status.OK){
                    return returnResponse(Status.OK);
                }else{
                    return returnResponse(Status.RELOAD_NOT_OCCURRED);
                }
            }else{
                return returnResponse(ticketSubjectRegisterStatus);
            }
        }else {
            return returnResponse(Status.SUBJECT_EXIST);
        }
    }

    @RequestMapping(value = "/master/ticketSubjects", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> viewAllTicketSubjects() {
        Object tickets = masterService.viewAllTicketSubjects();
        if (tickets instanceof JSONObject) {
            return returnResponse(Status.OK,(JSONObject) tickets);
        }else{
            return returnResponse((Status)tickets);
        }
    }

    @RequestMapping(value = "/master/vouchers", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> voucherRegister(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        VoucherCodeTypeConverter cConverter = (VoucherCodeTypeConverter) IOCContainer.getBean("voucherCodeTypeConverter");
        VoucherCodeGenerationTypeConverter gConverter = (VoucherCodeGenerationTypeConverter) IOCContainer.getBean("voucherCodeGenerationTypeConverter");
        Date startDate, endDate = null;
        String code=null;
        try {
            int maxUse = jsonObjectRequest.getInt("maxUses");
            String description = jsonObjectRequest.getString("description");
            String SD = jsonObjectRequest.getString("startDate");
            String ED = jsonObjectRequest.getString("expireDate");
            String gtype = jsonObjectRequest.getString("generationType");
            String ctype = jsonObjectRequest.getString("voucherType");
            String value = jsonObjectRequest.getString("discountValue");

            VoucherCodeGenerationType generationType = gConverter.convertToEntityAttribute(gtype.charAt(0));
            VoucherCodeType codeType = cConverter.convertToEntityAttribute(ctype.charAt(0));
            if (generationType == VoucherCodeGenerationType.MANUAL) {
                code = jsonObjectRequest.getString("code");
            }
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                startDate = formatter.parse(SD);
                if (!ED.isEmpty()) {
                    endDate = formatter.parse(ED);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return returnResponse(Status.BAD_DATA);
            }
            if (generationType==VoucherCodeGenerationType.AUTOMATIC || !masterService.isVoucherCodeExist(code)){
                Object object = masterService.voucherRegister(maxUse, description, startDate, endDate, generationType, codeType, value, code);
                if (object instanceof JSONObject) {
                    return returnResponse(Status.OK, (JSONObject) object);
                } else {
                    return returnResponse((Status) object);
                }
            }else{
                return returnResponse(Status.VOUCHER_CODE_EXIST);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/master/vouchers/{voucherID}", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> voucherUpdate(@RequestBody String request,@PathVariable String voucherID) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        VoucherCodeTypeConverter cConverter = (VoucherCodeTypeConverter) IOCContainer.getBean("voucherCodeTypeConverter");
        Date startDate, endDate = null;
        try {
            Long id = Long.valueOf(voucherID);
            int maxUse = jsonObjectRequest.getInt("maxUses");
            String description = jsonObjectRequest.getString("description");
            String SD = jsonObjectRequest.getString("startDate");
            String ED = jsonObjectRequest.getString("expireDate");
            String ctype = jsonObjectRequest.getString("voucherType");
            String value = jsonObjectRequest.getString("discountValue");

            VoucherCodeType codeType = cConverter.convertToEntityAttribute(ctype.charAt(0));

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                startDate = formatter.parse(SD);
                if(!ED.isEmpty()) {
                    endDate = formatter.parse(ED);
                }
            }catch (ParseException e){
                e.printStackTrace();
                return returnResponse(Status.BAD_DATA);
            }
            Status status = masterService.voucherUpdate(id, maxUse,description,startDate,endDate,codeType,value);
            return returnResponse(status);
        } catch (JSONException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/master/banOperator", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> banOperator(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String username;
        try {
            username = jsonObjectRequest.getString("username");
            Status status = masterService.banOperator(username);

            return ResponseProvider.getInstance().getResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/master/unBanOperator", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> unBanOperator(@RequestBody String request) {
        JSONObject jsonObjectRequest = new JSONObject(request);
        String username;
        try {
            username = jsonObjectRequest.getString("username");
            Status status = masterService.unBanOperator(username);

            return ResponseProvider.getInstance().getResponse(status);
        } catch (JSONException e) {
            return ResponseProvider.getInstance().getResponse(Status.BAD_JSON);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseProvider.getInstance().getResponse(Status.BAD_DATA);
        }
    }

    @RequestMapping(value = "/master/operators", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> searchOperator(@RequestParam(value = "q") String q,@RequestParam(value = "count") String count,@RequestParam(value = "offset") String pageIndex, @RequestHeader(value = "Authorization") String auth) {
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
            HTTPAuthParser http = (HTTPAuthParser) IOCContainer.getBean("httpAuthParser");
            String operatorUsername = http.returnUsername(auth);
            Object  operators = masterService.searchOperators(q,c,p,operatorUsername);

            if(operators instanceof JSONObject){
                return returnResponse(Status.OK,(JSONObject) operators);
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
