package model.enums;

/**
 * Created by kasra on 3/8/2017.
 */
public enum TripState {
    REQUESTED,
    ACCEPTED,
    STARTED,
    ARRIVED,
    BOARDED,
    CANCELED_BY_PASSENGER,
    CANCELED_BY_DRIVER,
    CANCELED_BY_OPERATOR,
    NOBODY_ACCEPTED,
    COULD_NOT_CALCULATE_COST,
    UNKNOWN_STATE,
    COMPLETED
}
