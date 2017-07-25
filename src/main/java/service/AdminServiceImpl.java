package service;

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
import util.converter.CityConverter;
import util.converter.ServiceTypeConverter;
import util.converter.TicketStateConverter;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


@org.springframework.stereotype.Service("adminService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
@Transactional
public class AdminServiceImpl implements AdminService {

    private EntityManager entityManager;

    public AdminServiceImpl() {

    }

    public Object adminLogin(String username, String password){
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

    public Status masterRegister(String firstname, String lastname, Date birthDate,String email, String PhoneNumber, String workNumber, String username, String password, Gender gender, City city) {
        entityManager = LocalEntityManagerFactory.createEntityManager();
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
            operator.setAccountState(AccountState.REGISTERED);
            operator.setRegistrationTimestamp(new Timestamp(System.currentTimeMillis()));
            operator.setRole(UserRole.MASTER_OPERATOR);

            entityManager.persist(operator);
            entityManager.getTransaction().commit();

            return Status.OK;
        } catch (RollbackException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return Status.BAD_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public Status serviceTypeRegister(City city, ServiceType serviceType) {
        try {
            entityManager = LocalEntityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Service service = (Service) IOCContainer.getBean("service");
            List<Service> services = entityManager.createNamedQuery("activeServices.find.byCityAndService")
                    .setParameter("city",city)
                    .setParameter("serviceType",serviceType)
                    .getResultList();
            if(services.size()==0) {
                try{
                Tariff tariff = (Tariff)entityManager.createNamedQuery("tariff.findby.cityAndServiceType")
                                        .setParameter("serviceType",serviceType)
                                        .setParameter("city",city)
                                        .getSingleResult();

                    service.setCity(city);
                    service.setServiceState(true);
                    service.setServiceType(serviceType);

                    entityManager.persist(service);
                    entityManager.getTransaction().commit();

                    return Status.OK;
                }catch(NoResultException e){
                    e.printStackTrace();
                    return Status.TARIFF_NOT_REGISTERED;
                }
            }else{
                return Status.SERVICE_TYPE_EXIST;
            }
        } catch (RollbackException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return Status.BAD_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public JSONObject viewActiveServices(City city){
        entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject actives = new JSONObject();
        JSONArray services = new JSONArray();
        try {
            entityManager.getTransaction().begin();
            List activeServices = entityManager.createNamedQuery("activeServices.find.byCity")
                    .setParameter("city", city)
                    .getResultList();
            entityManager.getTransaction().commit();
            for(int i=0;i<activeServices.size();i++) {
                JSONObject ob = new JSONObject();
                Service a = (Service)activeServices.get(i);
                ob.put("id" , a.getId());
                ob.put("type" , a.getServiceType());
                ob.put("status",a.getServiceState());
                services.put(ob);
            }
            actives.put("activeServices", services);
            return actives;
        } catch (NoResultException e){
            return  null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public Status radiusRegister(double radius, ServiceType serviceType){
        entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            SearchRadius searchRadius = (SearchRadius)IOCContainer.getBean("searchRadius");
            List<SearchRadius> sr =  entityManager.createNamedQuery("searchRadius.findBy.serviceType")
                    .setParameter("serviceType", serviceType)
                    .getResultList();
            if(sr.size()==0){
                searchRadius.setRadius(radius);
                searchRadius.setServiceType(serviceType);
                entityManager.persist(searchRadius);
                entityManager.getTransaction().commit();
                return Status.OK;
            }else{
                return Status.SERVICE_TYPE_EXIST;
            }
        }catch (NoResultException e) {
            e.printStackTrace();
            return Status.NOT_FOUND;
        }catch (JSONException e){
            e.printStackTrace();
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
    }

    public Status radiusUpdate(double radius, ServiceType serviceType){
        entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            SearchRadius sr =  (SearchRadius) entityManager.createNamedQuery("searchRadius.findBy.serviceType")
                    .setParameter("serviceType", serviceType)
                    .getSingleResult();
            sr.setRadius(radius);
            entityManager.getTransaction().commit();
            return Status.OK;
        }catch (NoResultException e) {
            e.printStackTrace();
            return Status.NOT_FOUND;
        }catch (JSONException e){
            e.printStackTrace();
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
    }

    public Object viewSearchRadius(){
        entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray types = new JSONArray();
        try {
            entityManager.getTransaction().begin();
            List<SearchRadius> searchRadius = entityManager.createNamedQuery("searchRadius.all")
                    .getResultList();
            for(int i=0;i<searchRadius.size();i++) {
                JSONObject type = new JSONObject();
                type.put("radius", searchRadius.get(i).getRadius());
                type.put("serviceType", ((ServiceTypeConverter)IOCContainer.getBean("serviceTypeConverter")).convertToDatabaseColumn(searchRadius.get(i).getServiceType()));
                types.put(type);
            }
            jsonObjectResponse.put("searchRadius", types);
            entityManager.getTransaction().commit();
        }catch (NoResultException e) {
            e.printStackTrace();
            return Status.NOT_FOUND;
        }catch (JSONException e){
            e.printStackTrace();
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

    public Object viewSearchRadiusByServiceType(ServiceType serviceType){
        entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray types = new JSONArray();
        try {
            entityManager.getTransaction().begin();
            SearchRadius searchRadius = (SearchRadius) entityManager.createNamedQuery("searchRadius.findBy.serviceType")
                    .setParameter("serviceType",serviceType).getSingleResult();
            JSONObject type = new JSONObject();
            type.put("radius", searchRadius.getRadius());
            type.put("serviceType",((ServiceTypeConverter)IOCContainer.getBean("serviceTypeConverter")).convertToDatabaseColumn(searchRadius.getServiceType()));
            types.put(type);
            jsonObjectResponse.put("searchRadius", types);
            entityManager.getTransaction().commit();
        }catch (NoResultException e) {
            e.printStackTrace();
            return Status.NOT_FOUND;
        }catch (JSONException e){
            e.printStackTrace();
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

    public Status operatorRemove(Long operatorID) {
        try {

            entityManager = LocalEntityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.findBy.id")
                    .setParameter("id",operatorID)
                    .setMaxResults(1).getSingleResult();
            if(operator!=null){
                entityManager.remove(operator);
                entityManager.getTransaction().commit();

                return Status.OK;
            }else{
                return Status.NOT_FOUND;
            }

        } catch (RollbackException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return Status.BAD_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
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
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
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
                    .setParameter("role", UserRole.MASTER_OPERATOR)
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

    public Object searchTariff(String cityName){
        entityManager = LocalEntityManagerFactory.createEntityManager();
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
            for(Tariff t : tariffs) {
                JSONObject tariffJsonObject = new JSONObject();
                tariffJsonObject.put("id",t.getId());
                tariffJsonObject.put("serviceType",serviceTypeConverter.convertToDatabaseColumn(t.getServiceType()));
                tariffJsonObject.put("coShare",t.getCoSharePercentage());
                tariffJsonObject.put("city",cityConverter.convertToDatabaseColumn(t.getCity()));
                tariffJsonObject.put("after2KM",t.getCostPerMeterAfter2KM());
                tariffJsonObject.put("before2KM",t.getCostPerMeterBefore2KM());
                tariffJsonObject.put("perMin",t.getCostPerMin());
                tariffJsonObject.put("waitingMin",t.getCostPerWaitingMin());
                tariffJsonObject.put("twoWayCost",t.getTwoWayCostPercentage());
                tariffJsonObject.put("entrance",t.getEntranceCost());
                tariffJsonArray.put(tariffJsonObject);
            }
            jsonObjectResponse.put("tariffs", tariffJsonArray);
            return jsonObjectResponse;
        } catch (NoResultException e){
            return Status.NOT_FOUND;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public Status tariffRegister(City city, double before2KM, double after2KM, double perMin, double waitingMin, double entrance, ServiceType serviceType, int twoWayCost,int coShare) {
        try {
            entityManager = LocalEntityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            List<Tariff> t = entityManager.createNamedQuery("tariff.findby.cityAndServiceType")
                    .setParameter("serviceType",serviceType)
                    .setParameter("city",city)
                    .getResultList();
            if(t.size()==0) {
                Tariff tariff = (Tariff) IOCContainer.getBean("tariff");
                tariff.setCity(city);
                tariff.setCostPerMeterBefore2KM(before2KM);
                tariff.setCostPerMeterAfter2KM(after2KM);
                tariff.setCostPerMin(perMin);
                tariff.setCostPerWaitingMin(waitingMin);
                tariff.setEntranceCost(entrance);
                tariff.setServiceType(serviceType);
                tariff.setTwoWayCostPercentage(twoWayCost);
                tariff.setCoSharePercentage(coShare);

                entityManager.persist(tariff);
                entityManager.getTransaction().commit();
                return Status.OK;
            }else{
                return Status.SERVICE_TYPE_EXIST;
            }

        } catch (NoResultException e){
            return Status.NOT_FOUND;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public Status tariffUpdate(Long tariffID, double before2KM, double after2KM, double perMin, double waitingMin, double entrance,int twoWayCost,int coShare) {
        try {
            entityManager = LocalEntityManagerFactory.createEntityManager();
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
            previousTariff.setCoSharePercentage(coShare);

            entityManager.getTransaction().commit();

            return Status.OK;
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        }catch (NullPointerException e){
            e.printStackTrace();
            return Status.NOT_FOUND;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public Status banOperator(String username) {
        entityManager = LocalEntityManagerFactory.createEntityManager();
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
        entityManager = LocalEntityManagerFactory.createEntityManager();
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

    public Status disableService(Long serviceID){
        entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            model.entity.persistent.Service service = entityManager.find(model.entity.persistent.Service.class,serviceID);
            service.setServiceState(false);
            entityManager.getTransaction().commit();
            return Status.OK;
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        }catch (NullPointerException e){
            e.printStackTrace();
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

    public Status enableService(Long serviceID){
        entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            model.entity.persistent.Service service = entityManager.find(model.entity.persistent.Service.class,serviceID);
            Tariff t = (Tariff) entityManager.createNamedQuery("tariff.findby.cityAndServiceType")
                    .setParameter("city",service.getCity())
                    .setParameter("serviceType",service.getServiceType())
                    .getSingleResult();
            service.setServiceState(true);
            entityManager.getTransaction().commit();
            return Status.OK;
        } catch (NoResultException e) {
            return Status.TARIFF_NOT_REGISTERED;
        }catch (NullPointerException e){
            e.printStackTrace();
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

    public Object viewBugs(){
        entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray bugJsonArray = new JSONArray();
        try {
            entityManager.getTransaction().begin();
            List<Bug> bugs;
            TicketStateConverter ticketStateConverter = (TicketStateConverter)IOCContainer.getBean("ticketStateConverter");
            bugs = entityManager.createNamedQuery("bug.all")
                    .getResultList();

            entityManager.getTransaction().commit();
            for(Bug b : bugs) {
                JSONObject bugJSON = new JSONObject();
                bugJSON.put("id",b.getId());
                bugJSON.put("description",b.getDesc());
                bugJSON.put("state",ticketStateConverter.convertToDatabaseColumn(b.getState()));
                if(b.getDriver()!=null) {
                    bugJSON.put("driverUsername", b.getDriver().getUsername());
                }
                if(b.getPassenger()!=null) {
                    bugJSON.put("passengerUsername", b.getPassenger().getUsername());
                }
                bugJsonArray.put(bugJSON);
            }
            jsonObjectResponse.put("bugs", bugJsonArray);

        } catch (NoResultException e){
            return Status.NOT_FOUND;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
        return jsonObjectResponse;
    }

    public Status resolveBug(Long bugID){
        entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Bug bug = entityManager.find(Bug.class,bugID);
            bug.setState(TicketState.RESOLVED);
            entityManager.getTransaction().commit();
            return Status.OK;
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        }catch (NullPointerException e){
            e.printStackTrace();
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

    public Status settingUpdate(Boolean smsSender, Boolean emailSender,Boolean dailySmsReport,Boolean weeklySmsReport,
                                Boolean dailyEmailReport,Boolean weeklyEmailReport,Boolean monthlyEmailReport,Boolean exceptionOccurrenceSms,Boolean exceptionOccurrenceEmail,
                                Boolean IOSUpdate,Boolean IOSUpdateCritical,Boolean androidUpdate,Boolean androidUpdateCritical,Boolean allowCompetitors){
        try {
            entityManager = LocalEntityManagerFactory.createEntityManager();
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

            entityManager.getTransaction().commit();

            return Status.OK;
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        }catch (NullPointerException e){
            e.printStackTrace();
            return Status.NOT_FOUND;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public Object viewSystemSetting(){
        entityManager = LocalEntityManagerFactory.createEntityManager();
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
        }catch (NoResultException e) {
            e.printStackTrace();
            return Status.NOT_FOUND;
        }catch (JSONException e){
            e.printStackTrace();
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

    public Status stateRegister(String name) {
        entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            State state = (State) IOCContainer.getBean("state");

            state.setName(name);

            entityManager.persist(state);
            entityManager.getTransaction().commit();

            return Status.OK;
        } catch (RollbackException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return Status.BAD_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public Status cityRegister(String name, Long stateID) {
        entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            model.entity.persistent.City city = (model.entity.persistent.City) IOCContainer.getBean("city");
            State state = entityManager.find(State.class,stateID);
            city.setState(state);
            city.setName(name);
            entityManager.persist(city);
            entityManager.getTransaction().commit();

            return Status.OK;
        }catch(NoResultException e){
            e.printStackTrace();
            return Status.NOT_FOUND;
        }catch (NullPointerException e ){
            e.printStackTrace();
            return Status.NOT_FOUND;
        } catch (RollbackException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return Status.BAD_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public State isStateExist(String name){
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        State state = null ;
        try {
            entityManager.getTransaction().begin();
            state = (State) entityManager.createNamedQuery("state.by.name")
                    .setParameter("name", name)
                    .getSingleResult();
            return state;
        }catch (NoResultException e){
            e.printStackTrace();
            return state;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public City isCityExist(String name, Long stateID){
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        City city = null ;
        try {
            entityManager.getTransaction().begin();
            city = (City) entityManager.createNamedQuery("city.by.nameAndStateID")
                    .setParameter("name", name)
                    .setParameter("stateID",stateID)
                    .getSingleResult();
            return city;
        }catch (NoResultException e){
            e.printStackTrace();
            return city;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }
}