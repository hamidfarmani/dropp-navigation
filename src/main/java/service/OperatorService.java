package service;

import model.entity.persistent.Address;
import model.enums.FileTypeState;
import model.enums.Status;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


public interface OperatorService {


    Object subscribeRegister(String firstName, String lastName, String phoneNumber, Address address, String city);

    Object operatorLogin(String username, String password,String ip);

    Status confirmDriver(String username,String providerID,String operatorUsername);

    JSONObject viewNumberOfAllDrivers();

    JSONObject viewOnlineAllDrivers();

    JSONObject viewNumberOfOnlineDrivers();

    JSONObject viewLowRateDrivers();

    Object searchDriver(String q,int count, int pageIndex);

    Object searchPassenger(String q,int count, int pageIndex);

    Status organizationRegister(String name, String username, String password, String email, String phoneNumber, String workNumber,int employeeCount);

    Status banDriver(String username);

    Status unBanDriver(String username);

    Status banPassenger(String username);

    Status unBanPassenger(String username);

    Object viewBanDrivers();

    Object viewBanPassengers();

    Object searchOrganization(String q,int count, int pageIndex);

    Object viewOrganization(String username);

    Status confirmOrganization(String username);

    Status removeOrganization(String username);

    Object viewDriverCredit(String username);

    Object viewAllDriverCredit();

    Object viewPassengersTicket(String username);

    Status sendMessage(String username, String message, String operatorUsername);

    Status resolveTicket(Long ticketID);

    Object viewTickets();

    boolean isSubscribePhoneNumberExist(String phoneNumber);

    Object searchSubscribe(String q,int count, int pageIndex);

    Object viewOnlineTrips();

    Object viewNumberOfPassengers();

    Object viewNumberOfNewPassengers();

    Object viewNumberOfOnlineTrips();

    Object viewNumberOfTrips();

    Object viewNumberOfOrganization();

    Object viewNumberOfNewOrganization();

    Status paymentRequestRegister(String driverUsername);

    Object viewAllStates();

    Object viewCityByStateID(Long stateID);

    Object viewAllVouchers();

    File downloadFile(String driverUsername,FileTypeState fileTypeState);

    Status uploadFile(MultipartFile file, String driverUsername,FileTypeState fileTypeState);

    Object searchTrip(String q,int count, int pageIndex);

    boolean isOrganizationPhoneNumberExist(String phoneNumber);

    boolean isOrganizationUsernameExist(String username);

    Object viewProviders();

    Status changeOperatorPassword(String logedInOperatorUsername, String newPass);
}
