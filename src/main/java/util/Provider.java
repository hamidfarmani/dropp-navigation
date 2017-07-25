package util;

/**
 * Created by kasra on 2/5/2017.
 */
public class Provider {
    private static Provider ourInstance = new Provider();
    //not share
    private String FCM_SERVER_KEY = "AAAA-YfB9-E:APA91bEIFGwos2bCy_x3tourda31Mbwt-b5XU9m9sg8L-b0uH6zwnshgOQaZf5OYlEaqUABzN6NOsD9udveHm7EgNoH6Exv3L9iOpU0O3kH_on2bFnYEd_yYi4ojEhg-67h56yUFL5j3";
    //
    private String FCM_Sender_ID = "1071724492769";

    private String CILENT_IP = "http://localhost:8080";


    private String KAVENEGAR_API_KEY = "44497732736A707676616C58437154637046736738513D3D";
    //
    private String GOOGLE_MAP_SERVER_API_KEY = "AIzaSyAsleyyiaQxwL2iTjpXqT1PDYdU0kaYv1A";

    private double MAX_DISTANCE_FROM_TAXI_1 = 10000;

    private String phoneNumberRegEx = "(0|\\+98)?([ ]|,|-|[()]){0,2}9[0|1|2|3|4|9]([ ]|,|-|[()]){0,2}(?:[0-9]([ ]|,|-|[()]){0,2}){8}";

    private Provider() {
    }

    public static Provider getInstance() {
        return ourInstance;
    }

    public String getFCM_SERVER_KEY() {
        return FCM_SERVER_KEY;
    }

    public String getFCM_Sender_ID() {
        return FCM_Sender_ID;
    }

    public String getKAVENEGAR_API_KEY() {
        return KAVENEGAR_API_KEY;
    }

    public String getGOOGLE_MAP_SERVER_API_KEY() {
        return GOOGLE_MAP_SERVER_API_KEY;
    }

    public double getMAX_DISTANCE_FROM_TAXI_1() {
        return MAX_DISTANCE_FROM_TAXI_1;
    }

    public String getPhoneNumberRegEx() {
        return phoneNumberRegEx;
    }

    public String getCILENT_IP() {
        return CILENT_IP;
    }
}
