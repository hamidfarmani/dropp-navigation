package util;

/**
 * Created by kasra on 2/16/2017.
 */
public class Messages {
    private static Messages ourInstance = new Messages();
    private String verificationMessage = "<گنو>"+"\n"+"کدفعالسازی شما :"+" %s";

    private Messages() {
    }

    public static Messages getInstance() {
        return ourInstance;
    }

    public String getVerificationMessage() {
        return verificationMessage;
    }
}
