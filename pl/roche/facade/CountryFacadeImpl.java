package pl.roche.facade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    @Override
    public List<Countries> getListOfCountries(String... ipsArr) {

        for (String ipAddress : ipsArr) {

            try {
                double latitude = 0;
                String countryName = "";
                String result = getJSONString(ipAddress);
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
            } catch (ParseException | IOException ex) {
                LOG.log(Level.INFO, "Error ocuured and here are the details: {0}", ex.getMessage());
            }
        }
        LOG.log(Level.INFO, "Original List Size: {0}", originalList.size());
        LOG.log(Level.INFO, "Unique List Size: {0}", uniqueCountryList.size());
        countriesList.addAll(0, uniqueCountryList);
        return countriesList;
    }

    private static String getJSONString(String ipAddress) throws MalformedURLException, IOException {
        String result = "";
        String output = "";
        URL url = new URL("https://ipvigilante.com/json/?ip=" + ipAddress);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        LOG.log(Level.INFO, "Response code: {0}", conn.getResponseCode());
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        while ((output = br.readLine()) != null) {
            result += output;
        }
        return result;
    }
}
