package service;

import model.enums.Status;
import org.json.JSONObject;

public interface COPService {

    Object copLogin(String username, String password);

    Status carRegister(String name, Long manufacture);

    Status manufactureRegister(String name);

    JSONObject carDistance(Long vehicleID);

    Object getAllManufactures();

}
