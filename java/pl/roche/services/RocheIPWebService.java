package pl.roche.services;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import pl.roche.facade.CountryFacade;
import pl.roche.facade.CountryFacadeImpl;
import pl.roche.objects.Countries;

@Path("/northcountries")
public class RocheIPWebService {

    @GET
    @Path("/")
    @Produces("application/json")

    public List<Countries> getCountriesOnIPAdresses(@QueryParam("ip") String... ipAddressArr) {
        List<Countries> countries = new ArrayList<>();

        CountryFacade facade = new CountryFacadeImpl();

        if (ipAddressArr.length > 0 && ipAddressArr.length < 6) {
            countries = facade.getListOfCountries(ipAddressArr);
        }
        return countries;
    }
}
