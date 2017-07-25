package model.enums;

/**
 * Created by kasra on 4/7/2017.
 */
public enum FCMMessageDataTypes {
    REQUESTED_SERVICE_ACCEPTED(1,"requestedServiceAccepted"),
    REQUESTED_SERVICE_DETAILS(2, "requestedServiceDetails"),
    TRIP_STARTED(3,"tripStarted"),
    DRIVER_ARRIVED(4,"driverArrived"),
    PASSENGER_BOARDED(5,"passengerBoarded"),
    TRIP_COMPLETED(6,"tripCompleted"),
    TRIP_CANCELLED_BY_PASSENGER(7,"tripCancelledByPassenger");


    int dataTypeCode;
    String dataTypeDesc;

    FCMMessageDataTypes(int dataTypeCode, String dataTypeDesc) {
        this.dataTypeCode = dataTypeCode;
        this.dataTypeDesc = dataTypeDesc;
    }

    public int getDataTypeCode() {
        return dataTypeCode;
    }

    public String getDataTypeDesc() {
        return dataTypeDesc;
    }
}
