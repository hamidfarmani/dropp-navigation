package util.converter;

import model.enums.City;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kasra on 4/23/2017.
 */
@Converter(autoApply = true)
public class CityConverter implements AttributeConverter<City, String> {
    @Override
    public String convertToDatabaseColumn(City city) {
        if (city == null) {
            return "-";
        }
        switch (city) {
            case GORGAN:
                return "GO";
            case TEHRAN:
                return "TE";
            case MASHHAD:
                return "MA";
            case ISFAHAN:
                return "IS";
            default:
                throw new IllegalArgumentException("Unknown " + city);
        }
    }

    @Override
    public City convertToEntityAttribute(String s) {
        if (s == null || s.equals("-")) {
            return null;
        }
        switch (s) {
            case "GO":
                return City.GORGAN;
            case "TE":
                return City.TEHRAN;
            case "MA":
                return City.MASHHAD;
            case "IS":
                return City.ISFAHAN;
            default:
                throw new IllegalArgumentException("Unknown " + s);
        }
    }

    public City getCityFromAddress(String address) {
        address = address.toLowerCase();
        if (address == null || address.equals("")) {
            return null;
        }
        if (address.contains("گرگان")) {
            return City.GORGAN;
        } else if (address.contains("تهران") || address.contains("tehran")) {
            return City.TEHRAN;
        } else if (address.contains("اصفهان") || address.contains("esfahan") || address.contains("isfahan")) {
            return City.ISFAHAN;
        } else if (address.contains("مشهد") || address.contains("mashhad")) {
            return City.MASHHAD;
        } else
            throw new IllegalArgumentException("Unknown " + address);
    }
}
