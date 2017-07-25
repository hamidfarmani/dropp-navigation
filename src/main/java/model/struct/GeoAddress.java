package model.struct;

import java.io.Serializable;

/**
 * Created by kasra on 4/3/2017.
 */

public class GeoAddress implements Serializable {

    private String street_address;

    private String route;

    private String country;

    private String state;

    private String city;

    public GeoAddress() {
    }

    public GeoAddress(String street_address, String route, String country, String state, String city) {
        this.street_address = street_address;
        this.route = route;
        this.country = country;
        this.state = state;
        this.city = city;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
