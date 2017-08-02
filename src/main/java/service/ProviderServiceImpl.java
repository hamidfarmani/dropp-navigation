package service;

import model.entity.persistent.Driver;
import model.entity.persistent.SearchRadius;
import model.entity.persistent.ServiceProvider;
import model.enums.AccountState;
import model.enums.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.transaction.annotation.Transactional;
import util.IOCContainer;
import util.LocalEntityManagerFactory;
import util.converter.ServiceTypeConverter;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import java.util.List;


@org.springframework.stereotype.Service("providerService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
@Transactional
public class ProviderServiceImpl implements ProviderService {

    private EntityManager entityManager;

    public ProviderServiceImpl() {

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

    public Status payment(String username) {
        try {
            entityManager = LocalEntityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Driver driver = (Driver) entityManager.createNamedQuery("driver.almasDriver.username")
                    .setParameter("username",username)
                    .setMaxResults(1).getSingleResult();
            ServiceProvider serviceProvider = (ServiceProvider) entityManager.createNamedQuery("serviceProvider.findby.id")
                    .setParameter("id",driver.getServiceProvider().getId())
                    .setMaxResults(1).getSingleResult();
            serviceProvider.setDriversClaim(serviceProvider.getDriversClaim()+driver.getCredit()); //driver claim + and driver credit -
            serviceProvider.setTotalClaim(serviceProvider.getTotalClaim()+driver.getCredit());
            driver.setCredit(0);
            entityManager.getTransaction().commit();
            return Status.OK;
        }catch (NoResultException e){
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

    public Object calculateClaim() {
        JSONObject jsonObjectResponse = new JSONObject();
        try {
            entityManager = LocalEntityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            ServiceProvider serviceProvider = (ServiceProvider) entityManager.createNamedQuery("serviceProvider.get.all")
                    .getSingleResult();
            entityManager.getTransaction().commit();
            Long driversClaim = serviceProvider.getDriversClaim();
            Long totalClaim = serviceProvider.getTotalClaim();
            Long rem = driversClaim + totalClaim;
            jsonObjectResponse.put("debt", rem);
            return jsonObjectResponse;

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

    public Object driversDebt() {
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray drivers = new JSONArray();
        try {
            entityManager = LocalEntityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            List<Object[]> driversDebt = entityManager.createNamedQuery("driver.groupby.debt")
                    .getResultList();

            for (Object[] obj : driversDebt) {
                String username = String.valueOf(obj[0]);
//                Long ccID = Long.valueOf(String.valueOf(obj[1]));
                Long sum = (Long) obj[1];
                JSONObject object = new JSONObject();
                object.put("username", username);
//                object.put("ccID", ccID);
                object.put("debt", sum);
                drivers.put(object);
            }
            jsonObjectResponse.put("drivers",drivers);
            entityManager.getTransaction().commit();
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
        return jsonObjectResponse;
    }

    public Object mostDebtDrivers() {
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray drivers = new JSONArray();
        try {
            entityManager = LocalEntityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            List<Driver> driversDebt = entityManager.createNamedQuery("driver.orderby.credit")
                    .setMaxResults(10)
                    .getResultList();


            for (Driver driver : driversDebt) {
                JSONObject d = new JSONObject();
                d.put("firstName", driver.getFirstName());
                d.put("lastName", driver.getLastName());
                d.put("username", driver.getUsername());
                d.put("phoneNumber", driver.getPhoneNumber());
                d.put("credit", driver.getCredit());
                drivers.put(d);
            }
            jsonObjectResponse.put("mostDebts",drivers);
            entityManager.getTransaction().commit();
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
        return jsonObjectResponse;
    }

    public Object customDebtDrivers(Long value) {
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray drivers = new JSONArray();
        try {
            entityManager = LocalEntityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            List<Driver> driversDebt = entityManager.createNamedQuery("driver.orderby.gt.credit")
                    .setParameter("value",value)
                    .getResultList();


            for (Driver driver : driversDebt) {
                JSONObject d = new JSONObject();
                d.put("firstName", driver.getFirstName());
                d.put("lastName", driver.getLastName());
                d.put("username", driver.getUsername());
                d.put("phoneNumber", driver.getPhoneNumber());
                d.put("credit", driver.getCredit());
                drivers.put(d);
            }
            entityManager.getTransaction().commit();
            jsonObjectResponse.put("mostDebts",drivers);
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
        return jsonObjectResponse;
    }

    public Status banDriver(String username) {
        entityManager = LocalEntityManagerFactory.createEntityManager();
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

    public Status deactiveDriver(String username) {
        entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Driver driver = (Driver) entityManager.createNamedQuery("driver.searchExact.username")
                    .setParameter("username", username)
                    .getSingleResult();
            driver.setAccountState(AccountState.DEACTIVATE);
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

    public Status banDriverByCredit(Long value) {
        try {
            entityManager = LocalEntityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            List<Driver> driversDebt = entityManager.createNamedQuery("driver.orderby.gt.credit")
                    .setParameter("value",value)
                    .getResultList();
            for(Driver d : driversDebt){
                d.setAccountState(AccountState.BANNED);
            }
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

}