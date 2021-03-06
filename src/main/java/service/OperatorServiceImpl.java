package service;

import com.mongodb.client.MongoCursor;
import model.da.MongoDBQueries;
import model.entity.persistent.*;
import model.entity.persistent.City;
import model.enums.*;
import org.apache.commons.io.FilenameUtils;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import security.model.AccountCredentials;
import util.EncoderUtil;
import util.Generator;
import util.IOCContainer;
import util.LocalEntityManagerFactory;
import util.converter.*;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service("operatorService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
@Transactional

public class OperatorServiceImpl implements OperatorService {

    public Object subscribeRegister(String firstName, String lastName, String phoneNumber, Address address, String city) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            JSONObject jsonObjectResponse = new JSONObject();
            entityManager.getTransaction().begin();
            SubscribeUser subscribeUser = (SubscribeUser) IOCContainer.getBean("subscribeUser");
            City c = (City) entityManager.createNamedQuery("city.by.id")
                    .setParameter("id",Long.valueOf(city))
                    .getSingleResult();
            Generator g = new Generator();
            String code = g.generateRandomCode(6);
            subscribeUser.setFirstName(firstName);
            subscribeUser.setLastName(lastName);
            subscribeUser.setSubscriptionCode(code);
            subscribeUser.setPhoneNumber(phoneNumber);
            address.setCity(c);
            subscribeUser.setAddress(address);
            entityManager.persist(subscribeUser);
            entityManager.getTransaction().commit();
            jsonObjectResponse.put("code",code);
            return jsonObjectResponse;

        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Object operatorLogin(String username, String password, String ip) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        Operator operator;
        String hashedPassword = EncoderUtil.getSHA512Hash(password).toLowerCase();
        try {
            try {
                entityManager.getTransaction().begin();
                operator = (Operator) entityManager.createNamedQuery("operator.findBy.usernameAndPassword")
                        .setParameter("username", username)
                        .setParameter("password", hashedPassword)
                        .setMaxResults(1)
                        .getSingleResult();

                if (operator.getAccountState() == AccountState.BANNED) {
                    return Status.USER_BANNED;
                }else if(operator.getAccountState() == AccountState.DEACTIVATE){
                    return Status.USER_DEACTIVATED;
                }else if(operator.getAccountState() != AccountState.VERIFIED){
                    return Status.USER_NOT_ACTIVATED;
                }

            } catch (NoResultException e) {
                entityManager.getTransaction().commit();
                return Status.NOT_FOUND;
            }
            OperatorLog operatorLog = (OperatorLog) IOCContainer.getBean("operatorLog");
            operatorLog.setOperator(operator);
            operatorLog.setIp(ip);
            operatorLog.setLogTimestamp(new Date());
            operatorLog.setLogType(LogType.LOGIN);
            entityManager.persist(operatorLog);
            entityManager.getTransaction().commit();
            UserRoleConverter userRoleConverter = (UserRoleConverter)IOCContainer.getBean("userRoleConverter");
            return new AccountCredentials(operator.getUsername(), String.valueOf(userRoleConverter.convertToDatabaseColumn(operator.getRole())));
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen())
                entityManager.close();
        }
    }

    public Status confirmDriver(String username,String providerID, String op) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        Long serviceProviderID;
        try {
            entityManager.getTransaction().begin();

            Driver driver = (Driver) entityManager.createNamedQuery("driver.searchExact.username")
                    .setParameter("username", username)
                    .getSingleResult();
            if(driver.getAccountState()==AccountState.DEACTIVATE){
                entityManager.getTransaction().rollback();
                return Status.USER_DEACTIVATED;
            }
            if(driver.getAccountState()==AccountState.BANNED){
                entityManager.getTransaction().rollback();
                return Status.USER_BANNED;
            }
            Operator operator = (Operator)entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username",op)
                    .getSingleResult();
            if(!providerID.isEmpty()){
                serviceProviderID = Long.valueOf(providerID);
                ServiceProvider serviceProvider = (ServiceProvider)entityManager.createNamedQuery("serviceProvider.findby.id")
                        .setParameter("id",serviceProviderID)
                        .getSingleResult();
                driver.setServiceProvider(serviceProvider);
            }

            driver.setOperator(operator);
            driver.setAccountState(AccountState.VERIFIED);
            entityManager.getTransaction().commit();
            return Status.OK;

        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public JSONObject viewNumberOfAllDrivers() {
        JSONObject jsonObjectResponse = new JSONObject();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Long driversCount = (Long) entityManager.createNamedQuery("driver.all.count").getSingleResult();
            jsonObjectResponse.put("numberAllDrivers", driversCount);
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
        }finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return jsonObjectResponse;
    }

    public JSONObject viewOnlineAllDrivers() {

        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray drivers = new JSONArray();
        ServiceTypeConverter serviceTypeConverter = (ServiceTypeConverter)IOCContainer.getBean("serviceTypeConverter");
        for (ServiceType serviceType : ServiceType.values()) {
            MongoCursor<Document> cursor = ((MongoDBQueries) IOCContainer.getBean("mongoDBQueries")).getOnlineDrivers(serviceType);
            while (cursor.hasNext()) {
                JSONObject driver = new JSONObject();
                Document cursorDoc = cursor.next();
                driver.put("username", cursorDoc.get("username"));
                driver.put("phoneNumber", cursorDoc.get("phoneNumber"));
                driver.put("rate", cursorDoc.get("rate"));
                driver.put("serviceType",serviceTypeConverter.convertToDatabaseColumn(serviceType));
                drivers.put(driver);
            }
        }
        jsonObjectResponse.put("onlines", drivers);

        return jsonObjectResponse;
    }

    public JSONObject viewNumberOfOnlineDrivers() {
        JSONObject jsonObjectResponse = new JSONObject();
        for (ServiceType serviceType : ServiceType.values()) {
            Long numbers = ((MongoDBQueries) IOCContainer.getBean("mongoDBQueries")).getNumberOfOnlineDriversCount(serviceType);
            jsonObjectResponse.put(serviceType.toString(), numbers);
        }
        return jsonObjectResponse;
    }

    public JSONObject viewLowRateDrivers() {
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray trips = new JSONArray();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            List<Object[]> lowRateDrivers = entityManager.createNamedQuery("trip.findBy.lowRate")
                    .setParameter("rate", 3.0).getResultList();
            for (Object[] obj : lowRateDrivers) {
                String username = String.valueOf(obj[0]);
                double avg = (double) obj[1];
                JSONObject object = new JSONObject();
                object.put("username", username);
                object.put("avg", avg);
                trips.put(object);

            }
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        jsonObjectResponse.put("Drivers", trips);
        return jsonObjectResponse;
    }

    public Object searchDriver(String q,int count,int pageIndex) {
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray drivers = new JSONArray();
        GenderConverter genderConverter = (GenderConverter)IOCContainer.getBean("genderConverter");
        EntityManager  entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            List<Driver> driversList = null;
            entityManager.getTransaction().begin();
            if(count==-1){
                driversList = entityManager.createNamedQuery("driver.searchLike")
                        .setParameter("input", q + "%")
                        .getResultList();
            } else {
                driversList = entityManager.createNamedQuery("driver.searchLike")
                        .setParameter("input", q + "%")
                        .setFirstResult(count * pageIndex)
                        .setMaxResults(count)
                        .getResultList();
            }
            for(int i=0;i<driversList.size();i++) {
                JSONObject driver = new JSONObject();
                JSONObject address = new JSONObject();
                JSONObject vehicle = new JSONObject();
                JSONObject device = new JSONObject();
                JSONObject operator = new JSONObject();
                Driver d = driversList.get(i);
                driver.put("firstName", d.getFirstName());
                driver.put("lastName", d.getLastName());
                driver.put("nationalNumber", d.getNationalNumber());
                driver.put("username", d.getUsername());
                driver.put("birthDate", d.getBirthDate());
                driver.put("email", d.getEmail());

                driver.put("gender", genderConverter.convertToDatabaseColumn(d.getGender()));
                driver.put("serviceType", d.getServiceType());
                driver.put("credit", d.getCredit());
                driver.put("phoneNumber",d.getPhoneNumber());
                driver.put("cardNumber",d.getBankCardNumber());
                driver.put("accountNumber",d.getBankAccountNumber());
                if (d.getAccountState() != null) {
                    driver.put("stateCode", d.getAccountState().getStateCode());
                }

                if (d.getVehicle() != null) {
                    vehicle.put("buildDate", d.getVehicle().getBuildDate());
                    vehicle.put("color", d.getVehicle().getColor());
                    vehicle.put("licencePlate", d.getVehicle().getLicencePlate());
                    if(d.getVehicle().getCar()!=null) {
                        if(d.getVehicle().getCar().getCarManufacture()!=null) {
                            vehicle.put("car", d.getVehicle().getCar().getCarManufacture().getName() + " - " + d.getVehicle().getCar().getName());
                        }else{
                            vehicle.put("car", d.getVehicle().getCar().getName());
                        }
                    }
                    driver.put("vehicle", vehicle);
                }

                if (d.getAddress() != null) {
                    if(d.getAddress().getCity()!=null){
                        address.put("city", d.getAddress().getCity().getName());
                        if(d.getAddress().getCity().getState()!=null){
                            address.put("state", d.getAddress().getCity().getState().getName());
                        }
                    }
                    address.put("line1", d.getAddress().getLine1());
                    address.put("line2", d.getAddress().getLine2());
                    address.put("postalCode", d.getAddress().getPostalCode());
                    driver.put("address", address);
                }

                if (d.getDevice() != null) {
                    device.put("OS", d.getDevice().getOS());
                    device.put("OSVersion", d.getDevice().getOSVersion());
                    driver.put("device", device);
                }

                if (d.getOperator() != null) {
                    operator.put("firstname", d.getOperator().getFirstName());
                    operator.put("lastname", d.getOperator().getLastName());
                    operator.put("username", d.getOperator().getUsername());
                    operator.put("phoneNumber", d.getOperator().getPhoneNumber());
                    driver.put("operator", operator);
                }

                driver.put("registrationDate", d.getRegistrationTimestamp());
                drivers.put(driver);
            }
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        jsonObjectResponse.put("Drivers", drivers);
        return jsonObjectResponse;
    }

    public Object searchPassenger(String q,int count, int pageIndex) {
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray passengers = new JSONArray();
        GenderConverter genderConverter = (GenderConverter)IOCContainer.getBean("genderConverter");
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            List<Passenger> passengersList = null;

            entityManager.getTransaction().begin();
            if(count==-1){
                passengersList = entityManager.createNamedQuery("passenger.searchLike")
                        .setParameter("input", q + "%")
                        .getResultList();
            }else {
                passengersList = entityManager.createNamedQuery("passenger.searchLike")
                        .setParameter("input", q + "%")
                        .setFirstResult(count * pageIndex)
                        .setMaxResults(count)
                        .getResultList();
            }
            for(Passenger p: passengersList) {
                JSONObject passenger = new JSONObject();
                JSONObject passengerInfo = new JSONObject();
                JSONObject device = new JSONObject();

                passenger.put("username", p.getUsername());
                passenger.put("credit", p.getCredit());
                passenger.put("phoneNumber", p.getPhoneNumber());
                passenger.put("point", p.getPoint());
                passenger.put("star", p.getStar());
                if(p.getRecommender()!=null) {
                    passenger.put("recommender", p.getRecommender().getUsername());
                }

                if (p.getPassengerInfo() != null) {
                    passengerInfo.put("firstName", p.getPassengerInfo().getFirstName());
                    passengerInfo.put("lastName", p.getPassengerInfo().getLastName());
                    passengerInfo.put("birthDate", p.getPassengerInfo().getBirthDate());
                    passengerInfo.put("email", p.getPassengerInfo().getEmail());
                    passengerInfo.put("gender", genderConverter.convertToDatabaseColumn(p.getPassengerInfo().getGender()));
                    passenger.put("passengerInfo", passengerInfo);
                }

                if (p.getAccountState() != null) {
                    passenger.put("stateCode", p.getAccountState().getStateCode());
                }

                if (p.getDevice() != null) {
                    device.put("OS", p.getDevice().getOS());
                    device.put("OSVersion", p.getDevice().getOSVersion());
                    passenger.put("device", device);
                }

                passenger.put("registrationDate", p.getRegistrationTimestamp());
                passengers.put(passenger);
            }
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        jsonObjectResponse.put("Passengers", passengers);
        return jsonObjectResponse;
    }

    public Status organizationRegister(String name, String username, String password, String email, String phoneNumber, String workNumber, int employeeCount) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {

            entityManager.getTransaction().begin();
            Organization organization = (Organization) IOCContainer.getBean("organization");
            organization.setOrgName(name);
            organization.setUsername(username);
            organization.setPassword(EncoderUtil.getSHA512Hash(password).toLowerCase());
            organization.setEmail(email);
            organization.setPhoneNumber(phoneNumber);
            organization.setWorkNumber(workNumber);
            organization.setRegistrationTimestamp(new Date());
            organization.setCredit(0);
            organization.setAccountState(AccountState.READY_TO_VERIFY);
            if (employeeCount < 10) {
                organization.setEmpCount(EmployeeCount.LESS_THAN_10);
            } else if (employeeCount < 20) {
                organization.setEmpCount(EmployeeCount.LESS_THAN_20);
            } else if (employeeCount < 50) {
                organization.setEmpCount(EmployeeCount.LESS_THAN_50);
            } else if (employeeCount < 100) {
                organization.setEmpCount(EmployeeCount.LESS_THAN_100);
            } else if (employeeCount < 500) {
                organization.setEmpCount(EmployeeCount.LESS_THAN_500);
            } else if (employeeCount < 1000) {
                organization.setEmpCount(EmployeeCount.LESS_THAN_1000);
            } else {
                organization.setEmpCount(EmployeeCount.MORE_THAN_1000);
            }
            entityManager.persist(organization);
            entityManager.getTransaction().commit();
            return Status.OK;

        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Status banDriver(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Driver driver = (Driver) entityManager.createNamedQuery("driver.searchExact.username")
                    .setParameter("username", username)
                    .getSingleResult();
            driver.setAccountState(AccountState.BANNED);
            entityManager.getTransaction().commit();
            return Status.OK;

        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Status unBanDriver(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Driver driver = (Driver) entityManager.createNamedQuery("driver.searchExact.username")
                    .setParameter("username", username)
                    .getSingleResult();
            driver.setAccountState(AccountState.VERIFIED);
            entityManager.getTransaction().commit();
            return Status.OK;

        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Status banPassenger(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Passenger passenger = (Passenger) entityManager.createNamedQuery("passenger.exact.username")
                    .setParameter("username", username)
                    .getSingleResult();
            passenger.setAccountState(AccountState.BANNED);
            entityManager.getTransaction().commit();
            return Status.OK;

        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Status unBanPassenger(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Passenger passenger = (Passenger) entityManager.createNamedQuery("passenger.exact.username")
                    .setParameter("username", username)
                    .getSingleResult();
            passenger.setAccountState(AccountState.ACTIVATED);
            entityManager.getTransaction().commit();
            return Status.OK;

        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Object viewBanDrivers() {
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray banDrivers = new JSONArray();
        List<Driver> drivers;
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {

            entityManager.getTransaction().begin();
            drivers = entityManager.createNamedQuery("driver.findBy.accountState")
                    .setParameter("accountState", AccountState.BANNED)
                    .getResultList();
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        for (int i = 0; i < drivers.size(); i++) {
            JSONObject driver = new JSONObject();
            driver.put("firstName", drivers.get(i).getFirstName());
            driver.put("lastName", drivers.get(i).getLastName());
            driver.put("phoneNumber", drivers.get(i).getPhoneNumber());
            driver.put("username", drivers.get(i).getUsername());
            driver.put("credit", drivers.get(i).getCredit());
            banDrivers.put(driver);
        }
        jsonObjectResponse.put("banDrivers", banDrivers);
        return jsonObjectResponse;
    }

    public Object viewBanPassengers() {
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray banPassengers = new JSONArray();
        List<Passenger> passengers;
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            passengers = entityManager.createNamedQuery("passenger.findBy.accountState")
                    .setParameter("accountState", AccountState.BANNED)
                    .getResultList();
            entityManager.getTransaction().commit();

            for (int i = 0; i < passengers.size(); i++) {
                JSONObject passenger = new JSONObject();
//            passenger.put("firstName", passengers.get(i).getPassengerInfo().getFirstName());
//            passenger.put("lastName", passengers.get(i).getPassengerInfo().getLastName());
                passenger.put("phoneNumber", passengers.get(i).getPhoneNumber());
                passenger.put("username", passengers.get(i).getUsername());
                passenger.put("credit", passengers.get(i).getCredit());
                banPassengers.put(passenger);
            }
            jsonObjectResponse.put("banPassengers", banPassengers);
            return jsonObjectResponse;
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            entityManager.close();
        }
    }

    public Object searchOrganization(String q,int count, int pageIndex){
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray organizations = new JSONArray();

        try {
            entityManager.getTransaction().begin();
            List<Organization> org;
            if(count==-1){
                org = entityManager.createNamedQuery("organization.like")
                        .setParameter("input", q + "%")
                        .getResultList();
            }else {
                org = entityManager.createNamedQuery("organization.like")
                        .setParameter("input", q + "%")
                        .setFirstResult(count * pageIndex)
                        .setMaxResults(count)
                        .getResultList();
            }
            for(int i=0;i<org.size();i++) {
                JSONObject address = new JSONObject();
                JSONObject o = new JSONObject();
                Organization organ = org.get(i);
                o.put("name", organ.getOrgName());
                o.put("phoneNumber", organ.getPhoneNumber());
                o.put("workNumber", organ.getWorkNumber());
                o.put("email", organ.getEmail());
                o.put("empCount", organ.getEmpCount());
                o.put("accountState", organ.getAccountState());
                o.put("description", organ.getDescription());
                o.put("registrarFirstName",organ.getRegistrarFirstName());
                o.put("registrarLastName",organ.getRegistrarLastName());
                o.put("registrarRole",organ.getRegistrarRole());
                o.put("username", organ.getUsername());
                if(organ.getAddress()!=null){
                    Address add = organ.getAddress();
                    address.put("state",add.getCity().getState().getName());
                    address.put("city",add.getCity());
                    address.put("line1",add.getLine1());
                    address.put("line2",add.getLine2());
                    address.put("postalCode",add.getPostalCode());
                }
                o.put("address", address);

                organizations.put(o);
            }
            jsonObjectResponse.put("organizations" , organizations);

            entityManager.getTransaction().commit();
        }catch (NoResultException e) {
            return Status.NOT_FOUND;
        }catch (JSONException e){
            return Status.BAD_JSON;
        }catch(Exception e){
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        }finally{
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }
            if(entityManager.isOpen()){
                entityManager.close();
            }
        }

        return jsonObjectResponse;
    }

    public Object viewOrganization(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        try {
            entityManager.getTransaction().begin();
            List<Organization> org = entityManager.createNamedQuery("organization.findBy.username")
                    .setParameter("username", "%"+ username + "%").getResultList();
            JSONArray organizations = new JSONArray();
            for(int i = 0 ; i <org.size();i++) {
                JSONObject organization = new JSONObject();
                organization.put("name", org.get(i).getOrgName());
                organization.put("address", org.get(i).getAddress());
                organization.put("phoneNumber", org.get(i).getPhoneNumber());
                organization.put("workNumber", org.get(i).getWorkNumber());
                organization.put("email", org.get(i).getEmail());
                organization.put("empCount", org.get(i).getEmpCount());
                organization.put("accountState", org.get(i).getAccountState());
                organization.put("description", org.get(i).getDescription());
                organizations.put(organization);
            }
            jsonObjectResponse.put("organizations", organizations);
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (JSONException e) {
            return Status.BAD_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return jsonObjectResponse;
    }

    public Status confirmOrganization(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Organization org = (Organization) entityManager.createNamedQuery("organization.findBy.username")
                    .setParameter("username", username).getSingleResult();
            org.setAccountState(AccountState.VERIFIED);
            entityManager.getTransaction().commit();
            return Status.OK;
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (JSONException e) {
            return Status.BAD_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Status removeOrganization(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Organization org = (Organization) entityManager.createNamedQuery("organization.findBy.username")
                    .setParameter("username", username).getSingleResult();
            entityManager.remove(org);
            entityManager.getTransaction().commit();
            return Status.OK;
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (JSONException e) {
            return Status.BAD_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Object viewDriverCredit(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray drivers = new JSONArray();
        try {
            entityManager.getTransaction().begin();
            List<Driver> driversList = entityManager.createNamedQuery("driver.searchLike.username")
                    .setParameter("username", username + "%")
                    .getResultList();
            for(int i=0;i<driversList.size();i++) {
                JSONObject driver = new JSONObject();
                driver.put("credit", driversList.get(i).getCredit());
                driver.put("username",driversList.get(i).getUsername());
                driver.put("phoneNumber",driversList.get(i).getPhoneNumber());
                driver.put("accountNumber",driversList.get(i).getBankAccountNumber());
                driver.put("cardNumber",driversList.get(i).getBankCardNumber());
                drivers.put(driver);
            }
            entityManager.getTransaction().commit();
            jsonObjectResponse.put("drivers", drivers);
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (JSONException e) {
            return Status.BAD_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return jsonObjectResponse;
    }

    public Object viewAllDriverCredit() {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray drivers = new JSONArray();
        try {
            entityManager.getTransaction().begin();
            List<Driver> d = entityManager.createNamedQuery("driver.all")
                    .getResultList();
            for (int i = 0; i < d.size(); i++) {
                JSONObject driver = new JSONObject();
                driver.put("credit", d.get(i).getCredit());
                driver.put("username", d.get(i).getUsername());
                driver.put("phoneNumber", d.get(i).getPhoneNumber());
                driver.put("accountNumber",d.get(i).getBankAccountNumber());
                driver.put("cardNumber",d.get(i).getBankCardNumber());
                drivers.put(driver);
            }
            jsonObjectResponse.put("driversCredit", drivers);

            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (JSONException e) {
            return Status.BAD_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return jsonObjectResponse;
    }

    public Object viewPassengersTicket(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();

        JSONArray driversTicket = new JSONArray();
        try {
            entityManager.getTransaction().begin();
            List<Driver> driversList = entityManager.createNamedQuery("driver.searchLike.username")
                    .setParameter("username", username + "%")
                    .getResultList();
            for(int i=0;i<driversList.size();i++) {
                JSONArray drivers = new JSONArray();
                JSONObject driverJson = new JSONObject();
                JSONArray ticketsJsonArray = new JSONArray();
                Driver d = driversList.get(i);
                List<Ticket> ticketsList = entityManager.createNamedQuery("ticket.findBy.driverID")
                        .setParameter("driverID", d.getdId())
                        .getResultList();
                if(ticketsList.size()>0) {
                    for (Ticket t : ticketsList) {
                        JSONObject ticketJson = new JSONObject();
                        driverJson.put("firstName", d.getFirstName());
                        driverJson.put("lastName", d.getLastName());
                        driverJson.put("username", d.getUsername());
                        driverJson.put("phoneNumber", d.getPhoneNumber());
                        ticketJson.put("subject", t.getTicketSubject().getSubject());
                        ticketJson.put("description", t.getDescription());
                        ticketsJsonArray.put(ticketJson);
                    }
                    drivers.put(driverJson);
                    drivers.put(ticketsJsonArray);
                    driversTicket.put(drivers);
                }
            }
            jsonObjectResponse.put("driversTickets", driversTicket);

            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (JSONException e) {
            return Status.BAD_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return jsonObjectResponse;
    }

    public Status sendMessage(String username, String message, String operatorUsername) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Message m = (Message) IOCContainer.getBean("message");
            Driver d = (Driver) entityManager.createNamedQuery("driver.searchExact.username")
                    .setParameter("username", username).getSingleResult();
            Operator o = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", operatorUsername).getSingleResult();
            m.setMessageText(message);
            m.setTimestamp(new Date());
            m.setDriver(d);
            m.setOperator(o);
            m.setUserRole(UserRole.OPERATOR);
            entityManager.persist(m);
            entityManager.getTransaction().commit();
            return Status.OK;
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (JSONException e) {
            return Status.BAD_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Status resolveTicket(Long ticketID){
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Ticket t = (Ticket) entityManager.createNamedQuery("ticket.findBy.id")
                    .setParameter("id", ticketID)
                    .getSingleResult();
            t.setTicketState(TicketState.RESOLVED);
            entityManager.getTransaction().commit();
            return Status.OK;
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (JSONException e) {
            return Status.BAD_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Object viewTickets() {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray allTickets = new JSONArray();
        try {
            entityManager.getTransaction().begin();
            List<Ticket> t = entityManager.createNamedQuery("ticket.findBy.Unresolved")
                    .setParameter("state", TicketState.UNRESOLVED).getResultList();

            for (int i = 0; i < t.size(); i++) {
                Ticket ticketObject = t.get(i);
                JSONObject ticket = new JSONObject();
                ticket.put("id", ticketObject.getId());
                if(ticketObject.getDriver()!=null) {
                    ticket.put("driverUsername", ticketObject.getDriver().getUsername());
                }
                if(ticketObject.getPassenger()!=null) {
                    ticket.put("passengerUsername", ticketObject.getPassenger().getUsername());
                }
                if(ticketObject.getTicketSubject() != null) {
                    ticket.put("subject", ticketObject.getTicketSubject().getSubject());
                }
                ticket.put("description", ticketObject.getDescription());
                allTickets.put(ticket);
            }

            jsonObjectResponse.put("All Unresolved Tickets", allTickets);

            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (JSONException e) {
            return Status.BAD_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return jsonObjectResponse;
    }

    public boolean isSubscribePhoneNumberExist(String phoneNumber) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            return !entityManager.createNamedQuery("subscribe.phoneNumber.exist")
                    .setParameter("phoneNumber", phoneNumber)
                    .setMaxResults(1)
                    .getResultList()
                    .isEmpty();
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public Object searchSubscribe(String q,int count,int pageIndex) {
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray subscribeUsers = new JSONArray();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            List<SubscribeUser> subscribesList = null;
            entityManager.getTransaction().begin();
            if(count==-1){
                subscribesList = entityManager.createNamedQuery("subscribe.searchLike")
                        .setParameter("input", q + "%")
                        .getResultList();
            } else {
                subscribesList = entityManager.createNamedQuery("subscribe.searchLike")
                        .setParameter("input", q + "%")
                        .setFirstResult(count * pageIndex)
                        .setMaxResults(count)
                        .getResultList();
            }
            for(int i=0;i<subscribesList.size();i++) {
                JSONObject subscribeJson = new JSONObject();
                JSONObject address = new JSONObject();
                SubscribeUser u = subscribesList.get(i);
                subscribeJson.put("firstName", u.getFirstName());
                subscribeJson.put("lastName", u.getLastName());
                subscribeJson.put("code", u.getSubscriptionCode());
                subscribeJson.put("phoneNumber",u.getPhoneNumber());
                if (u.getAddress() != null) {
                    address.put("state", u.getAddress().getCity().getState().getName());
                    address.put("city", u.getAddress().getCity().getName());
                    address.put("line1", u.getAddress().getLine1());
                    address.put("line2", u.getAddress().getLine2());
                    address.put("postalCode", u.getAddress().getPostalCode());
                    subscribeJson.put("address", address);
                }

                subscribeUsers.put(subscribeJson);
            }
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        jsonObjectResponse.put("SubscribeUsers", subscribeUsers);
        return jsonObjectResponse;
    }

    public Object viewOnlineTrips(){

        JSONObject jsonObjectResponse = new JSONObject();
        MongoCursor<Document> cursor = ((MongoDBQueries) IOCContainer.getBean("mongoDBQueries")).getOnlineTrips();
        JSONArray trips = new JSONArray();
        while (cursor.hasNext()) {
            Document cursorDoc = cursor.next();

            Document passengerDoc = (Document) cursorDoc.get("passenger");
            Document driverDoc = (Document) cursorDoc.get("driver");
            Document vehicleDoc = (Document) cursorDoc.get("vehicle");
            Document locDoc = (Document) cursorDoc.get("loc");
            Document tripInfoDoc = (Document) cursorDoc.get("loc");
            Document subscribeUserDoc = (Document) cursorDoc.get("subUser");
            Document deliveryInfoDoc = (Document) cursorDoc.get("deliveryInfo");

            JSONObject vehicle = new JSONObject();
            JSONObject tripInfo = new JSONObject();
            JSONObject deliveryInfo = new JSONObject();

            if(driverDoc!=null) {
                tripInfo.put("driverUsername", driverDoc.get("username"));
            }
            if(vehicleDoc!=null) {
                vehicle.put("color", vehicleDoc.get("color"));
                vehicle.put("name", vehicleDoc.get("name"));
                vehicle.put("manufacture", vehicleDoc.get("manufacture"));

                tripInfo.put("vehicle", vehicle);
            }
            if(locDoc!=null) {
                tripInfo.put("originAddress", locDoc.get("originAddress"));
            }


            tripInfo.put("id", tripInfoDoc.get("id"));
            tripInfo.put("ETA", tripInfoDoc.get("ETA"));
            tripInfo.put("UUID", tripInfoDoc.get("UUID"));
            tripInfo.put("cashPayment", tripInfoDoc.get("cashPayment"));
            tripInfo.put("cost", tripInfoDoc.get("cost"));
            tripInfo.put("creditPayment", tripInfoDoc.get("creditPayment"));
            tripInfo.put("distance", tripInfoDoc.get("distance"));
            tripInfo.put("isOneWay", tripInfoDoc.get("isOneWay"));
            tripInfo.put("originAddress", tripInfoDoc.get("originAddress"));
            tripInfo.put("paymentMethod", tripInfoDoc.get("paymentMethod"));
            tripInfo.put("serviceType", tripInfoDoc.get("serviceType"));
            tripInfo.put("startDate", tripInfoDoc.get("startDate"));
            tripInfo.put("waitingTime", tripInfoDoc.get("waitingTime"));
            tripInfo.put("city", tripInfoDoc.get("city"));


            if(passengerDoc!=null) {
                tripInfo.put("passengerUsername", passengerDoc.get("username"));
            }
            if(subscribeUserDoc!=null) {
                tripInfo.put("subscriptionCode", subscribeUserDoc.get("subscriptionCode"));
            }
            if(deliveryInfoDoc!=null) {
                deliveryInfo.put("id", deliveryInfoDoc.get("id"));
                deliveryInfo.put("desc", deliveryInfoDoc.get("desc"));
                deliveryInfo.put("destInfo", deliveryInfoDoc.get("destInfo"));
                deliveryInfo.put("receiverFName", deliveryInfoDoc.get("receiverFName"));
                deliveryInfo.put("receiverLName", deliveryInfoDoc.get("receiverLName"));

                tripInfo.append("deliveryInfo", deliveryInfo);
            }
            if(cursorDoc.get("voucherId")!=null) {
                tripInfo.append("voucherId", cursorDoc.get("voucherId"));
                tripInfo.append("voucherCode", cursorDoc.get("voucherCode"));
            }
            JSONArray destinations = new JSONArray();

            List<Document> destinationList = (List<Document>) cursorDoc.get("destinations");
            for (Document destination : destinationList) {
                JSONObject d = new JSONObject();
                d.put("destId", destination.get("destId"));
                d.put("seq", destination.get("seq"));
                d.put("lng", destination.get("lng"));
                d.put("lat", destination.get("lat"));
                d.put("address", destination.get("address"));
                destinations.put(d);
            }
            tripInfo.put("destinations", destinations);
            trips.put(tripInfo);

        }
        jsonObjectResponse.put("onlineTrips", trips);

        return jsonObjectResponse;
    }

    public Object viewNumberOfPassengers() {
        JSONObject jsonObjectResponse = new JSONObject();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Long passengerCount = (Long) entityManager.createNamedQuery("passenger.all.count")
                    .getSingleResult();
            jsonObjectResponse.put("numberOfAllPassengers", passengerCount);
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
        }finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return jsonObjectResponse;
    }

    public Object viewNumberOfNewPassengers() {
        JSONObject jsonObjectResponse = new JSONObject();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            Date monthAgo = cal.getTime();
            Long passengerCount = (Long) entityManager.createNamedQuery("passenger.new.count")
                    .setParameter("date",monthAgo)
                    .getSingleResult();
            jsonObjectResponse.put("numberOfNewPassengers", passengerCount);
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {

        }finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return jsonObjectResponse;
    }

    public Object viewNumberOfOnlineTrips(){
        JSONObject jsonObjectResponse = new JSONObject();
        Long numbers = ((MongoDBQueries) IOCContainer.getBean("mongoDBQueries")).getNumberOfOnlineTripsCount();
        jsonObjectResponse.put("numberOfOnlineTrips", numbers);
        return jsonObjectResponse;
    }

    public Object viewNumberOfTrips(){
        JSONObject jsonObjectResponse = new JSONObject();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Long allTripsCount = (Long) entityManager.createNamedQuery("trip.today.count")
                    .getSingleResult();
            jsonObjectResponse.put("numberOfTodaysTrips", allTripsCount);
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {

        }finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return jsonObjectResponse;
    }

    public Object viewNumberOfOrganization(){
        JSONObject jsonObjectResponse = new JSONObject();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Long allOrganizationCount = (Long) entityManager.createNamedQuery("organization.all.count")
                    .getSingleResult();
            jsonObjectResponse.put("numberOfAllOrganizations", allOrganizationCount);
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {

        }finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return jsonObjectResponse;
    }

    public Object viewNumberOfNewOrganization(){
        JSONObject jsonObjectResponse = new JSONObject();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            Date monthAgo = cal.getTime();
            Long newOrganizationCount = (Long) entityManager.createNamedQuery("organization.new.count")
                    .setParameter("date",monthAgo)
                    .getSingleResult();
            jsonObjectResponse.put("numberOfNewOrganizations", newOrganizationCount);
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {

        }finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return jsonObjectResponse;
    }

    public Status paymentRequestRegister(String driverUsername){
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Driver driver = (Driver) entityManager.createNamedQuery("driver.searchExact.username")
                    .setParameter("username", driverUsername)
                    .getSingleResult();
            entityManager.getTransaction().commit();
            return Status.OK;

        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Object viewAllStates(){
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray statesJSONArray = new JSONArray();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            List<State> stateList = entityManager.createNamedQuery("state.all")
                    .getResultList();
            for (State state : stateList) {
                JSONObject StateJSON = new JSONObject();
                StateJSON.put("id", state.getId());
                StateJSON.put("name", state.getName());
                statesJSONArray.put(StateJSON);
            }
            jsonObjectResponse.put("allStates", statesJSONArray);
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {

        }finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return jsonObjectResponse;
    }

    public Object viewCityByStateID(Long stateID){
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray cityJSONArray = new JSONArray();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            List<City> cityList = entityManager.createNamedQuery("city.by.stateID")
                    .setParameter("stateID",stateID)
                    .getResultList();
            for (City city : cityList) {
                JSONObject cityJSON = new JSONObject();
                cityJSON.put("id", city.getId());
                cityJSON.put("name", city.getName());
                cityJSONArray.put(cityJSON);
            }
            jsonObjectResponse.put("cities", cityJSONArray);
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {

        }finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return jsonObjectResponse;
    }

    public Object viewAllVouchers(){
        JSONObject jsonObjectResponse = new JSONObject();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            List<VoucherCode> vouchersList =  entityManager.createNamedQuery("voucherCode.all")
                    .getResultList();
            JSONArray vouchersJsonArray = new JSONArray();
            VoucherCodeTypeConverter voucherCodeTypeConverter = (VoucherCodeTypeConverter) IOCContainer.getBean("voucherCodeTypeConverter");
            VoucherCodeGenerationTypeConverter voucherCodeGenerationTypeConverter = (VoucherCodeGenerationTypeConverter) IOCContainer.getBean("voucherCodeGenerationTypeConverter");
            for(VoucherCode voucherObject : vouchersList){
                JSONObject voucherJSON = new JSONObject();
                voucherJSON.put("id", voucherObject.getId());
                voucherJSON.put("code", voucherObject.getCode());
                voucherJSON.put("description", voucherObject.getDescription());
                voucherJSON.put("discountValue",voucherObject.getDiscountValue());
                voucherJSON.put("maxUses",voucherObject.getMaxUses());
                voucherJSON.put("used",voucherObject.getUses());
                voucherJSON.put("expireDate",voucherObject.getExpireDate());
                voucherJSON.put("creationDate",voucherObject.getCreationDate());
                voucherJSON.put("startDate",voucherObject.getStartDate());
                voucherJSON.put("voucherType",voucherCodeTypeConverter.convertToDatabaseColumn(voucherObject.getType()));
                voucherJSON.put("generationType",voucherCodeGenerationTypeConverter.convertToDatabaseColumn(voucherObject.getGenerationType()));
                if(voucherObject.getOperator()!=null) {
                    voucherJSON.put("creatorUsername", voucherObject.getOperator().getUsername());
                }
                vouchersJsonArray.put(voucherJSON);
            }
            jsonObjectResponse.put("vouchers", vouchersJsonArray);
            entityManager.getTransaction().commit();
            return jsonObjectResponse;
        }catch(NoResultException e){
            return Status.NOT_FOUND;
        }catch (RollbackException e) {
            entityManager.getTransaction().rollback();
            return Status.BAD_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public File downloadFile(String driverUsername,FileTypeState fileTypeState) {
        File file = new File("C:\\TS_Data\\drivers\\" + driverUsername + "\\"+fileTypeState+".jpg");
        return file;
    }

    public Status uploadFile(MultipartFile file, String driverUsername,FileTypeState fileTypeState) {
        try {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
            if (!(extension.equals("png") || extension.equals("jpg"))) {
                return Status.BAD_DATA;
            }
            byte[] bytes = file.getBytes();
            if (!new File("C:\\TS_Data").exists()) {
                new File("C:\\TS_Data").mkdir();
            }
            if (!new File("C:\\TS_Data\\drivers").exists()) {
                new File("C:\\TS_Data\\drivers").mkdir();
            }
            File dir = new File("C:\\TS_Data\\drivers\\" + driverUsername);
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdir();
            }
            BufferedOutputStream stream =
                    new BufferedOutputStream((new FileOutputStream(new File("C:\\TS_Data\\drivers\\"+driverUsername+"\\" + fileTypeState + ".jpg"), false)));
            stream.write(bytes);
            stream.close();
            return Status.OK;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        } catch (IOException e) {
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        }
    }

    public Object searchTrip(String q,int count,int pageIndex) {
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray trips = new JSONArray();
        GenderConverter genderConverter = (GenderConverter) IOCContainer.getBean("genderConverter");
        List<Trip> tripList = null;
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {

            entityManager.getTransaction().begin();
            if (count == -1) {
                tripList = entityManager.createNamedQuery("trip.searchLike")
                        .setParameter("input", q + "%")
                        .getResultList();
            } else {
                tripList = entityManager.createNamedQuery("trip.searchLike")
                        .setParameter("input", q + "%")
                        .setFirstResult(count * pageIndex)
                        .setMaxResults(count)
                        .getResultList();
            }
            for (Trip tripObject : tripList) {
                JSONObject vehicle = new JSONObject();
                JSONObject loc = new JSONObject();
                JSONObject tripInfo = new JSONObject();
                JSONObject subUser = new JSONObject();
                JSONObject deliveryInfo = new JSONObject();

                if (tripObject.getDriver() != null) {
                    tripInfo.put("driverUsername", tripObject.getDriver().getUsername());
                }
                if (tripObject.getVehicle() != null) {
                    vehicle.put("color", tripObject.getVehicle().getColor());
                    vehicle.put("name", tripObject.getVehicle().getCar().getName());
                    vehicle.put("manufacture", tripObject.getVehicle().getCar().getCarManufacture().getName());

                    tripInfo.put("vehicle", vehicle);
                }
                if (tripObject.getOrigin() != null) {
                    loc.put("originLng", tripObject.getOrigin().getLongitude());
                    loc.put("originLat", tripObject.getOrigin().getLatitude());

                    tripInfo.put("loc", loc);
                }


                tripInfo.put("id", tripObject.getId());
                tripInfo.put("ETA", tripObject.getInfo().getETA());
                tripInfo.put("UUID", tripObject.getUUID());
                tripInfo.put("cashPayment", tripObject.getCashPayment());
                tripInfo.put("cost", tripObject.getCost());
                tripInfo.put("creditPayment", tripObject.getCreditPayment());

//              tripInfo.put("paymentMethod", tripObject.getPaymentMethodDBValue());
//              tripInfo.put("city", tripObject.getCityDBValue());
                tripInfo.put("serviceType", tripObject.getServiceTypeDBValue());
                if(tripObject.getInfo()!=null) {
                    tripInfo.put("distance", tripObject.getInfo().getDistance());
                    tripInfo.put("originAddress", tripObject.getInfo().getOriginAddress());
                    tripInfo.put("startDate", tripObject.getInfo().getStartDate());
                    tripInfo.put("waitingTime", tripObject.getInfo().getWaitingTime());
                    tripInfo.put("rate", tripObject.getInfo().getRate());
                    tripInfo.put("isOneWay", tripObject.getInfo().isOneWay());

                }


                if (tripObject.getPassenger() != null) {
                    tripInfo.put("passengerUsername", tripObject.getPassenger().getUsername());
                }
                if (tripObject.getSubscribeUser() != null) {
                    subUser.put("id", tripObject.getSubscribeUser().getId());
                    subUser.put("firstName", tripObject.getSubscribeUser().getFirstName());
                    subUser.put("lastName", tripObject.getSubscribeUser().getLastName());
                    subUser.put("phoneNumber", tripObject.getSubscribeUser().getPhoneNumber());
                    subUser.put("subscriptionCode", tripObject.getSubscribeUser().getSubscriptionCode());
                    subUser.put("operatorUsername", tripObject.getOperator().getUsername());
                    tripInfo.put("subUser", subUser);
                }
                if (tripObject.getDeliveryInfo() != null) {
                    deliveryInfo.put("id", tripObject.getDeliveryInfo().getId());
                    deliveryInfo.put("desc", tripObject.getDeliveryInfo().getDesc());
                    deliveryInfo.put("destInfo", tripObject.getDeliveryInfo().getDestinationInfo());
                    deliveryInfo.put("receiverFName", tripObject.getDeliveryInfo().getReceiverFirstName());
                    deliveryInfo.put("receiverLName", tripObject.getDeliveryInfo().getReceiverLastName());

                    tripInfo.put("deliveryInfo", deliveryInfo);
                }
                if (tripObject.getVoucherCode() != null) {
                    tripInfo.put("voucherId", tripObject.getVoucherCode().getId());
                }
                JSONArray destinations = new JSONArray();

                List<TripDestination> destinationList = tripObject.getDestinations();
                for (TripDestination destination : destinationList) {
                    JSONObject d = new JSONObject();
                    d.put("destId", destination.getId());
                    d.put("seq", destination.getSeqNumber());
                    d.put("lng", destination.getLocation().getLongitude());
                    d.put("lat", destination.getLocation().getLatitude());
                    d.put("address", destination.getAddress());
                    destinations.put(d);
                }
                tripInfo.put("destinations", destinations);
                trips.put(tripInfo);

            }
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }


        jsonObjectResponse.put("trips", trips);
        return jsonObjectResponse;
    }

    public boolean isOrganizationPhoneNumberExist(String phoneNumber) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            return !entityManager.createNamedQuery("organization.findBy.phoneNumber")
                    .setParameter("phoneNumber", phoneNumber)
                    .setMaxResults(1)
                    .getResultList()
                    .isEmpty();
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public boolean isOrganizationUsernameExist(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            return !entityManager.createNamedQuery("organization.exact.username")
                    .setParameter("username", username)
                    .setMaxResults(1)
                    .getResultList()
                    .isEmpty();
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public Object viewProviders() {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        List<ServiceProvider> serviceProvidersList = null;
        JSONArray providersJSONArray = new JSONArray();
        try {
            entityManager.getTransaction().begin();
            serviceProvidersList = entityManager.createNamedQuery("serviceProvider.get.all")
                    .getResultList();

            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (JSONException e) {
            return Status.BAD_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.UNKNOWN_ERROR;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        for(ServiceProvider s : serviceProvidersList){
            JSONObject providerJSON = new JSONObject();

            providerJSON.put("driversClaim", s.getDriversClaim());
            providerJSON.put("totalClaim", s.getTotalClaim());
            providerJSON.put("id", s.getId());
            providerJSON.put("name", s.getName());

            providersJSONArray.put(providerJSON);
        }
        jsonObjectResponse.put("serviceProvider", providersJSONArray);
        return jsonObjectResponse;
    }

    public Status changeOperatorPassword(String logedInOperatorUsername, String newPass){
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator loggedInOperator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", logedInOperatorUsername)
                    .getSingleResult();
            String hashedNewPassword = EncoderUtil.getSHA512Hash(newPass).toLowerCase();
            loggedInOperator.setPassword(hashedNewPassword);
            entityManager.getTransaction().commit();
            return Status.OK;
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
