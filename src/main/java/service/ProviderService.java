package service;

import model.enums.Status;

public interface ProviderService {

    Status payment(String username);

    Object viewSearchRadius();

    Object calculateClaim();

    Object driversDebt();

    Object mostDebtDrivers();

    Object customDebtDrivers(Long value);

    Status banDriver(String username);

    Status deactiveDriver(String username);

    Status banDriverByCredit(Long value);

}


