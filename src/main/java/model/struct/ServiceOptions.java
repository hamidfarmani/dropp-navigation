package model.struct;

/**
 * Created by kasra on 4/4/2017.
 */
public class ServiceOptions {
    public boolean isReturn;
    public int waitingTimeInMin;

    public ServiceOptions(boolean isReturn, int waitingTimeInMin) {
        this.isReturn = isReturn;
        this.waitingTimeInMin = waitingTimeInMin;
    }
}
