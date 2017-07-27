package service;

import model.entity.persistent.Operator;
import model.enums.*;

import java.util.Date;

public interface MasterService {

    Object masterLogin(String username, String password);

    Status operatorRemove(Long id);

    Status operatorRegister(String firstname, String lastname, Date birthDate, String email, String PhoneNumber, String workNumber, String username, String password, Gender gender, City city);

    Object operatorUpdate(Long id, String firstname, String lastname, Date birthDate, String email, String PhoneNumber, String workNumber, String password, Gender gender, City city);

    boolean isUsernameExist(String username);

    Operator isPhoneNumberExist(String phoneNumber);

    boolean isSubjectExist(String subject, UserRole userRole);

    Status ticketSubjectRegister(String subject,String parentID, UserRole role);

    Object viewAllTicketSubjects();

    Object voucherRegister(int maxUse, String description, Date startDate, Date endDate, VoucherCodeGenerationType generationType, VoucherCodeType codeType, String value,String code);



    Status voucherUpdate(Long id, int maxUse, String description, Date startDate, Date endDate, VoucherCodeType codeType, String value);

    Status banOperator(String username);

    Status unBanOperator(String username);

    Object searchOperators(String query, int count, int pageIndex, String operatorUsername);


}
