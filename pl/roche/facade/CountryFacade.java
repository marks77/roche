package pl.roche.facade;

import java.util.List;
import pl.roche.objects.Countries;

public interface CountryFacade {

    List<Countries> getListOfCountries(String...ipsArr);
}
