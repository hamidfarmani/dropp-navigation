package service;

import model.entity.persistent.Operator;
import model.entity.persistent.ServiceProvider;
import model.entity.persistent.TicketSubject;
import model.entity.persistent.VoucherCode;
import model.enums.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import security.model.AccountCredentials;
import util.EncoderUtil;
import util.Generator;
import util.IOCContainer;
import util.LocalEntityManagerFactory;
import util.converter.AccountStateConverter;
import util.converter.CityConverter;
import util.converter.GenderConverter;
import util.converter.UserRoleConverter;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by kasra on 2/11/2017.
 */

@Service("masterService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
@Transactional
public class MasterServiceImpl implements MasterService {

    public MasterServiceImpl() {

    }

    public Object masterLogin(String username, String password){
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
                if (operator.getAccountState() == AccountState.REGISTERED) {
                    entityManager.getTransaction().commit();
                    return Status.USER_NOT_ACTIVATED;
                }

            } catch (NoResultException e) {
                entityManager.getTransaction().commit();
                return Status.NOT_FOUND;
            }

            entityManager.persist(operator);
            entityManager.getTransaction().commit();
            return new AccountCredentials(operator.getUsername(), "M");
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

    public Status operatorRegister(String creatorUsername,String firstname, String lastname, Date birthDate,String email, String PhoneNumber, String workNumber, String username, String password, Gender gender, City city, Long providerID) {
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
            operator.setAccountState(AccountState.REGISTERED);
            operator.setRegistrationTimestamp(new Timestamp(System.currentTimeMillis()));
            if(providerID==null) {
                operator.setRole(UserRole.OPERATOR);
            }else{
                ServiceProvider serviceProvider = (ServiceProvider) entityManager.createNamedQuery("serviceProvider.findby.id")
                        .setParameter("id",providerID)
                        .getSingleResult();
                operator.setServiceProvider(serviceProvider);
                operator.setRole(UserRole.PROVIDER);
            }
            entityManager.persist(operator);
            entityManager.getTransaction().commit();

            return Status.OK;
        } catch (NoResultException e) {
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

    public Status operatorUpdate(Long id, String firstname, String lastname, Date birthDate,String email, String PhoneNumber, String workNumber, String password, Gender gender, City city) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();

            Operator operator = (Operator)entityManager.createNamedQuery("operator.findBy.id")
                    .setParameter("id",id)
                    .getSingleResult();

            if(firstname!=null) {
                operator.setFirstName(firstname);
            }
            if(lastname!=null) {
                operator.setLastName(lastname);
            }
            if(password!=null) {
                String hashedPassword = EncoderUtil.getSHA512Hash(password).toLowerCase();
                operator.setPassword(hashedPassword);
            }
            if(PhoneNumber!=null) {
                operator.setPhoneNumber(PhoneNumber);
            }
            if(birthDate!=null) {
                operator.setBirthDate(birthDate);
            }
            if(workNumber!=null) {
                operator.setWorkNumber(workNumber);
            }
            if(city!=null) {
                operator.setCity(city);
            }
            if(email!=null) {
                operator.setEmail(email);
            }
            if(gender!=null) {
                operator.setGender(gender);
            }
            operator.setAccountState(AccountState.VERIFIED);
            operator.setLoggedIn(false);

            entityManager.getTransaction().commit();

            return Status.OK;
        } catch (NoResultException e) {
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

    public Status operatorRemove(Long operatorID) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.findBy.id")
                    .setParameter("id",operatorID)
                    .setMaxResults(1).getSingleResult();
            if(operator!=null && operator.getRole()==UserRole.OPERATOR){
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
            return Status.UNKNOWN_ERROR;
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

    public Operator isPhoneNumberExist(String phoneNumber) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        Operator op = null;
        try {
            entityManager.getTransaction().begin();
            op = (Operator) entityManager.createNamedQuery("operator.phoneNumber.exist")
                    .setParameter("phoneNumber", phoneNumber)
                    .getSingleResult();
            return op;
        }catch (NoResultException e){
            e.printStackTrace();
            return op;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }

    public boolean isSubjectExist(String subject,UserRole role) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            return !entityManager.createNamedQuery("ticketSubject.subject.exist")
                    .setParameter("subject", subject)
                    .setParameter("role",role)
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

    public Status ticketSubjectRegister(String subject, String parentID, UserRole role){
        TicketSubject parent = null;
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            TicketSubject ticketSubject = (TicketSubject) IOCContainer.getBean("ticketSubject");
            ticketSubject.setSubject(subject);
            if(!parentID.isEmpty()) {
                parent = (TicketSubject) entityManager.createNamedQuery("ticketSubject.findby.id")
                        .setParameter("id", Long.valueOf(parentID))
                        .getSingleResult();
                if(parent.getRole()!=role) {
                    return Status.ROLE_MISMATCH;
                }
            }
            ticketSubject.setRole(role);
            entityManager.persist(ticketSubject);

            if(!parentID.isEmpty()) {
                List<TicketSubject> ticketSubjects = parent.getTicketSubjects();
                ticketSubjects.add(ticketSubject);
                parent.setTicketSubjects(ticketSubjects);
            }

            entityManager.getTransaction().commit();

            return Status.OK;
        }catch(NoResultException e){
            e.printStackTrace();
            return Status.NOT_FOUND;
        }catch (RollbackException e) {
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

    public Object viewAllTicketSubjects(){
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray subjects = new JSONArray();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            List<TicketSubject> subjectsList =  entityManager.createNamedQuery("ticketSubject.All")
                    .getResultList();
            UserRoleConverter userRoleConverter = (UserRoleConverter)IOCContainer.getBean("userRoleConverter");
            for(TicketSubject ticketSubject : subjectsList){
                JSONObject t = new JSONObject();
                JSONArray parents = new JSONArray();
                t.put("id", ticketSubject.getId());
                t.put("subject", ticketSubject.getSubject());
                t.put("role", userRoleConverter.convertToDatabaseColumn(ticketSubject.getRole()));
                if(ticketSubject.getTicketSubjects()!=null) {
                    for(TicketSubject t1: ticketSubject.getTicketSubjects()) {
                        JSONObject parentJSON = new JSONObject();
                        parentJSON.put("id", t1.getId());
                        parentJSON.put("subject", t1.getSubject());
                        parentJSON.put("role", userRoleConverter.convertToDatabaseColumn(t1.getRole()));
                        parents.put(parentJSON);
                    }
                }
                t.put("subTicketSubjects",parents);
                subjects.put(t);
            }
            jsonObjectResponse.put("primarySubjects", subjects);
            entityManager.getTransaction().commit();
            return jsonObjectResponse;
        }catch(NoResultException e){
            e.printStackTrace();
            return Status.NOT_FOUND;
        }catch (RollbackException e) {
            e.printStackTrace();
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

    public Object voucherRegister(int maxUse, String description, Date startDate, Date endDate, VoucherCodeGenerationType generationType, VoucherCodeType codeType, String value,String code){
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        VoucherCode voucherCode = (VoucherCode) IOCContainer.getBean("voucherCode");
        String voucher = "";
        if(generationType==VoucherCodeGenerationType.AUTOMATIC) {
            Generator g = new Generator();
            voucher = g.generateRandomCode(8);
        }else{
            voucher=code;
        }

        try {
            entityManager.getTransaction().begin();
            voucherCode.setCode(voucher);
            voucherCode.setCreationDate(new Date());
            voucherCode.setType(codeType);
            voucherCode.setGenerationType(generationType);
            voucherCode.setDiscountValue(value);
//            voucherCode.setOperator();
            voucherCode.setStartDate(startDate);
            voucherCode.setExpireDate(endDate);
            voucherCode.setDescription(description);
            voucherCode.setMaxUses(maxUse);
            voucherCode.setUses(0);
            entityManager.persist(voucherCode);
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
        jsonObjectResponse.put("voucher", voucher);
        return jsonObjectResponse;
    }

    public Status voucherUpdate(Long id, int maxUse, String description, Date startDate, Date endDate,  VoucherCodeType codeType, String value){
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        VoucherCode voucherCode = entityManager.find(VoucherCode.class,id);

        try {
            entityManager.getTransaction().begin();
            voucherCode.setType(codeType);
            voucherCode.setDiscountValue(value);
            voucherCode.setStartDate(startDate);
            voucherCode.setExpireDate(endDate);
            voucherCode.setDescription(description);
            voucherCode.setMaxUses(maxUse);
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
        return Status.OK;
    }

    public Status banOperator(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", username)
                    .getSingleResult();
            if(operator.getRole()== UserRole.OPERATOR || operator.getRole()== UserRole.PROVIDER) {
                operator.setAccountState(AccountState.BANNED);
                entityManager.getTransaction().commit();
                return Status.OK;
            }else{
                return Status.NOT_FOUND;
            }
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
            if(operator.getRole()== UserRole.OPERATOR || operator.getRole()== UserRole.PROVIDER) {
                operator.setAccountState(AccountState.VERIFIED);
                entityManager.getTransaction().commit();
                return Status.OK;
            }else{
                return Status.NOT_FOUND;
            }
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

    public Object searchOperators(String query, int count, int pageIndex,String operatorUsername){
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray operatorJsonArray = new JSONArray();
        UserRoleConverter userRoleConverter = (UserRoleConverter) IOCContainer.getBean("userRoleConverter");
        GenderConverter genderConverter = (GenderConverter) IOCContainer.getBean("genderConverter");
        CityConverter cityConverter = (CityConverter) IOCContainer.getBean("cityConverter");
        AccountStateConverter accountStateConverter = (AccountStateConverter) IOCContainer.getBean("accountStateConverter");
        try {
            entityManager.getTransaction().begin();
            Operator op = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username",operatorUsername)
                    .getSingleResult();
            List<Operator> operators;
            if(count==-1){
                operators = entityManager.createNamedQuery("operator.like")
                        .setParameter("input", query + "%")
                        .getResultList();
            }else {
                operators = entityManager.createNamedQuery("operator.like")
                        .setParameter("input", query + "%")
                        .setFirstResult(count * pageIndex)
                        .setMaxResults(count)
                        .getResultList();
            }
            entityManager.getTransaction().commit();
            for(Operator o : operators) {
                if(op.getRole()==UserRole.MASTER_OPERATOR
                        && (o.getRole()==UserRole.CAR_OPERATOR || o.getRole()==UserRole.ADMIN || o.getRole()==UserRole.MASTER_OPERATOR)){
                    continue;
                }
                JSONObject operatorJsonObject = new JSONObject();
                operatorJsonObject.put("id",o.getoId());
                operatorJsonObject.put("firstName",o.getFirstName());
                operatorJsonObject.put("lastName",o.getLastName());
                operatorJsonObject.put("username",o.getUsername());
                operatorJsonObject.put("accountState",accountStateConverter.convertToDatabaseColumn(o.getAccountState()));
                operatorJsonObject.put("phoneNumber",o.getPhoneNumber());
                operatorJsonObject.put("workNumber",o.getWorkNumber());
                operatorJsonObject.put("birthDate",o.getBirthDate());
                operatorJsonObject.put("city",cityConverter.convertToDatabaseColumn(o.getCity()));
                operatorJsonObject.put("gender",genderConverter.convertToDatabaseColumn(o.getGender()));
                operatorJsonObject.put("email",o.getEmail());
                operatorJsonObject.put("role",userRoleConverter.convertToDatabaseColumn(o.getRole()));
                operatorJsonArray.put(operatorJsonObject);
            }
            jsonObjectResponse.put("operators", operatorJsonArray);
            return jsonObjectResponse;
        } catch (NoResultException e){
            e.printStackTrace();
            return  Status.NOT_FOUND;
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

    public boolean isVoucherCodeExist(String code) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            return !entityManager.createNamedQuery("voucherCode.findBy.code")
                    .setParameter("code", code)
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

}