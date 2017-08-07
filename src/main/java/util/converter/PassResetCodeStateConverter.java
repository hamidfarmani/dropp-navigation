package util.converter;

import model.enums.PassResetCodeState;

import javax.persistence.AttributeConverter;

/**
 * Created by kasra on 8/5/2017.
 */
public class PassResetCodeStateConverter implements AttributeConverter<PassResetCodeState, Character> {
    @Override
    public Character convertToDatabaseColumn(PassResetCodeState passResetCodeState) {
        switch (passResetCodeState) {
            case REQUESTED:
                return 'R';
            case VERIFIED:
                return 'V';
            case HAD_RESET:
                return 'H';
            default:
                throw new IllegalArgumentException("Unknown " + passResetCodeState);
        }
    }

    @Override
    public PassResetCodeState convertToEntityAttribute(Character character) {
        switch (character) {
            case 'R':
                return PassResetCodeState.REQUESTED;
            case 'V':
                return PassResetCodeState.VERIFIED;
            case 'H':
                return PassResetCodeState.HAD_RESET;
            default:
                throw new IllegalArgumentException("Unknown" + character);
        }
    }
}
