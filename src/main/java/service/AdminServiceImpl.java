package service;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import model.entity.persistent.*;
import model.enums.*;
import model.enums.City;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.transaction.annotation.Transactional;
import security.model.AccountCredentials;
import util.EncoderUtil;
import util.IOCContainer;
import util.LocalEntityManagerFactory;
import util.Reloader;
import util.converter.CityConverter;
import util.converter.ServiceTypeConverter;
import util.converter.TicketStateConverter;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@org.springframework.stereotype.Service("adminService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
@Transactional
public class AdminServiceImpl implements AdminService {

    public AdminServiceImpl() {

    }

    public Object adminLogin(String username, String password) {
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

            } catch (NoResultException e) {
                entityManager.getTransaction().commit();
                return Status.NOT_FOUND;
            }

            entityManager.persist(operator);
            entityManager.getTransaction().commit();
            return new AccountCredentials(operator.getUsername(), "A");
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

    public Status masterRegister(String creatorUsername, String firstname, String lastname, Date birthDate, String email, String PhoneNumber, String workNumber, String username, String password, Gender gender, City city) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator creator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username",creatorUsername)
                    .getSingleResult();
            Operator operator = (Operator) IOCContainer.getBean("operator");

            String hashedPassword = EncoderUtil.getSHA512Hash(password).toLowerCase();
            operator.setFirstName(firstname);
            operator.setLastName(lastname);
            operator.setUsername(username);
            operator.setPassword(hashedPassword);
            operator.setPhoneNumber(PhoneNumber);
            operator.setBirthDate(birthDate);
            operator.setWorkNumber(workNumber);
            operator.setCity(city);
            operator.setLoggedIn(false);
            operator.setEmail(email);
            operator.setGender(gender);
            operator.setCreator(creator);
            operator.setAccountState(AccountState.VERIFIED);
            operator.setRegistrationTimestamp(new Timestamp(System.currentTimeMillis()));
            operator.setRole(UserRole.MASTER_OPERATOR);

            entityManager.persist(operator);
            entityManager.getTransaction().commit();

            return Status.OK;
        } catch (RollbackException e) {
            entityManager.getTransaction().rollback();
            return Status.BAD_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Status adminRegister(String firstname, String lastname, Date birthDate, String email, String PhoneNumber, String workNumber, String username, String password, Gender gender, City city) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) IOCContainer.getBean("operator");

            String hashedPassword = EncoderUtil.getSHA512Hash(password).toLowerCase();
            operator.setFirstName(firstname);
            operator.setLastName(lastname);
            operator.setUsername(username);
            operator.setPassword(hashedPassword);
            operator.setPhoneNumber(PhoneNumber);
            operator.setBirthDate(birthDate);
            operator.setWorkNumber(workNumber);
            operator.setCity(city);
            operator.setLoggedIn(false);
            operator.setEmail(email);
            operator.setGender(gender);
            operator.setAccountState(AccountState.VERIFIED);
            operator.setRegistrationTimestamp(new Timestamp(System.currentTimeMillis()));
            operator.setRole(UserRole.ADMIN);

            entityManager.persist(operator);
            entityManager.getTransaction().commit();

            return Status.OK;
        } catch (RollbackException e) {
            entityManager.getTransaction().rollback();
            return Status.BAD_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Status serviceTypeRegister(City city, ServiceType serviceType) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();

            Service service = (Service) IOCContainer.getBean("service");
            List<Service> services = entityManager.createNamedQuery("activeServices.find.byCityAndService")
                    .setParameter("city", city)
                    .setParameter("serviceType", serviceType)
                    .getResultList();
            if (services.size() == 0) {
                try {
                    Tariff tariff = (Tariff) entityManager.createNamedQuery("tariff.findby.cityAndServiceType")
                            .setParameter("serviceType", serviceType)
                            .setParameter("city", city)
                            .getSingleResult();

                    service.setCity(city);
                    service.setServiceState(true);
                    service.setServiceType(serviceType);

                    Reloader r =(Reloader)IOCContainer.getBean("reloader");
                    Status reloadStatus = r.reload(ReloadType.SERVICE_TYPE);
                    if(reloadStatus == Status.OK){
                        entityManager.persist(service);
                        entityManager.getTransaction().commit();
                        r =(Reloader)IOCContainer.getBean("reloader");
                        reloadStatus = r.reload(ReloadType.SERVICE_TYPE);
                        return reloadStatus;
                    }else{
                        return Status.RELOAD_NOT_OCCURRED;
                    }

                } catch (NoResultException e) {
                    return Status.TARIFF_NOT_REGISTERED;
                }
            } else {
                return Status.SERVICE_TYPE_EXIST;
            }
        } catch (RollbackException e) {
            entityManager.getTransaction().rollback();
            return Status.BAD_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public JSONObject viewActiveServices(City city) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject actives = new JSONObject();
        JSONArray services = new JSONArray();
        ServiceTypeConverter serviceTypeConverter = (ServiceTypeConverter) IOCContainer.getBean("serviceTypeConverter");

        try {
            entityManager.getTransaction().begin();
            List activeServices = entityManager.createNamedQuery("activeServices.find.byCity")
                    .setParameter("city", city)
                    .getResultList();
            entityManager.getTransaction().commit();
            for (int i = 0; i < activeServices.size(); i++) {
                JSONObject ob = new JSONObject();
                Service a = (Service) activeServices.get(i);
                ob.put("id", a.getId());
                ob.put("type", serviceTypeConverter.convertToDatabaseColumn(a.getServiceType()));
                ob.put("status", a.getServiceState());
                services.put(ob);
            }
            actives.put("activeServices", services);
            return actives;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Status radiusRegister(double radius, ServiceType serviceType) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            SearchRadius searchRadius = (SearchRadius) IOCContainer.getBean("searchRadius");
            List<SearchRadius> sr = entityManager.createNamedQuery("searchRadius.findBy.serviceType")
                    .setParameter("serviceType", serviceType)
                    .getResultList();
            if (sr.size() == 0) {
                searchRadius.setRadius(radius);
                searchRadius.setServiceType(serviceType);
                Reloader r =(Reloader)IOCContainer.getBean("reloader");
                Status reloadStatus = r.reload(ReloadType.SEARCH_RADIUS);
                if(reloadStatus == Status.OK){
                    entityManager.persist(searchRadius);
                    entityManager.getTransaction().commit();
                    r =(Reloader)IOCContainer.getBean("reloader");
                    reloadStatus = r.reload(ReloadType.SEARCH_RADIUS);
                    return reloadStatus;
                }else{
                    return Status.RELOAD_NOT_OCCURRED;
                }

            } else {
                return Status.SERVICE_TYPE_EXIST;
            }
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

    public Status radiusUpdate(double radius, ServiceType serviceType) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            SearchRadius sr = (SearchRadius) entityManager.createNamedQuery("searchRadius.findBy.serviceType")
                    .setParameter("serviceType", serviceType)
                    .getSingleResult();
            sr.setRadius(radius);
            Status reloadStatus;
            Reloader r =(Reloader)IOCContainer.getBean("reloader");
            reloadStatus = r.reload(ReloadType.SEARCH_RADIUS);
            if(reloadStatus == Status.OK){
                entityManager.getTransaction().commit();
                r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(ReloadType.SEARCH_RADIUS);
                return reloadStatus;
            }else{
                return Status.RELOAD_NOT_OCCURRED;
            }
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

    public Object viewSearchRadius() {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray types = new JSONArray();
        try {
            entityManager.getTransaction().begin();
            List<SearchRadius> searchRadius = entityManager.createNamedQuery("searchRadius.all")
                    .getResultList();
            for (int i = 0; i < searchRadius.size(); i++) {
                JSONObject type = new JSONObject();
                type.put("radius", searchRadius.get(i).getRadius());
                type.put("serviceType", ((ServiceTypeConverter) IOCContainer.getBean("serviceTypeConverter")).convertToDatabaseColumn(searchRadius.get(i).getServiceType()));
                types.put(type);
            }
            jsonObjectResponse.put("searchRadius", types);
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

    public Object viewSearchRadiusByServiceType(ServiceType serviceType) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray types = new JSONArray();
        try {
            entityManager.getTransaction().begin();
            SearchRadius searchRadius = (SearchRadius) entityManager.createNamedQuery("searchRadius.findBy.serviceType")
                    .setParameter("serviceType", serviceType).getSingleResult();
            JSONObject type = new JSONObject();
            type.put("radius", searchRadius.getRadius());
            type.put("serviceType", ((ServiceTypeConverter) IOCContainer.getBean("serviceTypeConverter")).convertToDatabaseColumn(searchRadius.getServiceType()));
            types.put(type);
            jsonObjectResponse.put("searchRadius", types);
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

    public Status operatorRemove(Long operatorID) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.findBy.id")
                    .setParameter("id", operatorID)
                    .setMaxResults(1).getSingleResult();
            if (operator != null) {
                operator.setAccountState(AccountState.DEACTIVATE);
                entityManager.getTransaction().commit();
                return Status.OK;
            } else {
                return Status.NOT_FOUND;
            }

        } catch (RollbackException e) {
            entityManager.getTransaction().rollback();
            return Status.BAD_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public boolean isPhoneNumberExist(String phoneNumber) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            return !entityManager.createNamedQuery("operator.phoneNumber.exist")
                    .setParameter("phoneNumber", phoneNumber)
                    .setMaxResults(1)
                    .getResultList()
                    .isEmpty();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public boolean isUsernameExist(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            return !entityManager.createNamedQuery("operator.username.exist")
                    .setParameter("username", username)
                    .setMaxResults(1)
                    .getResultList()
                    .isEmpty();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Object searchTariff(String cityName) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray tariffJsonArray = new JSONArray();

        try {
            entityManager.getTransaction().begin();
            List<Tariff> tariffs;
            CityConverter cityConverter = (CityConverter) IOCContainer.getBean("cityConverter");
            ServiceTypeConverter serviceTypeConverter = (ServiceTypeConverter) IOCContainer.getBean("serviceTypeConverter");
            City city = cityConverter.convertToEntityAttribute(cityName);

            tariffs = entityManager.createNamedQuery("tariff.findBy.city")
                    .setParameter("city", city)
                    .getResultList();

            entityManager.getTransaction().commit();
            for (Tariff t : tariffs) {
                JSONObject tariffJsonObject = new JSONObject();
                tariffJsonObject.put("id", t.getId());
                tariffJsonObject.put("serviceType", serviceTypeConverter.convertToDatabaseColumn(t.getServiceType()));
                tariffJsonObject.put("genoShare", t.getGenoSharePercentage());
                tariffJsonObject.put("city", cityConverter.convertToDatabaseColumn(t.getCity()));
                tariffJsonObject.put("after2KM", t.getCostPerMeterAfter2KM());
                tariffJsonObject.put("before2KM", t.getCostPerMeterBefore2KM());
                tariffJsonObject.put("perMin", t.getCostPerMin());
                tariffJsonObject.put("waitingMin", t.getCostPerWaitingMin());
                tariffJsonObject.put("twoWayCost", t.getTwoWayCostPercentage());
                tariffJsonObject.put("entrance", t.getEntranceCost());
                tariffJsonArray.put(tariffJsonObject);
            }
            jsonObjectResponse.put("tariffs", tariffJsonArray);
            return jsonObjectResponse;
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Status tariffRegister(City city, double before2KM, double after2KM, double perMin, double waitingMin, double entrance, ServiceType serviceType, int twoWayCost, int coShare) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {

            entityManager.getTransaction().begin();
            List<Tariff> t = entityManager.createNamedQuery("tariff.findby.cityAndServiceType")
                    .setParameter("serviceType", serviceType)
                    .setParameter("city", city)
                    .getResultList();
            if (t.size() == 0) {
                Tariff tariff = (Tariff) IOCContainer.getBean("tariff");
                tariff.setCity(city);
                tariff.setCostPerMeterBefore2KM(before2KM);
                tariff.setCostPerMeterAfter2KM(after2KM);
                tariff.setCostPerMin(perMin);
                tariff.setCostPerWaitingMin(waitingMin);
                tariff.setEntranceCost(entrance);
                tariff.setServiceType(serviceType);
                tariff.setTwoWayCostPercentage(twoWayCost);
                tariff.setGenoSharePercentage(coShare);

                Reloader r =(Reloader)IOCContainer.getBean("reloader");
                Status reloadStatus = r.reload(ReloadType.TARIFF);
                if(reloadStatus == Status.OK){
                    entityManager.persist(tariff);
                    entityManager.getTransaction().commit();
                    r =(Reloader)IOCContainer.getBean("reloader");
                    reloadStatus = r.reload(ReloadType.TARIFF);
                    return reloadStatus;
                }else{
                    return Status.RELOAD_NOT_OCCURRED;
                }

            } else {
                return Status.SERVICE_TYPE_EXIST;
            }

        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Status tariffUpdate(Long tariffID, double before2KM, double after2KM, double perMin, double waitingMin, double entrance, int twoWayCost, int coShare) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();

            TariffHistory tariffHistory = (TariffHistory) IOCContainer.getBean("tariffHistory");


            Tariff previousTariff = entityManager.find(Tariff.class, tariffID);

            tariffHistory.setCity(previousTariff.getCity());
            tariffHistory.setCostPerMeterBefore2KM(previousTariff.getCostPerMeterBefore2KM());
            tariffHistory.setCostPerMeterAfter2KM(previousTariff.getCostPerMeterAfter2KM());
            tariffHistory.setCostPerMin(previousTariff.getCostPerMin());
            tariffHistory.setCostPerWaitingMin(previousTariff.getCostPerWaitingMin());
            tariffHistory.setEntranceCost(previousTariff.getEntranceCost());
            tariffHistory.setServiceType(previousTariff.getServiceType());
            tariffHistory.setEndDate(new Date());
            entityManager.persist(tariffHistory);

            previousTariff.setCostPerMeterBefore2KM(before2KM);
            previousTariff.setCostPerMeterAfter2KM(after2KM);
            previousTariff.setCostPerMin(perMin);
            previousTariff.setCostPerWaitingMin(waitingMin);
            previousTariff.setEntranceCost(entrance);
            previousTariff.setTwoWayCostPercentage(twoWayCost);
            previousTariff.setGenoSharePercentage(coShare);


            Reloader r =(Reloader)IOCContainer.getBean("reloader");
            Status reloadStatus = r.reload(ReloadType.TARIFF);
            if(reloadStatus == Status.OK){
                entityManager.getTransaction().commit();
                r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(ReloadType.TARIFF);
                return reloadStatus;
            }else{
                return Status.RELOAD_NOT_OCCURRED;
            }
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (NullPointerException e) {
            return Status.NOT_FOUND;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Status banOperator(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", username)
                    .getSingleResult();
            operator.setAccountState(AccountState.BANNED);
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

    public Status unBanOperator(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", username)
                    .getSingleResult();
            operator.setAccountState(AccountState.VERIFIED);
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

    public Status disableService(Long serviceID) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            model.entity.persistent.Service service = entityManager.find(model.entity.persistent.Service.class, serviceID);
            service.setServiceState(false);
            Reloader r =(Reloader)IOCContainer.getBean("reloader");
            Status reloadStatus = r.reload(ReloadType.SERVICE_TYPE);
            if(reloadStatus == Status.OK){
                entityManager.getTransaction().commit();
                r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(ReloadType.SERVICE_TYPE);
                return reloadStatus;
            }else{
                return Status.RELOAD_NOT_OCCURRED;
            }

        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (NullPointerException e) {
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

    public Status enableService(Long serviceID) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            model.entity.persistent.Service service = entityManager.find(model.entity.persistent.Service.class, serviceID);
            Tariff t = (Tariff) entityManager.createNamedQuery("tariff.findby.cityAndServiceType")
                    .setParameter("city", service.getCity())
                    .setParameter("serviceType", service.getServiceType())
                    .getSingleResult();
            service.setServiceState(true);
            Reloader r =(Reloader)IOCContainer.getBean("reloader");
            Status reloadStatus = r.reload(ReloadType.SERVICE_TYPE);
            if(reloadStatus == Status.OK){
                entityManager.getTransaction().commit();
                r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(ReloadType.SERVICE_TYPE);
                return reloadStatus;
            }else{
                return Status.RELOAD_NOT_OCCURRED;
            }

        } catch (NoResultException e) {
            return Status.TARIFF_NOT_REGISTERED;
        } catch (NullPointerException e) {
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

    public Object viewBugs() {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray bugJsonArray = new JSONArray();
        try {
            entityManager.getTransaction().begin();
            List<Bug> bugs;
            TicketStateConverter ticketStateConverter = (TicketStateConverter) IOCContainer.getBean("ticketStateConverter");
            bugs = entityManager.createNamedQuery("bug.all")
                    .getResultList();

            entityManager.getTransaction().commit();
            for (Bug b : bugs) {
                JSONObject bugJSON = new JSONObject();
                bugJSON.put("id", b.getId());
                bugJSON.put("description", b.getDesc());
                bugJSON.put("state", ticketStateConverter.convertToDatabaseColumn(b.getState()));
                if (b.getDriver() != null) {
                    bugJSON.put("driverUsername", b.getDriver().getUsername());
                }
                if (b.getPassenger() != null) {
                    bugJSON.put("passengerUsername", b.getPassenger().getUsername());
                }
                bugJsonArray.put(bugJSON);
            }
            jsonObjectResponse.put("bugs", bugJsonArray);

        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    public Status resolveBug(Long bugID) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Bug bug = entityManager.find(Bug.class, bugID);
            bug.setState(TicketState.RESOLVED);
            entityManager.getTransaction().commit();
            return Status.OK;
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (NullPointerException e) {
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

    public Status settingUpdate(Boolean smsSender, Boolean emailSender, Boolean dailySmsReport, Boolean weeklySmsReport,
                                Boolean dailyEmailReport, Boolean weeklyEmailReport, Boolean monthlyEmailReport, Boolean exceptionOccurrenceSms, Boolean exceptionOccurrenceEmail,
                                Boolean IOSUpdate, Boolean IOSUpdateCritical, Boolean androidUpdate, Boolean androidUpdateCritical, Boolean allowCompetitors) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {

            entityManager.getTransaction().begin();

            SystemSetting systemSetting = (SystemSetting) entityManager.createNamedQuery("systemSetting.all")
                    .getSingleResult();

            systemSetting.setSmsSenderState(smsSender);
            systemSetting.setEmailSenderState(emailSender);
            systemSetting.setSmsDailyReportState(dailySmsReport);
            systemSetting.setSmsWeeklyReportState(weeklySmsReport);
            systemSetting.setEmailDailyReportState(dailyEmailReport);
            systemSetting.setEmailWeeklyReportState(weeklyEmailReport);
            systemSetting.setEmailMonthlyReportState(monthlyEmailReport);
            systemSetting.setSmsExceptionOccurrenceState(exceptionOccurrenceSms);
            systemSetting.setEmailExceptionOccurrenceState(exceptionOccurrenceEmail);
            systemSetting.setIosUpdate(IOSUpdate);
            systemSetting.setCriticalIosUpdate(IOSUpdateCritical);
            systemSetting.setAndroidUpdate(androidUpdate);
            systemSetting.setCriticalAndroidUpdate(androidUpdateCritical);
            systemSetting.setAllowCompetitors(allowCompetitors);

            Reloader r =(Reloader)IOCContainer.getBean("reloader");
            Status reloadStatus = r.reload(ReloadType.SYSTEM_SETTING);
            if(reloadStatus == Status.OK){
                r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(ReloadType.SYSTEM_SETTING);
                entityManager.getTransaction().commit();
                return reloadStatus;
            }else{
                return Status.RELOAD_NOT_OCCURRED;
            }

        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (NullPointerException e) {
            return Status.NOT_FOUND;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Object viewSystemSetting() {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        try {
            entityManager.getTransaction().begin();
            SystemSetting s = (SystemSetting) entityManager.createNamedQuery("systemSetting.all")
                    .getSingleResult();
            JSONObject settingJSON = new JSONObject();

            settingJSON.put("smsSender", s.getSmsSenderState());
            settingJSON.put("emailSender", s.getEmailSenderState());
            settingJSON.put("dailySmsReport", s.getSmsDailyReportState());
            settingJSON.put("weeklySmsReport", s.getSmsWeeklyReportState());
            settingJSON.put("dailyEmailReport", s.getEmailDailyReportState());
            settingJSON.put("weeklyEmailReport", s.getEmailWeeklyReportState());
            settingJSON.put("monthlyEmailReport", s.getEmailMonthlyReportState());
            settingJSON.put("exceptionOccurrenceSms", s.getSmsExceptionOccurrenceState());
            settingJSON.put("exceptionOccurrenceEmail", s.getEmailExceptionOccurrenceState());
            settingJSON.put("IOSUpdate", s.getIosUpdate());
            settingJSON.put("IOSUpdateCritical", s.getCriticalIosUpdate());
            settingJSON.put("androidUpdate", s.getAndroidUpdate());
            settingJSON.put("androidUpdateCritical", s.getCriticalAndroidUpdate());
            settingJSON.put("allowCompetitors", s.getAllowCompetitors());

            jsonObjectResponse.put("systemSetting", settingJSON);
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

    public Status stateRegister(String name) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            State state = (State) IOCContainer.getBean("state");

            state.setName(name);
            Reloader r =(Reloader)IOCContainer.getBean("reloader");
            Status reloadStatus = r.reload(ReloadType.STATES);
            if(reloadStatus == Status.OK){
                entityManager.persist(state);
                entityManager.getTransaction().commit();
                r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(ReloadType.STATES);
                return reloadStatus;
            }else{
                return Status.RELOAD_NOT_OCCURRED;
            }

        } catch (RollbackException e) {
            entityManager.getTransaction().rollback();
            return Status.BAD_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Status cityRegister(String name, Long stateID) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            model.entity.persistent.City city = (model.entity.persistent.City) IOCContainer.getBean("city");
            State state = (State) entityManager.createNamedQuery("state.by.id")
                    .setParameter("id", stateID)
                    .getSingleResult();
            city.setState(state);
            city.setName(name);
            Reloader r =(Reloader)IOCContainer.getBean("reloader");
            Status reloadStatus = r.reload(ReloadType.STATES);
            if(reloadStatus == Status.OK){
                entityManager.persist(city);
                entityManager.getTransaction().commit();
                r =(Reloader)IOCContainer.getBean("reloader");
                reloadStatus = r.reload(ReloadType.STATES);
                return reloadStatus;
            }else{
                return Status.RELOAD_NOT_OCCURRED;
            }

        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (NullPointerException e) {
            return Status.NOT_FOUND;
        } catch (RollbackException e) {
            entityManager.getTransaction().rollback();
            return Status.BAD_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public State isStateExist(String name) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        State state = null;
        try {
            entityManager.getTransaction().begin();
            state = (State) entityManager.createNamedQuery("state.by.name")
                    .setParameter("name", name)
                    .getSingleResult();
            return state;
        } catch (NoResultException e) {
            return state;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public model.entity.persistent.City isCityExist(String name, Long stateID) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        model.entity.persistent.City city = null;
        try {
            entityManager.getTransaction().begin();
            city = (model.entity.persistent.City) entityManager.createNamedQuery("city.by.nameAndStateID")
                    .setParameter("name", name)
                    .setParameter("stateID", stateID)
                    .getSingleResult();
            return city;
        } catch (NoResultException e) {
            return city;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void driversAgeReport(HttpServletResponse resp) {
        List<Driver> driversList = null;
        Date oldest = new Date(), youngest = new Date();
        youngest.setYear(0);
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            driversList = entityManager.createNamedQuery("driver.all")
                    .getResultList();
            entityManager.getTransaction().commit();
        } catch (RollbackException e) {
            entityManager.getTransaction().rollback();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        WritableWorkbook driverExcelWorkSheet = null;
        try {
            int sum = 0;
            int num = 0;
            driverExcelWorkSheet = Workbook.createWorkbook(resp.getOutputStream());

            WritableSheet excelSheet = driverExcelWorkSheet.createSheet("رانندگان", 0);
//            excelSheet.getSettings().setProtected(true);
            Date birthDate = null;
            Label label = new Label(0, 4, "نام کاربری");
            excelSheet.addCell(label);
            label = new Label(1, 4, "شماره تلفن همراه");
            excelSheet.addCell(label);
            label = new Label(2, 4, "تاریخ تولد");
            excelSheet.addCell(label);
            for(int i=0;i<driversList.size();i++){
                Driver driver = driversList.get(i);
                birthDate = driver.getBirthDate();
                if(birthDate!=null) {
                    if (birthDate.before(oldest)) {
                        oldest = birthDate;
                    }
                    if (birthDate.after(youngest)) {
                        youngest = birthDate;
                    }

                    LocalDate bDateLocal = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate tDateLocal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    sum = sum + Period.between(bDateLocal, tDateLocal).getYears();
                    num++;
                }
                label = new Label(0, i+5, driver.getUsername());
                excelSheet.addCell(label);
                label = new Label(1, i+5, driver.getPhoneNumber());
                excelSheet.addCell(label);
                if(birthDate!=null) {
                    DateFormat outputFormatter = new SimpleDateFormat("yyyy/MM/dd");
                    String outputDate = outputFormatter.format(birthDate);
                    label = new Label(2, i + 5, String.valueOf(outputDate));
                    excelSheet.addCell(label);
                }

            }
            label = new Label(0, 0, "جوان ترین راننده");
            excelSheet.addCell(label);
            label = new Label(1, 0, "پیرترین راننده");
            excelSheet.addCell(label);
            label = new Label(2, 0, "میانگین سنی رانندگان");
            excelSheet.addCell(label);
            label = new Label(3, 0, "تعداد کل رانندگان");
            excelSheet.addCell(label);

            LocalDate bDateLocal = youngest.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate tDateLocal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int youngestAge = Period.between(bDateLocal, tDateLocal).getYears();

            bDateLocal = oldest.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            tDateLocal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int oldestAge = Period.between(bDateLocal, tDateLocal).getYears();

            label = new Label(0, 1, String.valueOf(youngestAge));
            excelSheet.addCell(label);
            label = new Label(1, 1, String.valueOf(oldestAge));
            excelSheet.addCell(label);
            label = new Label(2, 1, String.valueOf(Double.valueOf(sum)/num));
            excelSheet.addCell(label);
            label = new Label(3, 1, String.valueOf(driversList.size()));
            excelSheet.addCell(label);

            driverExcelWorkSheet.write();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {
            if (driverExcelWorkSheet != null) {
                try {
                    driverExcelWorkSheet.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void passengersAgeReport(HttpServletResponse resp) {
        List<Passenger> passengersList = null;
        Date oldest = new Date(), youngest = new Date();
        youngest.setYear(0);
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        WritableWorkbook passengerExcelWorkSheet = null;
        try {
            entityManager.getTransaction().begin();
            passengersList = entityManager.createNamedQuery("passenger.all")
                    .getResultList();
            int sum = 0;
            int num = 0;
            passengerExcelWorkSheet = Workbook.createWorkbook(resp.getOutputStream());
            WritableSheet excelSheet = passengerExcelWorkSheet.createSheet("مسافران", 0);
//            excelSheet.getSettings().setProtected(true);

            Label label = new Label(0, 4, "نام کاربری");
            excelSheet.addCell(label);
            label = new Label(1, 4, "شماره تلفن همراه");
            excelSheet.addCell(label);
            label = new Label(2, 4, "تاریخ تولد");
            excelSheet.addCell(label);
            for(int i=0;i<passengersList.size();i++){
                Passenger passenger = passengersList.get(i);

                label = new Label(0, i+5, passenger.getUsername());
                excelSheet.addCell(label);
                label = new Label(1, i+5, passenger.getPhoneNumber());
                excelSheet.addCell(label);

                if(passenger.getPassengerInfo()!=null) {
                    Date birthDate = passenger.getPassengerInfo().getBirthDate();
                    if (birthDate != null) {
                        if (birthDate.before(oldest)) {
                            oldest = birthDate;
                        }
                        if (birthDate.after(youngest)) {
                            youngest = birthDate;
                        }

                        LocalDate bDateLocal = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate tDateLocal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        sum = sum + Period.between(bDateLocal, tDateLocal).getYears();

                        if (birthDate != null) {
                            DateFormat outputFormatter = new SimpleDateFormat("yyyy/MM/dd");
                            String outputDate = outputFormatter.format(birthDate);
                            label = new Label(2, i + 5, String.valueOf(outputDate));
                            excelSheet.addCell(label);
                        }

                        num++;
                    }
                }
            }
            label = new Label(0, 0, "جوان ترین مسافر");
            excelSheet.addCell(label);
            label = new Label(1, 0, "پیرترین مسافر");
            excelSheet.addCell(label);
            label = new Label(2, 0, "میانگین سنی مسافران");
            excelSheet.addCell(label);
            label = new Label(3, 0, "تعداد کل مسافران");
            excelSheet.addCell(label);



            LocalDate bDateLocal = youngest.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate tDateLocal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int youngestAge = Period.between(bDateLocal, tDateLocal).getYears();



            bDateLocal = oldest.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            tDateLocal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int oldestAge = Period.between(bDateLocal, tDateLocal).getYears();



            label = new Label(0, 1, String.valueOf(youngestAge));
            excelSheet.addCell(label);
            label = new Label(1, 1, String.valueOf(oldestAge));
            excelSheet.addCell(label);
            label = new Label(2, 1, String.valueOf(Double.valueOf(sum)/num));
            excelSheet.addCell(label);
            label = new Label(3, 1, String.valueOf(passengersList.size()));
            excelSheet.addCell(label);

            passengerExcelWorkSheet.write();
            entityManager.getTransaction().commit();
            }catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }catch (RollbackException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (passengerExcelWorkSheet != null) {
                try {
                    passengerExcelWorkSheet.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
                }
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void operatorsAgeReport(HttpServletResponse resp) {
        List<Operator> operatorsList = null;
        Date oldest = new Date(), youngest = new Date();
        youngest.setYear(0);
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            operatorsList = entityManager.createNamedQuery("operator.all")
                    .getResultList();
            entityManager.getTransaction().commit();
        } catch (RollbackException e) {
            entityManager.getTransaction().rollback();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        WritableWorkbook operatorExcelWorkSheet = null;
        try {
            int sum = 0;
            int num = 0;
            operatorExcelWorkSheet = Workbook.createWorkbook(resp.getOutputStream());

            WritableSheet excelSheet = operatorExcelWorkSheet.createSheet("اپراتورها", 0);
//            excelSheet.getSettings().setProtected(true);
            Date birthDate = null;
            Label label = new Label(0, 4, "نام کاربری");
            excelSheet.addCell(label);
            label = new Label(1, 4, "شماره تلفن همراه");
            excelSheet.addCell(label);
            label = new Label(2, 4, "تاریخ تولد");
            excelSheet.addCell(label);
            for(int i=0;i<operatorsList.size();i++){
                Operator operator = operatorsList.get(i);
                birthDate = operator.getBirthDate();
                if(birthDate!=null) {
                    if (birthDate.before(oldest)) {
                        oldest = birthDate;
                    }
                    if (birthDate.after(youngest)) {
                        youngest = birthDate;
                    }

                    LocalDate bDateLocal = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate tDateLocal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    sum = sum + Period.between(bDateLocal, tDateLocal).getYears();
                    num++;
                }
                label = new Label(0, i+5, operator.getUsername());
                excelSheet.addCell(label);
                label = new Label(1, i+5, operator.getPhoneNumber());
                excelSheet.addCell(label);

                if(birthDate!=null) {
                    DateFormat outputFormatter = new SimpleDateFormat("yyyy/MM/dd");
                    String outputDate = outputFormatter.format(birthDate);
                    label = new Label(2, i + 5, String.valueOf(outputDate));
                    excelSheet.addCell(label);
                }

            }
            label = new Label(0, 0, "جوان ترین اپراتور");
            excelSheet.addCell(label);
            label = new Label(1, 0, "پیرترین اپراتور");
            excelSheet.addCell(label);
            label = new Label(2, 0, "میانگین سنی اپراتورها");
            excelSheet.addCell(label);
            label = new Label(3, 0, "تعداد کل اپراتورها");
            excelSheet.addCell(label);



            LocalDate bDateLocal = youngest.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate tDateLocal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int youngestAge = Period.between(bDateLocal, tDateLocal).getYears();



            bDateLocal = oldest.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            tDateLocal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int oldestAge = Period.between(bDateLocal, tDateLocal).getYears();



            label = new Label(0, 1, String.valueOf(youngestAge));
            excelSheet.addCell(label);
            label = new Label(1, 1, String.valueOf(oldestAge));
            excelSheet.addCell(label);
            label = new Label(2, 1, String.valueOf(Double.valueOf(sum)/num));
            excelSheet.addCell(label);
            label = new Label(3, 1, String.valueOf(operatorsList.size()));
            excelSheet.addCell(label);

            operatorExcelWorkSheet.write();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {
            if (operatorExcelWorkSheet != null) {
                try {
                    operatorExcelWorkSheet.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void tripsReport(HttpServletResponse resp,Date startDate, Date endDate) {
        List<Trip> tripsList = null;
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        WritableWorkbook operatorExcelWorkSheet = null;
        try {
            entityManager.getTransaction().begin();
            tripsList = entityManager.createNamedQuery("trip.between.date")
                    .setParameter("startDate",startDate)
                    .setParameter("endDate",endDate)
                    .getResultList();

            int sumCost = 0;
            int sumCashPay = 0;
            int sumCreditPay = 0;
            int sumRate = 0;
            int rates = 0;
            int mostRate = 0;
            int minRate = 5;
            int numOfCanceledTripsByPassenger = 0;
            int numOfCanceledTripsByOperator = 0;
            int numOfCanceledTripsByDriver = 0;
            Long sumDistance = Long.valueOf(0);
            Long mostDistance = Long.valueOf(0);
            Long minDistance = Long.valueOf(Integer.MAX_VALUE);
            operatorExcelWorkSheet = Workbook.createWorkbook(resp.getOutputStream());

            WritableSheet excelSheet = operatorExcelWorkSheet.createSheet("اطلاعات سفرها", 0);

            for(int i=0;i<tripsList.size();i++){
                Trip trip = tripsList.get(i);
                if(trip.getCost()!=null) {
                    sumCost = sumCost + trip.getCost();
                }
                if(trip.getCashPayment()!=null) {
                    sumCashPay = sumCashPay + trip.getCashPayment();
                }
                if(trip.getCreditPayment()!=null) {
                    sumCreditPay = sumCreditPay + trip.getCreditPayment();
                }

                if(trip.getInfo()!= null && trip.getInfo().getDistance()!=null) {
                    sumDistance = sumDistance + trip.getInfo().getDistance();
                    if (trip.getInfo().getDistance() > mostDistance) {
                        mostDistance = trip.getInfo().getDistance();
                    }
                    if (trip.getInfo().getDistance() < minDistance) {
                        minDistance = trip.getInfo().getDistance();
                    }
                }
                if(trip.getInfo()!=null && trip.getInfo().getRate()!=null) {
                    sumRate = sumRate + trip.getInfo().getRate();
                    if(trip.getInfo().getRate()>mostRate){
                        mostRate = trip.getInfo().getRate();
                    }
                    if(trip.getInfo().getRate()<minRate){
                        minRate = trip.getInfo().getRate();
                    }
                    rates++;
                }
                if(trip.getState()==TripState.CANCELED_BY_PASSENGER){
                    numOfCanceledTripsByPassenger++;
                }
                if(trip.getState()==TripState.CANCELED_BY_DRIVER){
                    numOfCanceledTripsByDriver++;
                }
                if(trip.getState()==TripState.CANCELED_BY_OPERATOR){
                    numOfCanceledTripsByOperator++;
                }
            }

            Label label = new Label(0, 0, "تعداد سفرها");
            excelSheet.addCell(label);
            label = new Label(1, 0, "رتبه سفرها");
            excelSheet.addCell(label);
            label = new Label(2, 0, "هزینه کل سفرها");
            excelSheet.addCell(label);
            label = new Label(3, 0, "میزان پرداخت نقدی");
            excelSheet.addCell(label);
            label = new Label(4, 0, "میزان پرداخت الکترونیکی");
            excelSheet.addCell(label);
            label = new Label(5, 0, "مسافت کل سفرها");
            excelSheet.addCell(label);
            label = new Label(6, 0, "بیشترین مسافت سفر");
            excelSheet.addCell(label);
            label = new Label(7, 0, "کمترین مسافت سفر");
            excelSheet.addCell(label);
            label = new Label(8, 0, "بیشترین امتیاز");
            excelSheet.addCell(label);
            label = new Label(9, 0, "کمترین امتیاز");
            excelSheet.addCell(label);
            label = new Label(10, 0, "");
            excelSheet.addCell(label);
            label = new Label(11, 0, "تعداد کنسلی توسط مسافر");
            excelSheet.addCell(label);
            label = new Label(12, 0, "تعداد کنسلی توسط راننده");
            excelSheet.addCell(label);
            label = new Label(13, 0, "تعداد کنسلی توسط اپراتور");
            excelSheet.addCell(label);


            label = new Label(0, 1, String.valueOf(tripsList.size()));
            excelSheet.addCell(label);
            label = new Label(1, 1, String.valueOf(sumRate/(double)rates));
            excelSheet.addCell(label);
            label = new Label(2, 1, String.valueOf(sumCost));
            excelSheet.addCell(label);
            label = new Label(3, 1, String.valueOf(sumCashPay));
            excelSheet.addCell(label);
            label = new Label(4, 1, String.valueOf(sumCreditPay));
            excelSheet.addCell(label);
            label = new Label(5, 1, String.valueOf(sumDistance));
            excelSheet.addCell(label);
            label = new Label(6, 1, String.valueOf(mostDistance));
            excelSheet.addCell(label);
            label = new Label(7, 1, String.valueOf(minDistance));
            excelSheet.addCell(label);
            label = new Label(8, 1, String.valueOf(mostRate));
            excelSheet.addCell(label);
            label = new Label(9, 1, String.valueOf(minRate));
            excelSheet.addCell(label);
            label = new Label(10, 1, String.valueOf(""));
            excelSheet.addCell(label);
            label = new Label(11, 1, String.valueOf(numOfCanceledTripsByPassenger));
            excelSheet.addCell(label);
            label = new Label(12, 1, String.valueOf(numOfCanceledTripsByDriver));
            excelSheet.addCell(label);
            label = new Label(13, 1, String.valueOf(numOfCanceledTripsByOperator));
            excelSheet.addCell(label);

            operatorExcelWorkSheet.write();
            entityManager.getTransaction().commit();
        } catch (RollbackException e) {
            entityManager.getTransaction().rollback();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
            if (operatorExcelWorkSheet != null) {
                try {
                    operatorExcelWorkSheet.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Status insertProvider(String name) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            ServiceProvider serviceProvider = (ServiceProvider) IOCContainer.getBean("serviceProvider");
            serviceProvider.setTotalClaim(Long.valueOf(0));
            serviceProvider.setDriversClaim(Long.valueOf(0));
            serviceProvider.setName(name);
            entityManager.persist(serviceProvider);
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

    public void OSReport(HttpServletResponse resp) {
        List<Device> deviceList = null;
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            deviceList = entityManager.createNamedQuery("device.all")
                    .getResultList();
            entityManager.getTransaction().commit();
        } catch (RollbackException e) {
            entityManager.getTransaction().rollback();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        WritableWorkbook deviceExcelWorkSheet = null;
        try {
            int numAnd = 0;
            int numIOS = 0;
            String maxIOS="0", maxAndroid="0", minIOS="99999999999999", minAndroid="99999999999999";
            deviceExcelWorkSheet = Workbook.createWorkbook(resp.getOutputStream());

            WritableSheet excelSheet = deviceExcelWorkSheet.createSheet("سیستم عامل", 0);
            Label label = new Label(0, 4, "مدل");
            excelSheet.addCell(label);
            label = new Label(1, 4, "سیستم عامل");
            excelSheet.addCell(label);
            label = new Label(2, 4, "نسخه");
            excelSheet.addCell(label);
            label = new Label(3, 4, "سازنده");
            excelSheet.addCell(label);
            for(int i=0;i<deviceList.size();i++){
                Device device = deviceList.get(i);
                if(device.getOS()!=null) {
                    if (device.getOS().toLowerCase().contains("and")) {
                        if(device.getOSVersion().compareTo(maxAndroid)>0){
                            maxAndroid = device.getOSVersion();
                        }
                        if(device.getOSVersion().compareTo(minAndroid)<0){
                            minAndroid = device.getOSVersion();
                        }
                        numAnd++;
                    }
                    if (device.getOS().toLowerCase().contains("ios")) {
                        if(device.getOSVersion().compareTo(maxIOS)>0){
                            maxIOS = device.getOSVersion();
                        }
                        if(device.getOSVersion().compareTo(minIOS)<0){
                            minIOS = device.getOSVersion();
                        }
                        numIOS++;
                    }

                }
                label = new Label(0, i+5, device.getModel());
                excelSheet.addCell(label);
                label = new Label(1, i+5, device.getOS());
                excelSheet.addCell(label);
                label = new Label(2, i+5, device.getOSVersion());
                excelSheet.addCell(label);
                label = new Label(3, i+5, device.getManufacturer());
                excelSheet.addCell(label);

            }
            label = new Label(0, 0, "تعداد دستگاه های ثبت شده");
            excelSheet.addCell(label);
            label = new Label(1, 0, "تعداد دستگاه های Android");
            excelSheet.addCell(label);
            label = new Label(2, 0, "تعداد دستگاه های IOS");
            excelSheet.addCell(label);
            label = new Label(3, 0, "پایین ترین نسخه ی Android");
            excelSheet.addCell(label);
            label = new Label(4, 0, "بالاترین نسخه ی Android");
            excelSheet.addCell(label);
            label = new Label(5, 0, "پایین ترین نسخه ی IOS");
            excelSheet.addCell(label);
            label = new Label(6, 0, "بالاترین نسخه ی IOS");
            excelSheet.addCell(label);




            label = new Label(0, 1, String.valueOf(deviceList.size()));
            excelSheet.addCell(label);
            label = new Label(1, 1, String.valueOf(numAnd));
            excelSheet.addCell(label);
            label = new Label(2, 1, String.valueOf(numIOS));
            excelSheet.addCell(label);
            if(!minAndroid.equals("99999999999999")){
                label = new Label(3, 1, String.valueOf(minAndroid));
            }else{
                label = new Label(3, 1, "0");
            }
            excelSheet.addCell(label);
            label = new Label(4, 1, String.valueOf(maxAndroid));
            excelSheet.addCell(label);
            if(!minIOS.equals("99999999999999")){
                label = new Label(5, 1, String.valueOf(minIOS));
            }else{
                label = new Label(5, 1, "0");
            }
            excelSheet.addCell(label);
            label = new Label(6, 1, String.valueOf(maxIOS));
            excelSheet.addCell(label);

            deviceExcelWorkSheet.write();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {
            if (deviceExcelWorkSheet != null) {
                try {
                    deviceExcelWorkSheet.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void costTripsReport(HttpServletResponse resp) {
        List<Object[]> tripsGroupByCityList = null;
        List<Object[]> tripsGroupByServiceTypeList = null;
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
//            tripsGroupByCityList = entityManager.createNamedQuery("trip.groupBy.city")
//                    .getResultList();
            tripsGroupByServiceTypeList = entityManager.createNamedQuery("trip.groupBy.serviceType")
                    .getResultList();
            entityManager.getTransaction().commit();
        } catch (RollbackException e) {
            entityManager.getTransaction().rollback();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        WritableWorkbook tripCostExcelWorkSheet = null;
        try {
            tripCostExcelWorkSheet = Workbook.createWorkbook(resp.getOutputStream());

            WritableSheet excelSheet = tripCostExcelWorkSheet.createSheet("درآمد", 0);
//            Label label = new Label(0, 0, "نام شهر");
//            excelSheet.addCell(label);
//            label = new Label(1, 0, "مجموع درآمد");
//            excelSheet.addCell(label);

            Label label = new Label(4, 0, "نام سرویس");
            excelSheet.addCell(label);
            label = new Label(5, 0, "مجموع درآمد");
            excelSheet.addCell(label);


//            for (int i=0;i<tripsGroupByCityList.size();i++) {
//                Object[] obj = tripsGroupByCityList.get(i);
//                String city = String.valueOf(obj[0]);
//                Long totalCost = (Long) obj[1];
//                label = new Label(0, i+1, city);
//                excelSheet.addCell(label);
//                label = new Label(1, i+1, String.valueOf(totalCost));
//                excelSheet.addCell(label);
//            }

            for (int i=0;i<tripsGroupByServiceTypeList.size();i++) {
                Object[] obj = tripsGroupByServiceTypeList.get(i);
                String serviceType = String.valueOf(obj[0]);
                Long totalCost = (Long) obj[1];
                label = new Label(4, i+1, serviceType);
                excelSheet.addCell(label);
                label = new Label(5, i+1, String.valueOf(totalCost));
                excelSheet.addCell(label);
            }

            tripCostExcelWorkSheet.write();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {
            if (tripCostExcelWorkSheet != null) {
                try {
                    tripCostExcelWorkSheet.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void providersClaimReport(HttpServletResponse resp) {
        List<ServiceProvider> serviceProviderList = null;
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            serviceProviderList = entityManager.createNamedQuery("serviceProvider.get.all")
                    .getResultList();
            entityManager.getTransaction().commit();
        } catch (RollbackException e) {
            entityManager.getTransaction().rollback();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        WritableWorkbook providersClaimExcelWorkSheet = null;
        try {
            providersClaimExcelWorkSheet = Workbook.createWorkbook(resp.getOutputStream());

            WritableSheet excelSheet = providersClaimExcelWorkSheet.createSheet("بدهی", 0);
            Label label = new Label(0, 3, "نام ارائه دهنده ی سرویس");
            excelSheet.addCell(label);
            label = new Label(1, 3, "بدهی");
            excelSheet.addCell(label);
            Long sum = Long.valueOf(0);
            for (int i=0;i<serviceProviderList.size();i++) {
                ServiceProvider serviceProvider = serviceProviderList.get(i);
                label = new Label(0, i+4, serviceProvider.getName());
                excelSheet.addCell(label);
                sum = sum + serviceProvider.getTotalClaim()-serviceProvider.getDriversClaim();
                label = new Label(1, i+4, String.valueOf(serviceProvider.getTotalClaim()-serviceProvider.getDriversClaim()));
                excelSheet.addCell(label);
            }

            label = new Label(0, 0, "تعداد سرویس دهندگان");
            excelSheet.addCell(label);
            label = new Label(1, 0, "مجموع بدهی");
            excelSheet.addCell(label);

            label = new Label(0, 1, String.valueOf(serviceProviderList.size()));
            excelSheet.addCell(label);
            label = new Label(1, 1, String.valueOf(sum));
            excelSheet.addCell(label);

            providersClaimExcelWorkSheet.write();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {
            if (providersClaimExcelWorkSheet != null) {
                try {
                    providersClaimExcelWorkSheet.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void tripsPeak(HttpServletResponse resp) {
        List<Object[]> tripsPeak = null;
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            tripsPeak = entityManager.createNamedQuery("tripinfo.startdate.peak")
                    .getResultList();
            entityManager.getTransaction().commit();
        } catch (RollbackException e) {
            entityManager.getTransaction().rollback();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        WritableWorkbook tripsPeakExcelWorkSheet = null;
        try {
            tripsPeakExcelWorkSheet = Workbook.createWorkbook(resp.getOutputStream());

            WritableSheet excelSheet = tripsPeakExcelWorkSheet.createSheet("ساعات شلوغی", 0);
            Label label = new Label(0, 0, "تاریخ");
            excelSheet.addCell(label);
            label = new Label(1, 0, "ساعت");
            excelSheet.addCell(label);
            label = new Label(2, 0, "تعداد سفر");
            excelSheet.addCell(label);
            for (int i=0;i<tripsPeak.size();i++) {
                Object[] obj = tripsPeak.get(i);
                String[] split = String.valueOf(obj[0]).split(" ");
                label = new Label(0, i+1, split[0]);
                excelSheet.addCell(label);
                label = new Label(1, i+1, split[1]);
                excelSheet.addCell(label);
                label = new Label(2, i+1, String.valueOf(obj[1]));
                excelSheet.addCell(label);
            }


            tripsPeakExcelWorkSheet.write();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {
            if (tripsPeakExcelWorkSheet != null) {
                try {
                    tripsPeakExcelWorkSheet.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isAdminExist() {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            return !entityManager.createNamedQuery("operator.admin.exist")
                    .setParameter("role",UserRole.ADMIN)
                    .getResultList()
                    .isEmpty();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Status reload(ReloadType reloadType){
        Reloader r =(Reloader)IOCContainer.getBean("reloader");
        Status reloadStatus = r.reload(reloadType);
        if(reloadStatus==Status.OK){
            return Status.OK;
        }else{
            return Status.RELOAD_NOT_OCCURRED;
        }
    }
}