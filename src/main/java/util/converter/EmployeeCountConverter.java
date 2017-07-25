package util.converter;

import model.enums.EmployeeCount;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kasra on 4/29/2017.
 */
@Converter(autoApply = true)
public class EmployeeCountConverter implements AttributeConverter<EmployeeCount, Integer> {

    @Override
    public Integer convertToDatabaseColumn(EmployeeCount employeeCount) {
        if (employeeCount == null) {
            {
                return -1;
            }
        }
        switch (employeeCount) {

            case LESS_THAN_10:
                return 0;
            case LESS_THAN_20:
                return 1;
            case LESS_THAN_50:
                return 2;
            case LESS_THAN_100:
                return 3;
            case LESS_THAN_500:
                return 4;
            case LESS_THAN_1000:
                return 5;
            case MORE_THAN_1000:
                return 6;
            default:
                throw new IllegalArgumentException("Unknown" + employeeCount);
        }
    }

    @Override
    public EmployeeCount convertToEntityAttribute(Integer i) {
        if (i == -1) {
            return null;
        }
        switch (i) {
            case 0:
                return EmployeeCount.LESS_THAN_10;
            case 1:
                return EmployeeCount.LESS_THAN_20;
            case 2:
                return EmployeeCount.LESS_THAN_50;
            case 3:
                return EmployeeCount.LESS_THAN_100;
            case 4:
                return EmployeeCount.LESS_THAN_500;
            case 5:
                return EmployeeCount.LESS_THAN_1000;
            case 6:
                return EmployeeCount.MORE_THAN_1000;
            default:
                throw new IllegalArgumentException("Unknown" + i);
        }
    }
}

