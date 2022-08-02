package com.example.bbbwithapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Absence implements Comparable<Absence> {
    @SerializedName("ID")
    @Expose
    private String ID;

    @SerializedName("User ID")
    @Expose
    private String UserID;

    @SerializedName("Name")
    @Expose
    private String Name;

    @SerializedName("Mobile Phone Number")
    @Expose
    private String MobilePhone;

    @SerializedName("Division")
    @Expose
    private String Division;

    @SerializedName("Tanggal Absen")
    @Expose
    private Integer TanggalAbsen;

    @SerializedName("Bulan Absen")
    @Expose
    private Integer BulanAbsen;

    @SerializedName("Tahun Absen")
    @Expose
    private Integer TahunAbsen;
    
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

    public Absence() {
    }

    public Absence(String ID, String UserID, String Name, String MobilePhone, String Division, Integer TanggalAbsen, Integer BulanAbsen, Integer TahunAbsen,
                   String CreatedBy, String CreatedIP, String CreatedPosition, String CreatedDate,
                   String ModifiedBy, String ModifiedIP, String ModifiedPosition, String ModifiedDate,
                   boolean IsActive) {
        this.ID = ID;
        this.UserID = UserID;
        this.Name = Name;
        this.MobilePhone = MobilePhone;
        this.Division = Division;
        this.TanggalAbsen = TanggalAbsen;
        this.BulanAbsen = BulanAbsen;
        this.TahunAbsen = TahunAbsen;

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

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getMobilePhone() {
        return MobilePhone;
    }

    public void setMobilePhone(String MobilePhone) {
        this.MobilePhone = MobilePhone;
    }

    public String getDivision() {
        return Division;
    }

    public void setDivision(String Division) {
        this.Division = Division;
    }

    public Integer getTanggalAbsen() {
        return TanggalAbsen;
    }

    public void setTanggalAbsen(Integer TanggalAbsen) {
        this.TanggalAbsen = TanggalAbsen;
    }

    public Integer getBulanAbsen() {
        return BulanAbsen;
    }

    public void setBulanAbsen(Integer BulanAbsen) {
        this.BulanAbsen = BulanAbsen;
    }

    public Integer getTahunAbsen() {
        return TahunAbsen;
    }

    public void setTahunAbsen(Integer TahunAbsen) {
        this.TahunAbsen = TahunAbsen;
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
        switch (getBulanAbsen()){
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
    public int compareTo(Absence another) {
        Integer sTahun = another.TahunAbsen.compareTo(this.TahunAbsen);
        if (sTahun != 0) {
            return sTahun;
        } else {
            Integer sBulan = another.getBulanAbsen().compareTo(this.getBulanAbsen());
            if (sBulan != 0) {
                return sBulan;
            } else {
                Integer sTanggal = another.getTanggalAbsen().compareTo(this.getTanggalAbsen());
                return sTanggal;
            }
        }
    }
}
