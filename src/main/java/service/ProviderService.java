package service;

import model.enums.Status;

import javax.servlet.http.HttpServletResponse;

public interface ProviderService {

    Status payment(String username,String providerUsername);

    Object calculateClaim(String providerUsername);

    Object mostDebtDrivers(String providerUsername);

    Object customDebtDrivers(String providerUsername, Long value);

    Status banDriver(String providerUsername, String username);

    Status deactiveDriver(String providerUsername, String username);

    Status banDriverByCredit(String providerUsername, Long value);

    Object viewDriverOfProvider(String providerUsername,String q,int count,int pageIndex);

    void driverOfProviderReport(HttpServletResponse resp,String providerUsername);

    void providerClaim(HttpServletResponse resp,String providerUsername);
}


