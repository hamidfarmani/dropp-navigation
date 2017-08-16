package service;

import model.entity.persistent.State;
import model.enums.City;
import model.enums.Gender;
import model.enums.ServiceType;
import model.enums.Status;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public interface AdminService {

    Object adminLogin(String username, String password);

    Status masterRegister(String creatorUsername, String firstname, String lastname, Date birthDate, String email, String PhoneNumber, String workNumber, String username, String password, Gender gender, City city);

    Status adminRegister(String firstname, String lastname, Date birthDate, String email, String PhoneNumber, String workNumber, String username, String password, Gender gender, City city);

    Status operatorRemove(Long operatorID);

    boolean isUsernameExist(String username);

    State isStateExist(String name);

    model.entity.persistent.City isCityExist(String name, Long stateID);

    boolean isPhoneNumberExist(String phoneNumber);

    Status serviceTypeRegister(City city, ServiceType type);

    JSONObject viewActiveServices(City city);

    Object viewSearchRadius();

    Object viewSearchRadiusByServiceType(ServiceType serviceType);

    Status radiusRegister(double radius,ServiceType serviceType);

    Status radiusUpdate(double radius,ServiceType serviceType);

    Status tariffRegister(City city, double before2KM, double after2KM, double perMin, double waitingMin, double entrance, ServiceType serviceType, int twoWayCost, int coShare);

    Status tariffUpdate(Long tariffID, double before2KM, double after2KM, double perMin, double waitingMin, double entrance, int twoWayCost,int coShare);

    Object searchTariff(String cityName);

    Status banOperator(String username);

    Status unBanOperator(String username);

    Status disableService(Long serviceID);

    Status enableService(Long serviceID);

    Object viewBugs();

    Status resolveBug(Long bugID);

    Status settingUpdate(Boolean smsSender, Boolean emailSender,Boolean dailySmsReport,Boolean weeklySmsReport,
                         Boolean dailyEmailReport,Boolean weeklyEmailReport,Boolean monthlyEmailReport,Boolean exceptionOccurrenceSms,Boolean exceptionOccurrenceEmail,
                         Boolean IOSUpdate,Boolean IOSUpdateCritical,Boolean androidUpdate,Boolean androidUpdateCritical,Boolean allowCompetitors);

    Object viewSystemSetting();

    Status stateRegister(String name);

    Status cityRegister(String name, Long stateID);

    void driversAgeReport(HttpServletResponse resp);

    void passengersAgeReport(HttpServletResponse resp);

    void operatorsAgeReport(HttpServletResponse resp);

    void tripsReport(HttpServletResponse resp,Date startDate, Date endDate);

    Status insertProvider(String name);

    void OSReport(HttpServletResponse resp);

    void costTripsReport(HttpServletResponse resp);

    void providersClaimReport(HttpServletResponse resp);

    boolean isAdminExist();
}


