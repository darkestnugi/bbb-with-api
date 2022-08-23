package com.example.bbbwithapi.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.type.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Donation implements Parcelable, Comparable<Donation> {
    @SerializedName("ID")
    @Expose
    private String ID;

    @SerializedName("User ID")
    @Expose
    private String UserID;

    @SerializedName("Transaction Date")
    @Expose
    private String TransactionDate;

    @SerializedName("Donor Name")
    @Expose
    private String DonorName;

    @SerializedName("Donor Email")
    @Expose
    private String DonorEmail;

    @SerializedName("Donor Mobile Phone Number")
    @Expose
    private String DonorMobilePhone;

    @SerializedName("Bank Name")
    @Expose
    private String BankTitle;

    @SerializedName("Program Name")
    @Expose
    private String ProgramTitle;

    @SerializedName("Religion Name")
    @Expose
    private String ReligionTitle;

    @SerializedName("Job Name")
    @Expose
    private String JobTitle;

    @SerializedName("Age Name")
    @Expose
    private String AgeTitle;

    @SerializedName("Domicile Name")
    @Expose
    private String DomicileTitle;

    @SerializedName("Category Name")
    @Expose
    private String CategoryTitle;

    @SerializedName("Nominal")
    @Expose
    private Double Nominal;

    @SerializedName("Prayer")
    @Expose
    private String Prayer;

    @SerializedName("Status Payment")
    @Expose
    private String StatusPayment;

    @SerializedName("Photo")
    @Expose
    private String Photo;

    @SerializedName("Photo URL")
    @Expose
    private String PhotoURL;

    @SerializedName("Reference Number")
    @Expose
    private String ReferenceNumber;

    @SerializedName("Reference Name")
    @Expose
    private String ReferenceName;

    @SerializedName("Reference Division")
    @Expose
    private String ReferenceDivision;

    @SerializedName("Reference Team")
    @Expose
    private String ReferenceTeam;

    @SerializedName("Reference Class")
    @Expose
    private String ReferenceClass;

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

    @SerializedName("User")
    @Expose
    private UserAccount User;

    @SerializedName("Donor")
    @Expose
    private Donor Donor;

    @SerializedName("Bank")
    @Expose
    private Bank Bank;

    @SerializedName("Program")
    @Expose
    private Program Program;

    @SerializedName("Religion")
    @Expose
    private Religion Religion;

    @SerializedName("Job")
    @Expose
    private Job Job;

    @SerializedName("Age")
    @Expose
    private Age Age;

    @SerializedName("Category")
    @Expose
    private Category Category;

    @SerializedName("Domicile")
    @Expose
    private Domicile Domicile;

    public Donation() {
    }

    protected Donation(Parcel parcel) {
        this.ID = parcel.readString();
        this.UserID = parcel.readString();
        this.DonorName = parcel.readString();
        this.DonorEmail = parcel.readString();
        this.DonorMobilePhone = parcel.readString();
        this.TransactionDate = parcel.readString();
        this.BankTitle = parcel.readString();
        this.ProgramTitle = parcel.readString();
        this.ReligionTitle = parcel.readString();
        this.JobTitle = parcel.readString();
        this.AgeTitle = parcel.readString();
        this.DomicileTitle = parcel.readString();
        this.CategoryTitle = parcel.readString();
        this.Nominal = parcel.readDouble();
        this.Prayer = parcel.readString();
        this.StatusPayment = parcel.readString();
        this.Photo = parcel.readString();
        this.PhotoURL = parcel.readString();
        this.ReferenceNumber = parcel.readString();
        this.ReferenceName = parcel.readString();
        this.ReferenceDivision = parcel.readString();
        this.ReferenceTeam = parcel.readString();
        this.ReferenceClass = parcel.readString();
        this.CreatedDate = parcel.readString();
        this.CreatedBy = parcel.readString();
        this.CreatedIP = parcel.readString();
        this.CreatedPosition = parcel.readString();
        this.ModifiedBy = parcel.readString();
        this.ModifiedIP = parcel.readString();
        this.ModifiedPosition = parcel.readString();
        this.ModifiedDate = parcel.readString();
    }

    public Donation(String ID, String UserID, String TransactionDate, UserAccount User, Donor Donor,
                    Double Nominal, String Prayer, String StatusPayment, String Photo, String PhotoURL,
                    String ReferenceNumber, String ReferenceName, String ReferenceDivision, String ReferenceTeam, String ReferenceClass,
                    String BankTitle, String ProgramTitle, String ReligionTitle, String JobTitle, String AgeTitle, String DomicileTitle, String CategoryTitle,
                    Bank Bank, Program Program, Religion Religion, Job Job, Age Age, Domicile Domicile, Category Category,
                    String CreatedBy, String CreatedIP, String CreatedPosition, String CreatedDate,
                    String ModifiedBy, String ModifiedIP, String ModifiedPosition, String ModifiedDate,
                    boolean IsActive) {
        this.ID = ID;
        this.UserID = UserID;
        this.TransactionDate = TransactionDate;
        this.User = User;
        this.Donor = Donor;
        this.Nominal = Nominal;
        this.Prayer = Prayer;
        this.StatusPayment = StatusPayment;
        this.Photo = Photo;
        this.PhotoURL = PhotoURL;

        this.ReferenceNumber = ReferenceNumber;
        this.ReferenceName = ReferenceName;
        this.ReferenceDivision = ReferenceDivision;
        this.ReferenceTeam = ReferenceTeam;
        this.ReferenceClass = ReferenceClass;

        this.BankTitle = BankTitle;
        this.ProgramTitle = ProgramTitle;
        this.ReligionTitle = ReligionTitle;
        this.JobTitle = JobTitle;
        this.AgeTitle = AgeTitle;
        this.DomicileTitle = DomicileTitle;
        this.CategoryTitle = CategoryTitle;

        this.Bank = Bank;
        this.Program = Program;
        this.Religion = Religion;
        this.Job = Job;
        this.Age = Age;
        this.Domicile = Domicile;
        this.Category = Category;

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

    public String getDonorName() {
        return DonorName;
    }

    public void setDonorName(String donator) {
        this.DonorName = donator;
    }

    public String getDonorEmail() {
        return DonorEmail;
    }

    public void setDonorEmail(String donorEmail) {
        this.DonorEmail = donorEmail;
    }

    public String getDonorMobilePhone() {
        return DonorMobilePhone;
    }

    public void setDonorMobilePhone(String donorPhoneNumber) {
        this.DonorMobilePhone = donorPhoneNumber;
    }

    public String getBankTitle() {
        return BankTitle;
    }

    public void setBankTitle(String theName) {
        this.BankTitle = theName;
    }

    public String getProgramTitle() {
        return ProgramTitle;
    }

    public void setProgramTitle(String theName) {
        this.ProgramTitle = theName;
    }

    public String getReligionTitle() {
        return ReligionTitle;
    }

    public void setReligionTitle(String theName) {
        this.ReligionTitle = theName;
    }

    public String getJobTitle() {
        return JobTitle;
    }

    public void setJobTitle(String theName) {
        this.JobTitle = theName;
    }

    public String getAgeTitle() {
        return AgeTitle;
    }

    public void setAgeTitle(String theName) {
        this.AgeTitle = theName;
    }

    public String getDomicileTitle() {
        return DomicileTitle;
    }

    public void setDomicileTitle(String theName) {
        this.DomicileTitle = theName;
    }

    public String getCategoryTitle() {
        return CategoryTitle;
    }

    public void setCategoryTitle(String theName) {
        this.CategoryTitle = theName;
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

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String TransactionDate) {
        this.TransactionDate = TransactionDate;
    }

    public UserAccount getUser() {
        return User;
    }

    public void setUser(UserAccount User) {
        this.User = User;
    }

    public Donor getDonor() {
        return Donor;
    }

    public void setDonor(Donor Donor) {
        this.Donor = Donor;
    }

    public Double getNominal() {
        return Nominal;
    }

    public void setNominal(Double Nominal) {
        this.Nominal = Nominal;
    }

    public String getPrayer() {
        return Prayer;
    }

    public void setPrayer(String Prayer) {
        this.Prayer = Prayer;
    }

    public String getStatusPayment() {
        return StatusPayment;
    }

    public void setStatusPayment(String StatusPayment) {
        this.StatusPayment = StatusPayment;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String Photo) {
        this.Photo = Photo;
    }

    public String getPhotoURL() {
        return PhotoURL;
    }

    public void setPhotoURL(String PhotoURL) {
        this.PhotoURL = PhotoURL;
    }

    public String getReferenceNumber() {
        return ReferenceNumber;
    }

    public void setReferenceNumber(String ReferenceNumber) {
        this.ReferenceNumber = ReferenceNumber;
    }

    public String getReferenceName() {
        return ReferenceName;
    }

    public void setReferenceName(String ReferenceName) {
        this.ReferenceName = ReferenceName;
    }

    public String getReferenceDivision() {
        return ReferenceDivision;
    }

    public void setReferenceDivision(String ReferenceDivision) {
        this.ReferenceDivision = ReferenceDivision;
    }

    public String getReferenceTeam() {
        return ReferenceTeam;
    }

    public void setReferenceTeam(String ReferenceTeam) {
        this.ReferenceTeam = ReferenceTeam;
    }

    public String getReferenceClass() {
        return ReferenceClass;
    }

    public void setReferenceClass(String ReferenceClass) {
        this.ReferenceClass = ReferenceClass;
    }

    public Bank getBank() {
        return Bank;
    }

    public void setBank(Bank Bank) {
        this.Bank = Bank;
    }

    public Program getProgram() {
        return Program;
    }

    public void setProgram(Program Program) {
        this.Program = Program;
    }

    public Religion getReligion() {
        return Religion;
    }

    public void setReligion(Religion Religion) {
        this.Religion = Religion;
    }

    public Job getJob() {
        return Job;
    }

    public void setJob(Job Job) {
        this.Job = Job;
    }

    public Age getAge() {
        return Age;
    }

    public void setAge(Age Age) {
        this.Age = Age;
    }

    public Domicile getDomicile() {
        return Domicile;
    }

    public void setDomicile(Domicile Domicile) {
        this.Domicile = Domicile;
    }

    public Category getCategory() {
        return Category;
    }

    public void setCategory(Category Category) {
        this.Category = Category;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getID());
        parcel.writeString(getUserID());
        parcel.writeString(getDonorName());
        parcel.writeString(getDonorEmail());
        parcel.writeString(getDonorMobilePhone());
        parcel.writeString(getTransactionDate());
        parcel.writeString(getBankTitle());
        parcel.writeString(getProgramTitle());
        parcel.writeString(getReligionTitle());
        parcel.writeString(getJobTitle());
        parcel.writeString(getAgeTitle());
        parcel.writeString(getDomicileTitle());
        parcel.writeString(getCategoryTitle());
        parcel.writeDouble(getNominal());
        parcel.writeString(getPrayer());
        parcel.writeString(getStatusPayment());
        parcel.writeString(getPhoto());
        parcel.writeString(getPhotoURL());
        parcel.writeString(getReferenceNumber());
        parcel.writeString(getReferenceName());
        parcel.writeString(getCreatedDate());
        parcel.writeString(getCreatedBy());
        parcel.writeString(getCreatedIP());
        parcel.writeString(getCreatedPosition());
        parcel.writeString(getModifiedBy());
        parcel.writeString(getModifiedIP());
        parcel.writeString(getModifiedPosition());
        parcel.writeString(getModifiedDate());
    }

    public static final Creator<Donation> CREATOR = new Creator<Donation>() {
        @Override
        public Donation createFromParcel(Parcel parcel) {
            return new Donation(parcel);
        }

        @Override
        public Donation[] newArray(int i) {
            return new Donation[i];
        }
    };

    @Override
    public int compareTo(Donation another) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date1 = formatter.parse(this.getTransactionDate());
            Date date2 = formatter.parse(another.getTransactionDate());

            int sTanggalTransaksi = date2.compareTo(date1);
            if (sTanggalTransaksi != 0) {
                return sTanggalTransaksi;
            } else {
                return 0;
            }
        } catch (Exception err) {
            return 0;
        }
    }
}
