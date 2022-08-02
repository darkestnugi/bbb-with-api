package com.example.bbbwithapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WatchYearly implements Comparable<WatchYearly> {
    @SerializedName("ID")
    @Expose
    private String ID;

    @SerializedName("User ID")
    @Expose
    private List<String> UserID;

    @SerializedName("Name")
    @Expose
    private List<String> Name;

    @SerializedName("Mobile Phone Number")
    @Expose
    private List<String> MobilePhone;

    @SerializedName("Division")
    @Expose
    private List<String> Division;

    @SerializedName("Tanggal Piket")
    @Expose
    private Integer TanggalPiket;

    @SerializedName("Bulan Piket")
    @Expose
    private Integer BulanPiket;

    @SerializedName("Tahun Piket")
    @Expose
    private Integer TahunPiket;

    @SerializedName("CreatedBy")
    @Expose
    private String CreatedBy;

    @SerializedName("CreatedIP")
    @Expose
    private String CreatedIP;

    @SerializedName("CreatedPosition")
    @Expose
    private String CreatedPosition;

    @SerializedName("CreatedDate")
    @Expose
    private String CreatedDate;

    @SerializedName("ModifiedBy")
    @Expose
    private String ModifiedBy;

    @SerializedName("ModifiedIP")
    @Expose
    private String ModifiedIP;

    @SerializedName("ModifiedPosition")
    @Expose
    private String ModifiedPosition;

    @SerializedName("ModifiedDate")
    @Expose
    private String ModifiedDate;

    @SerializedName("IsActive")
    @Expose
    private boolean IsActive;

    public WatchYearly() {
    }

    public WatchYearly(String ID, List<String> UserID, List<String> Name, List<String> MobilePhone, List<String> Division, Integer TanggalPiket, Integer BulanPiket, Integer TahunPiket,
                       String CreatedBy, String CreatedIP, String CreatedPosition, String CreatedDate,
                       String ModifiedBy, String ModifiedIP, String ModifiedPosition, String ModifiedDate,
                       boolean IsActive) {
        this.ID = ID;
        this.UserID = UserID;
        this.Name = Name;
        this.MobilePhone = MobilePhone;
        this.Division = Division;
        this.TanggalPiket = TanggalPiket;
        this.BulanPiket = BulanPiket;
        this.TahunPiket = TahunPiket;

        this.CreatedBy = CreatedBy;
        this.CreatedIP = CreatedIP;
        this.CreatedPosition = CreatedPosition;
        this.CreatedDate = CreatedDate;

        this.ModifiedBy = ModifiedBy;
        this.ModifiedIP = ModifiedIP;
        this.ModifiedPosition = ModifiedPosition;
        this.ModifiedDate = ModifiedDate;

        this.IsActive = IsActive;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public List<String> getUserID() {
        return UserID;
    }

    public void setUserID(List<String> UserID) {
        this.UserID = UserID;
    }

    public List<String> getName() {
        return Name;
    }

    public void setName(List<String> Name) {
        this.Name = Name;
    }

    public List<String> getMobilePhone() {
        return MobilePhone;
    }

    public void setMobilePhone(List<String> MobilePhone) {
        this.MobilePhone = MobilePhone;
    }

    public List<String> getDivision() {
        return Division;
    }

    public void setDivision(List<String> Division) {
        this.Division = Division;
    }

    public Integer getTanggalPiket() {
        return TanggalPiket;
    }

    public void setTanggalPiket(Integer TanggalPiket) {
        this.TanggalPiket = TanggalPiket;
    }

    public Integer getBulanPiket() {
        return BulanPiket;
    }

    public void setBulanPiket(Integer BulanPiket) {
        this.BulanPiket = BulanPiket;
    }

    public Integer getTahunPiket() {
        return TahunPiket;
    }

    public void setTahunPiket(Integer TahunPiket) {
        this.TahunPiket = TahunPiket;
    }
    
    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String CreatedBy) {
        this.CreatedBy = CreatedBy;
    }

    public String getCreatedIP() {
        return CreatedIP;
    }

    public void setCreatedIP(String CreatedIP) {
        this.CreatedIP = CreatedIP;
    }

    public String getCreatedPosition() {
        return CreatedPosition;
    }

    public void setCreatedPosition(String CreatedPosition) {
        this.CreatedPosition = CreatedPosition;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String CreatedDate) {
        this.CreatedDate = CreatedDate;
    }

    public String getModifiedBy() {
        return ModifiedBy;
    }

    public void setModifiedBy(String ModifiedBy) {
        this.ModifiedBy = ModifiedBy;
    }

    public String getModifiedIP() {
        return ModifiedIP;
    }

    public void setModifiedIP(String ModifiedIP) {
        this.ModifiedIP = ModifiedIP;
    }

    public String getModifiedPosition() {
        return ModifiedPosition;
    }

    public void setModifiedPosition(String ModifiedPosition) {
        this.ModifiedPosition = ModifiedPosition;
    }

    public String getModifiedDate() {
        return ModifiedDate;
    }

    public void setModifiedDate(String ModifiedDate) {
        this.ModifiedDate = ModifiedDate;
    }

    public Boolean getIsActive() {
        return IsActive;
    }

    public void setIsActive(Boolean IsActive) {
        this.IsActive = IsActive;
    }

    public String getMonth() {
        switch (getBulanPiket()){
            case 0 : return "Januari";
            case 1 : return "Februari";
            case 2 : return "Maret";
            case 3 : return "April";
            case 4 : return "May";
            case 5 : return "Juni";
            case 6 : return "Juli";
            case 7 : return "Agustus";
            case 8 : return "September";
            case 9 : return "October";
            case 10 : return "November";
            case 11 : return "Desember";
        }
        return "";
    }

    @Override
    public int compareTo(WatchYearly another) {
        int sTahun = another.TahunPiket.compareTo(this.TahunPiket);
        if (sTahun != 0) {
            return sTahun;
        } else {
            Integer sBulan = another.getBulanPiket().compareTo(this.getBulanPiket());
            if (sBulan != 0) {
                return sBulan;
            } else {
                Integer sTanggal = another.getTanggalPiket().compareTo(this.getTanggalPiket());
                return sTanggal;
            }
        }
    }
}
