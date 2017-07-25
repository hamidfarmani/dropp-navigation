package util.converter;

import model.enums.TripState;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kasra on 3/8/2017.
 */

@Converter(autoApply = true)
public class TripStateConverter implements AttributeConverter<TripState, Integer> {
    @Override
    public Integer convertToDatabaseColumn(TripState tripState) {
        if (tripState == null) {
            return null;
        }
        switch (tripState) {
            case REQUESTED:
                return 1;
            case ACCEPTED:
                return 2;
            case STARTED:
                return 3;
            case ARRIVED:
                return 4;
            case BOARDED:
                return 5;
            case COMPLETED:
                return 6;
            case CANCELED_BY_PASSENGER:
                return 10;
            case CANCELED_BY_DRIVER:
                return 11;
            case CANCELED_BY_OPERATOR:
                return 12;
            case NOBODY_ACCEPTED:
                return 15;
            case COULD_NOT_CALCULATE_COST:
                return 20;
            case UNKNOWN_STATE:
                return 0;
            default:
                throw new IllegalArgumentException("Unknown " + tripState);
        }
    }

    @Override
    public TripState convertToEntityAttribute(Integer integer) {
        if (integer == null){
            return null;
        }
        switch (integer) {

            case 0:
                return TripState.UNKNOWN_STATE;
            case 1:
                return TripState.REQUESTED;
            case 2:
                return TripState.ACCEPTED;
            case 3:
                return TripState.STARTED;
            case 4:
                return TripState.ARRIVED;
            case 5:
                return TripState.BOARDED;
            case 6:
                return TripState.COMPLETED;
            case 10:
                return TripState.CANCELED_BY_PASSENGER;
            case 11:
                return TripState.CANCELED_BY_DRIVER;
            case 12:
                return TripState.CANCELED_BY_OPERATOR;
            case 15:
                return TripState.NOBODY_ACCEPTED;
            case 20:
                return TripState.COULD_NOT_CALCULATE_COST;
            default:
                throw new IllegalArgumentException("Unknown" + integer);
        }
    }
}
