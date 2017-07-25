package util.converter;

import model.enums.TicketState;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kasra on 5/28/2017.
 */
@Converter(autoApply = true)
public class TicketStateConverter implements AttributeConverter<TicketState, Character> {
    @Override
    public Character convertToDatabaseColumn(TicketState ticketState) {
        switch (ticketState) {
            case RESOLVED:
                return 'R';
            case UNRESOLVED:
                return 'U';
            case REJECTED:
                return 'J';
            default:
                throw new IllegalArgumentException("Unknown " + ticketState);
        }
    }

    @Override
    public TicketState convertToEntityAttribute(Character character) {
        switch (character) {
            case 'R':
                return TicketState.RESOLVED;
            case 'U':
                return TicketState.UNRESOLVED;
            case 'J':
                return TicketState.REJECTED;
            default:
                throw new IllegalArgumentException("Unknown " + character);
        }
    }
}
