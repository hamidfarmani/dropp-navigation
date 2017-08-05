package service;

import model.enums.Status;

public interface ProviderService {

    Status payment(String username,String providerUsername);

    Object viewSearchRadius();

    Object calculateClaim();

    Object driversDebt(String providerUsername);

    Object mostDebtDrivers(String providerUsername);

    Object customDebtDrivers(Long value);

    Status banDriver(String username);

    Status deactiveDriver(String username);

    Status banDriverByCredit(Long value);

}


