package model.enums;

/**
 * Created by kasra on 2/22/2017.
 */
public enum AccountState {

    REGISTERED(0,Status.USER_NOT_ACTIVATED),
    ACTIVATED(1,Status.INCOMPLETE_REGISTERATION),
    READY_TO_VERIFY(2,Status.NEEDS_VERIFYING),
    VERIFIED(3,Status.OK),
    BANNED(-1,Status.USER_BANNED),
    DEACTIVATE(-2,Status.USER_DEACTIVATED);

    private int stateCode;
    private Status status;

    AccountState(int stateCode, Status status) {
        this.stateCode = stateCode;
        this.status = status;
    }

    public int getStateCode() {
        return stateCode;
    }

    public Status getStatus() {
        return status;
    }
}