package service;


import model.enums.Status;

public interface SystemService {

    Object refreshToken(String oldToken);

    Status validateOperatorForNewToken(String username);
}
