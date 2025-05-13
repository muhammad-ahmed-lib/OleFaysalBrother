package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Country {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("name_ar")
    @Expose
    private String nameAr;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("flag")
    @Expose
    private String flag;
    @SerializedName("iso_code")
    @Expose
    private String isoCode;
    @SerializedName("login_type")
    @Expose
    private String loginType;
    @SerializedName("allow_module")
    @Expose
    private String allowModule;
    @SerializedName("short_name")
    @Expose
    private String shortName;
    @SerializedName("cities")
    @Expose
    private List<Country> cities = null;
    @SerializedName("is_selected")
    @Expose
    private String isSelected;
    @SerializedName("teams")
    @Expose
    private List<Team> teams = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getAllowModule() {
        return allowModule;
    }

    public void setAllowModule(String allowModule) {
        this.allowModule = allowModule;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<Country> getCities() {
        return cities;
    }

    public void setCities(List<Country> cities) {
        this.cities = cities;
    }

    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
