package pl.roche.objects;

import java.io.Serializable;
import java.util.Objects;

public class Countries implements Serializable {

    private static final long serialVersionUID = 1L;
    private String country;

    public Countries() {
    }

    public Countries(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Countries)) {
            return false;
        }

        Countries ip = (Countries) o;
        return this.country.equalsIgnoreCase(ip.getCountry());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.country);
        return hash;
    }
}
