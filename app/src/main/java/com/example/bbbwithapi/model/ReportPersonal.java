package com.example.bbbwithapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportPersonal implements Comparable<ReportPersonal> {
    @SerializedName("ID")
    @Expose
    private String ID;

    @SerializedName("User ID")
    @Expose
    private String UserID;

    @SerializedName("Bulan Transaksi")
    @Expose
    private Integer BulanTransaksi;

    @SerializedName("Tahun Transaksi")
    @Expose
    private Integer TahunTransaksi;

    @SerializedName("Jumlah Transaksi")
    @Expose
    private Double JumlahTransaksi;

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

    public ReportPersonal() {
    }

    public ReportPersonal(String ID, String UserID, Integer BulanTransaksi, Integer TahunTransaksi, Double JumlahTransaksi,
                          String CreatedBy, String CreatedIP, String CreatedPosition, String CreatedDate,
                          String ModifiedBy, String ModifiedIP, String ModifiedPosition, String ModifiedDate,
                          boolean IsActive) {
        this.ID = ID;
        this.UserID = UserID;
        this.BulanTransaksi = BulanTransaksi;
        this.TahunTransaksi = TahunTransaksi;
        this.JumlahTransaksi = JumlahTransaksi;

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

    public Integer getBulanTransaksi() {
        return BulanTransaksi;
    }

    public void setBulanTransaksi(Integer BulanTransaksi) {
        this.BulanTransaksi = BulanTransaksi;
    }

    public Integer getTahunTransaksi() {
        return TahunTransaksi;
    }

    public void setTahunTransaksi(Integer TahunTransaksi) {
        this.TahunTransaksi = TahunTransaksi;
    }

    public Double getJumlahTransaksi() {
        return JumlahTransaksi;
    }

    public void setJumlahTransaksi(Double JumlahTransaksi) {
        this.JumlahTransaksi = JumlahTransaksi;
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
        switch (getBulanTransaksi()){
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
    public int compareTo(ReportPersonal another) {
        int sTahun = another.TahunTransaksi.compareTo(this.TahunTransaksi);
        if (sTahun != 0) {
            return sTahun;
        } else {
            Integer sBulan = another.getBulanTransaksi().compareTo(this.getBulanTransaksi());
            return sBulan;
        }
    }
}
