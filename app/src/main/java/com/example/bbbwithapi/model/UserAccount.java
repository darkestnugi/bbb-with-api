package com.example.bbbwithapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserAccount {
    @SerializedName("ID")
    @Expose
    private String ID;

    @SerializedName("UserID")
    @Expose
    private String UserID;

    @SerializedName("UserName")
    @Expose
    private String UserName;

    @SerializedName("Name")
    @Expose
    private String Name;

    @SerializedName("Email")
    @Expose
    private String Email;

    @SerializedName("Mobile Phone Number")
    @Expose
    private String MobilePhone;

    @SerializedName("Sex")
    @Expose
    private String Sex;

    @SerializedName("Division")
    @Expose
    private String Division;

    @SerializedName("Team")
    @Expose
    private String Team;

    @SerializedName("UserClass")
    @Expose
    private String UserClass;

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

    @SerializedName("LastLoginIP")
    @Expose
    private String LastLoginIP;

    @SerializedName("LastLoginPosition")
    @Expose
    private String LastLoginPosition;

    @SerializedName("LastLoginDate")
    @Expose
    private String LastLoginDate;

    @SerializedName("Level")
    @Expose
    private int Level;

    @SerializedName("IsActive")
    @Expose
    private boolean IsActive;

    public UserAccount() {
    }

    public UserAccount(String ID, String UserID, String UserName, String Name, String Email, String MobilePhone, String Sex,
                       String Division, String Team, String UserClass,
                       String CreatedBy, String CreatedIP, String CreatedPosition, String CreatedDate,
                       String ModifiedBy, String ModifiedIP, String ModifiedPosition, String ModifiedDate,
                       String LastLoginIP, String LastLoginPosition, String LastLoginDate,
                       int Level, boolean IsActive) {

        this.ID = ID;
        this.UserID = UserID;
        this.UserName = UserName;
        this.Name = Name;
        this.Email = Email;
        this.MobilePhone = MobilePhone;
        this.Sex = Sex;
        this.Division = Division;
        this.Team = Team;
        this.UserClass = UserClass;

        this.CreatedBy = CreatedBy;
        this.CreatedIP = CreatedIP;
        this.CreatedPosition = CreatedPosition;
        this.CreatedDate = CreatedDate;

        this.ModifiedBy = ModifiedBy;
        this.ModifiedIP = ModifiedIP;
        this.ModifiedPosition = ModifiedPosition;
        this.ModifiedDate = ModifiedDate;

        this.LastLoginIP = LastLoginIP;
        this.LastLoginPosition = LastLoginPosition;
        this.LastLoginDate = LastLoginDate;

        this.Level = Level;
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

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getMobilePhone() {
        return MobilePhone;
    }

    public void setMobilePhone(String MobilePhone) {
        this.MobilePhone = MobilePhone;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String Sex) {
        this.Sex = Sex;
    }

    public String getDivision() {
        return Division;
    }

    public void setDivision(String Division) {
        this.Division = Division;
    }

    public String getTeam() {
        return Team;
    }

    public void setTeam(String Team) {
        this.Team = Team;
    }

    public String getUserClass() {
        return UserClass;
    }

    public void setUserClass(String UserClass) {
        this.UserClass = UserClass;
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

    public String getLastLoginIP() {
        return LastLoginIP;
    }

    public void setLastLoginIP(String LastLoginIP) {
        this.LastLoginIP = LastLoginIP;
    }

    public String getLastLoginPosition() {
        return LastLoginPosition;
    }

    public void setLastLoginPosition(String LastLoginPosition) {
        this.LastLoginPosition = LastLoginPosition;
    }

    public String getLastLoginDate() {
        return LastLoginDate;
    }

    public void setLastLoginDate(String LastLoginDate) {
        this.LastLoginDate = LastLoginDate;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        this.Level = level;
    }

    public boolean getIsActive() {
        return IsActive;
    }

    public void setIsActive(boolean IsActive) {
        this.IsActive = IsActive;
    }

    @Override
    public String toString() {
        return this.UserName;            // What to display in the Spinner list.
    }
}
