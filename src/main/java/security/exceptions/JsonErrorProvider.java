package security.exceptions;

import model.enums.Status;
import org.json.JSONObject;

/**
 * Created by kasra on 5/29/2017.
 */
public class JsonErrorProvider {

    public static String getJsonError(Status status) {
        JSONObject jsonObject = new JSONObject()
                .put("statusCode", status.getStatusCode())
                .put("status", status.getStatusDesc());
        return jsonObject.toString();
    }
}
