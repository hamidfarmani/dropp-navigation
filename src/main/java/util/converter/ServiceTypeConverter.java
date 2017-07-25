package util.converter;

import model.enums.ServiceType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kasra on 3/25/2017.
 */

@Converter(autoApply = true)
public class ServiceTypeConverter implements AttributeConverter<ServiceType, String> {

    @Override
    public String convertToDatabaseColumn(ServiceType serviceType) {
        if (serviceType == null) {
            return null;
        }
        switch (serviceType) {
            case TAXI:
                return "T";
            case ECO:
                return "E";
            case NORMAL:
                return "N";
            case SUV:
                return "S";
            case LUX:
                return "L";
            case MOTOR_DELIVERY:
                return "D";
            case MOTOR_TRANSPORT:
                return "R";
            default:
                throw new IllegalArgumentException("Unknown " + serviceType);
        }
    }

    @Override
    public ServiceType convertToEntityAttribute(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        switch (s) {
            case "T":
                return ServiceType.TAXI;
            case "E":
                return ServiceType.ECO;
            case "N":
                return ServiceType.NORMAL;
            case "S":
                return ServiceType.SUV;
            case "L":
                return ServiceType.LUX;
            case "D":
                return ServiceType.MOTOR_DELIVERY;
            case "R":
                return ServiceType.MOTOR_TRANSPORT;
            default:
                throw new IllegalArgumentException("Unknown " + s);
        }
    }
}
