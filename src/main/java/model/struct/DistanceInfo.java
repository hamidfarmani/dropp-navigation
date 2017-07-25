package model.struct;

/**
 * Created by kasra on 3/28/2017.
 */
public class DistanceInfo {

    public String originAddress;
    public String destAddress;
    public long ETA;
    public long distanceInMeter;

    public DistanceInfo() {
    }

    public DistanceInfo(String originAddress, String destAddress, long ETA, long distanceInMeter) {
        this.originAddress = originAddress;
        this.destAddress = destAddress;
        this.ETA = ETA;
        this.distanceInMeter = distanceInMeter;
    }
}
