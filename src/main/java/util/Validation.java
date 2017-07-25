package util;


/**
 * Created by kasra on 2/14/2017.
 */
public class Validation {
private static Validation ourInstance = new Validation();

    private Validation() {
    }

    public static Validation getInstance() {
        return ourInstance;
    }

    public boolean validateUsername(String username) {
        return !(username.trim().length() < 3 || Character.isDigit(username.trim().charAt(0)));
    }

    public boolean validatePassword(String password) {
        return password.trim().length() >= 5;
    }

    public boolean validatePhoneNumber(String phoneNumber) {
        return phoneNumber.matches(Provider.getInstance().getPhoneNumberRegEx());
    }
}
