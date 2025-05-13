package ae.oleapp.util;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.api.APIClient;
import ae.oleapp.api.APIInterface;
import ae.oleapp.models.Club;
import ae.oleapp.models.Country;
import ae.oleapp.models.CountryPhoneList;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OleFieldData;

public class AppManager {
    private static final AppManager ourInstance = new AppManager();
    public static AppManager getInstance() {
        return ourInstance;
    }

    public APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
    public APIInterface apiInterfaceNew = APIClient.getClientNew().create(APIInterface.class);
    public APIInterface apiInterface2 = APIClient.getClient2().create(APIInterface.class);
    public APIInterface apiInterfaceNode = APIClient.getNodeClient().create(APIInterface.class);
    public APIInterface apiInterfacePartner = APIClient.getPartnerClient().create(APIInterface.class);

    public List<Country> countries = new ArrayList<>();
    public List<Club> clubs = new ArrayList<>();
    public List<OleClubFacility> clubFacilities = new ArrayList<>();
    public OleFieldData oleFieldData = null;
    public List<CountryPhoneList> countryPhoneLists = new ArrayList<>();

    public int notificationCount = 0;

    private AppManager() {
    }
}
