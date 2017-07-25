package service;

import model.entity.persistent.Car;
import model.entity.persistent.CarManufacture;
import model.entity.persistent.Operator;
import model.enums.AccountState;
import model.enums.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import security.model.AccountCredentials;
import util.EncoderUtil;
import util.IOCContainer;
import util.LocalEntityManagerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import java.util.List;

/**
 * Created by kasra on 2/11/2017.
 */

@Service("copService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
@Transactional
public class COPServiceImpl implements COPService {

    private EntityManager entityManager;

    public COPServiceImpl() {

    }

    public Object copLogin(String username, String password){
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
            return new AccountCredentials(operator.getUsername(), "C");
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

    public Status carRegister(String name, Long manufactureID) {
        try {
            entityManager = LocalEntityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            List<Car> c =  entityManager.createNamedQuery("cars.findBy.name")
                    .setParameter("name",name)
                    .getResultList();
            if(c.size()!=0){
                return Status.CAR_EXIST;
            }
            Car car = (Car) IOCContainer.getBean("car");
            car.setName(name);
            CarManufacture carManufacture = entityManager.find(CarManufacture.class,manufactureID);

            if(carManufacture!=null) {
                car.setCarManufacture(carManufacture);
                entityManager.persist(car);
                entityManager.getTransaction().commit();
                return Status.OK;
            }else{
                return Status.NOT_FOUND;
            }
        } catch (NoResultException e){
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

    public Status manufactureRegister(String name) {
        entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            List<CarManufacture> man = entityManager.createNamedQuery("manufacture.findBy.name")
                    .setParameter("name",name)
                    .getResultList();
            if(man.size()==0) {
                CarManufacture manufacture = (CarManufacture) IOCContainer.getBean("carManufacture");
                manufacture.setName(name);
                entityManager.persist(manufacture);
                entityManager.getTransaction().commit();
                return Status.OK;
            }else{
                return Status.MANUFACTURE_EXIST;
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

    public JSONObject carDistance(Long vehicleID){
        entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Long sum;
            sum = (Long)entityManager.createNamedQuery("trip.distance.findBy.vehicleID")
                    .setParameter("vehicleID",vehicleID)
                    .getSingleResult();
            if(sum==null){
                sum = Long.valueOf(0);
            }
            entityManager.getTransaction().commit();
            JSONObject j = new JSONObject();
            j.put("sum",sum);
            return j;
        } catch (NoResultException e){
            e.printStackTrace();
            return null;
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

    public Object getAllManufactures(){
        entityManager = LocalEntityManagerFactory.createEntityManager();
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray allManufacture = new JSONArray();
        try {
            entityManager.getTransaction().begin();
            List<CarManufacture> m =  entityManager.createNamedQuery("find.all.manufactures").getResultList();

            for(int i=0;i<m.size();i++){
                JSONObject manufacture = new JSONObject();
                manufacture.put("id",m.get(i).getId());
                manufacture.put("name",m.get(i).getName());
                allManufacture.put(manufacture);
            }
            jsonObjectResponse.put("All Manufactures" , allManufacture);
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
}