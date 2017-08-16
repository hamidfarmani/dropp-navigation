package service;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import model.entity.persistent.Driver;
import model.entity.persistent.Operator;
import model.entity.persistent.ServiceProvider;
import model.enums.AccountState;
import model.enums.Status;
import model.enums.UserRole;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.transaction.annotation.Transactional;
import util.IOCContainer;
import util.LocalEntityManagerFactory;
import util.converter.AccountStateConverter;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@org.springframework.stereotype.Service("providerService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
@Transactional
public class ProviderServiceImpl implements ProviderService {


    public ProviderServiceImpl() {

    }

    public Status payment(String username,String providerUsername) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", providerUsername)
                    .getSingleResult();
            if(operator.getRole()!= UserRole.PROVIDER){
                return Status.NOT_FOUND;
            }
            if(operator.getServiceProvider()==null){
                return Status.SERVICE_PROVIDER_NOT_REGISTERED;
            }
            Driver driver = (Driver) entityManager.createNamedQuery("driver.provider.username")
                    .setParameter("providerID",operator.getServiceProvider().getId())
                    .setParameter("username",username)
                    .setMaxResults(1).getSingleResult();
            if(driver.getCredit()>=0){
                return Status.NO_POSSIBILITY_FOR_PAYMENT;
            }
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

    public Object calculateClaim(String providerUsername) {
        JSONObject jsonObjectResponse = new JSONObject();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", providerUsername)
                    .getSingleResult();
            ServiceProvider serviceProvider = (ServiceProvider) entityManager.createNamedQuery("serviceProvider.findby.id")
                    .setParameter("id",operator.getServiceProvider().getId())
                    .getSingleResult();
            entityManager.getTransaction().commit();
            Long driversClaim = serviceProvider.getDriversClaim();
            Long totalClaim = serviceProvider.getTotalClaim();
            Long rem = totalClaim - driversClaim;
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

    public Object mostDebtDrivers(String providerUsername) {
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray drivers = new JSONArray();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", providerUsername)
                    .getSingleResult();
            List<Driver> driversDebt = entityManager.createNamedQuery("driver.orderby.credit")
                    .setParameter("providerID",operator.getServiceProvider().getId())
                    .setMaxResults(10)
                    .getResultList();
            for (Driver driver : driversDebt) {
                JSONObject d = new JSONObject();
                d.put("username", driver.getUsername());
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

    public Object customDebtDrivers(String providerUsername, Long value) {
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray drivers = new JSONArray();
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", providerUsername)
                    .getSingleResult();

            List<Driver> driversDebt = entityManager.createNamedQuery("driver.orderby.gt.creditAndProviderID")
                    .setParameter("providerID", operator.getServiceProvider().getId())
                    .setParameter("value",value)
                    .getResultList();


            for (Driver driver : driversDebt) {
                JSONObject d = new JSONObject();
                d.put("username", driver.getUsername());
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

    public Status banDriver(String providerUsername, String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", providerUsername)
                    .getSingleResult();

            Driver driver = (Driver) entityManager.createNamedQuery("driver.searchExact.usernameAndProviderID")
                    .setParameter("providerID", operator.getServiceProvider().getId())
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

    public Status unBanDriver(String providerUsername, String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", providerUsername)
                    .getSingleResult();

            Driver driver = (Driver) entityManager.createNamedQuery("driver.searchExact.usernameAndProviderID")
                    .setParameter("providerID", operator.getServiceProvider().getId())
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

    public Status deactiveDriver(String providerUsername, String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();

            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", providerUsername)
                    .getSingleResult();


            Driver driver = (Driver) entityManager.createNamedQuery("driver.searchExact.usernameAndProviderID")
                    .setParameter("providerID", operator.getServiceProvider().getId())
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

    public Status banDriverByCredit(String providerUsername, Long value) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", providerUsername)
                    .getSingleResult();

            List<Driver> driversDebt = entityManager.createNamedQuery("driver.orderby.gt.creditAndProviderID")
                    .setParameter("providerID",operator.getServiceProvider().getId())
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

    public Object viewDriverOfProvider(String providerUsername,String q,int count,int pageIndex) {
        JSONObject jsonObjectResponse = new JSONObject();
        JSONArray drivers = new JSONArray();
        List<Driver> driverList = null;
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", providerUsername)
                    .getSingleResult();
            if(operator.getRole()!=UserRole.PROVIDER){
                return Status.NOT_FOUND;
            }
            if(operator.getServiceProvider()==null){
                return Status.SERVICE_PROVIDER_NOT_REGISTERED;
            }
            if (count == -1) {
                driverList = entityManager.createNamedQuery("driver.findBy.providerID.searchLike")
                        .setParameter("providerID",operator.getServiceProvider().getId())
                        .setParameter("input", q + "%")
                        .getResultList();
            } else {
                driverList = entityManager.createNamedQuery("driver.findBy.providerID.searchLike")
                        .setParameter("providerID",operator.getServiceProvider().getId())
                        .setParameter("input", q + "%")
                        .setFirstResult(count * pageIndex)
                        .setMaxResults(count)
                        .getResultList();
            }
            AccountStateConverter accountStateConverter = (AccountStateConverter) IOCContainer.getBean("accountStateConverter");

            for (Driver driver : driverList) {
                JSONObject d = new JSONObject();
                d.put("credit", driver.getCredit());
                d.put("username", driver.getUsername());
                d.put("accountState", accountStateConverter.convertToDatabaseColumn(driver.getAccountState()));
                drivers.put(d);
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

    public void driverOfProviderReport(HttpServletResponse resp,String providerUsername) {
        List<Driver> driverList = null;
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", providerUsername)
                    .getSingleResult();

            driverList = entityManager.createNamedQuery("driver.findBy.providerID")
                    .setParameter("providerID", operator.getServiceProvider().getId())
                    .getResultList();
            entityManager.getTransaction().commit();
        } catch (RollbackException e) {
            e.printStackTrace();
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
        WritableWorkbook driverOfProviderExcelWorkSheet = null;
        try {
            Long mostDebt = Long.valueOf(0);
            String mostDebtor = null;
            driverOfProviderExcelWorkSheet = Workbook.createWorkbook(resp.getOutputStream());

            WritableSheet excelSheet = driverOfProviderExcelWorkSheet.createSheet("رانندگان", 0);

            Label label = new Label(0, 4, "نام کاربری");
            excelSheet.addCell(label);
            label = new Label(1, 4, "شماره تلفن همراه");
            excelSheet.addCell(label);
            label = new Label(2, 4, "موجودی حساب");
            excelSheet.addCell(label);
            label = new Label(3, 4, "وضعیت حساب");
            excelSheet.addCell(label);
            for(int i=0;i<driverList.size();i++){
                Driver driver = driverList.get(i);
                if(driver.getCredit()<mostDebt){
                    mostDebt = driver.getCredit();
                    mostDebtor = driver.getUsername();
                }
                label = new Label(0, i+5, driver.getUsername());
                excelSheet.addCell(label);
                label = new Label(1, i+5, driver.getPhoneNumber());
                excelSheet.addCell(label);
                label = new Label(2, i+5, String.valueOf(driver.getCredit()));
                excelSheet.addCell(label);
                label = new Label(3, i+5, String.valueOf(driver.getAccountState()));
                excelSheet.addCell(label);

            }
            label = new Label(0, 0, "بیشترین بدهی");
            excelSheet.addCell(label);
            label = new Label(1, 0, "نام کاربری بدهکار");
            excelSheet.addCell(label);
            label = new Label(2, 0, "تعداد کل رانندگان");
            excelSheet.addCell(label);



            label = new Label(0, 1, String.valueOf(mostDebt));
            excelSheet.addCell(label);
            label = new Label(1, 1, String.valueOf(mostDebtor));
            excelSheet.addCell(label);
            label = new Label(2, 1, String.valueOf(driverList.size()));
            excelSheet.addCell(label);

            driverOfProviderExcelWorkSheet.write();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {
            if (driverOfProviderExcelWorkSheet != null) {
                try {
                    driverOfProviderExcelWorkSheet.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void providerClaim(HttpServletResponse resp,String providerUsername) {
        List<Driver> driverList = null;
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        Long driversClaim = Long.valueOf(0),totalClaim= Long.valueOf(0);
        ServiceProvider serviceProvider = null;
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", providerUsername)
                    .getSingleResult();
            serviceProvider = operator.getServiceProvider();
            if(serviceProvider!=null) {
                driversClaim = serviceProvider.getDriversClaim();
                totalClaim = serviceProvider.getTotalClaim();
                driverList = entityManager.createNamedQuery("driver.orderby.credit")
                        .setParameter("providerID", serviceProvider.getId())
                        .getResultList();
            }
            entityManager.getTransaction().commit();
        } catch (RollbackException e) {
            e.printStackTrace();
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
        if(serviceProvider!=null) {
            WritableWorkbook providerClaimExcelWorkSheet = null;
            try {
                Long mostDebt = Long.valueOf(0);
                String mostDebtor = null;
                providerClaimExcelWorkSheet = Workbook.createWorkbook(resp.getOutputStream());

                WritableSheet excelSheet = providerClaimExcelWorkSheet.createSheet("بدهی", 0);

                Label label = new Label(0, 4, "نام کاربری");
                excelSheet.addCell(label);
                label = new Label(1, 4, "شماره تلفن همراه");
                excelSheet.addCell(label);
                label = new Label(2, 4, "موجودی حساب");
                excelSheet.addCell(label);
                label = new Label(3, 4, "وضعیت حساب");
                excelSheet.addCell(label);

                for (int i = 0; i < driverList.size(); i++) {
                    Driver driver = driverList.get(i);
                    if (driver.getCredit() < mostDebt) {
                        mostDebt = driver.getCredit();
                        mostDebtor = driver.getUsername();
                    }
                    label = new Label(0, i + 5, driver.getUsername());
                    excelSheet.addCell(label);
                    label = new Label(1, i + 5, driver.getPhoneNumber());
                    excelSheet.addCell(label);
                    label = new Label(2, i + 5, String.valueOf(driver.getCredit()));
                    excelSheet.addCell(label);
                    label = new Label(3, i + 5, String.valueOf(driver.getAccountState()));
                    excelSheet.addCell(label);

                }
                label = new Label(0, 0, "بیشترین بدهی");
                excelSheet.addCell(label);
                label = new Label(1, 0, "نام کاربری بدهکار بیشترین مبلغ");
                excelSheet.addCell(label);
                label = new Label(2, 0, "تعداد رانندگان بدهکار");
                excelSheet.addCell(label);
                label = new Label(3, 0, "کل بدهی رانندگان");
                excelSheet.addCell(label);
                if (totalClaim - driversClaim >= 0) {
                    label = new Label(4, 0, "بدهی گنو");
                    excelSheet.addCell(label);
                } else {
                    label = new Label(4, 0, "طلب گنو");
                    excelSheet.addCell(label);
                }


                label = new Label(0, 1, String.valueOf(mostDebt));
                excelSheet.addCell(label);
                label = new Label(1, 1, String.valueOf(mostDebtor));
                excelSheet.addCell(label);
                label = new Label(2, 1, String.valueOf(driverList.size()));
                excelSheet.addCell(label);
                label = new Label(3, 1, String.valueOf(driversClaim));
                excelSheet.addCell(label);
                label = new Label(4, 1, String.valueOf(Math.abs(totalClaim - driversClaim)));
                excelSheet.addCell(label);

                providerClaimExcelWorkSheet.write();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            } finally {
                if (providerClaimExcelWorkSheet != null) {
                    try {
                        providerClaimExcelWorkSheet.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}