package util.converter;

import model.enums.AccountState;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kasra on 3/14/2017.
 */

@Converter(autoApply = true)
public class AccountStateConverter implements AttributeConverter<AccountState, Integer> {
    @Override
    public Integer convertToDatabaseColumn(AccountState accountState) {
        if(accountState == null){
            return null;
        }
        switch (accountState) {
            case REGISTERED:
                return 0;
            case ACTIVATED:
                return 1;
            case READY_TO_VERIFY:
                return 2;
            case VERIFIED:
                return 3;
            case BANNED:
                return -1;
            default:
                throw new IllegalArgumentException("Unknown " + accountState);
        }
    }

    @Override
    public AccountState convertToEntityAttribute(Integer aInteger) {
        if(aInteger==null){
            return null;
        }
        switch (aInteger) {
            case 0:
                return AccountState.REGISTERED;
            case 1:
                return AccountState.ACTIVATED;
            case 2:
                return AccountState.READY_TO_VERIFY;
            case 3:
                return AccountState.VERIFIED;
            case -1:
                return AccountState.BANNED;
            default:
                throw new IllegalArgumentException("Unknown " + aInteger);
        }
    }
}
