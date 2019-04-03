package pl.roche.facade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pl.roche.objects.Countries;

public class CountryFacadeImpl implements CountryFacade {

    private final SortedSet<Countries> uniqueCountryList = new TreeSet<>(Comparator.comparing(Countries::getCountry));
    private final List<Countries> countriesList = new ArrayList<>();
    private final List<Countries> originalList = new ArrayList<>();
    private static final Logger LOG = Logger.getLogger(CountryFacadeImpl.class.getName());

    //Call the webservice
    @Override
    public List<Countries> getListOfCountries(String... ipsArr) {

        for (String ipAddress : ipsArr) {
            String result = "";
            double latitude = 0;
            String countryName = "";
            try {
                URL url = new URL("https://ipvigilante.com/json/?ip=" + ipAddress);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                System.out.println("Response code: " + conn.getResponseCode());
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                while ((br.readLine()) != null) {
                    result += br.readLine();
                }
                //result = "{\"data\":{\"continent_name\":\"Africa\",\"subdivision_2_name\":null,\"city_name\":null,\"ipv4\":\"196.10.113.18\",\"latitude\":\"29.00000\",\"country_name\":\"South Africa\",\"subdivision_1_name\":null,\"longitude\":\"24.00000\"},\"status\":\"success\"}";
                Object obj = new JSONParser().parse(result);
                JSONObject jo = (JSONObject) obj;

                // getting data 
                Map data = ((Map) jo.get("data"));

                // iterating data Map 
                Iterator<Map.Entry> itr = data.entrySet().iterator();

                while (itr.hasNext()) {
                    Map.Entry pair = itr.next();
                    if (pair.getKey().equals("latitude")) {
                        latitude = Double.parseDouble((String) pair.getValue());
                    } else if (pair.getKey().equals("country_name")) {
                        countryName = (String) pair.getValue();
                        originalList.add(new Countries(countryName));
                    }
                    if (latitude > 0 && (!countryName.isEmpty() && countryName != null)) {
                        uniqueCountryList.add(new Countries(countryName));
                    }
                }
                conn.disconnect();
            } catch (ParseException | IOException ex) {
                ex.printStackTrace();
                LOG.log(Level.INFO, "Error parsing json string or ioexception occured: {0}", ex.getMessage());
            }
        }
        LOG.log(Level.INFO, "Unique List: {0}", uniqueCountryList.size());
        LOG.log(Level.INFO, "Original List: {0}", originalList.size());
        countriesList.addAll(0, uniqueCountryList);
        return countriesList;
    }
}
