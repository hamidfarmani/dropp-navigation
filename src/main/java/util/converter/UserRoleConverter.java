package util.converter;

import model.enums.UserRole;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kasra on 3/6/2017.
 */

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, Character> {

    public Character convertToDatabaseColumn(UserRole userRoles) {
        if (userRoles == null) {
            return '-';
        }
        switch (userRoles) {
            case DRIVER:
                return 'D';
            case PASSENGER:
                return 'P';
            case OPERATOR:
                return 'O';
            case MASTER_OPERATOR:
                return 'M';
            case ADMIN:
                return 'A';
            case CAR_OPERATOR:
                return 'C';
            default:
                throw new IllegalArgumentException("UNKNOWN" + userRoles);
        }
    }

    public UserRole convertToEntityAttribute(Character character) {
        if (character == null) {
            return null;
        }
        switch (character) {
            case 'D':
                return UserRole.DRIVER;
            case 'P':
                return UserRole.PASSENGER;
            case 'O':
                return UserRole.OPERATOR;
            case 'A':
                return UserRole.ADMIN;
            case 'M':
                return UserRole.MASTER_OPERATOR;
            case 'C':
                return UserRole.CAR_OPERATOR;
            case '-':
                return null;
            default:
                throw new IllegalArgumentException("UNKNOWN" + character);
        }
    }
}
