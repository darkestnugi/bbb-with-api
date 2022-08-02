package com.example.bbbwithapi.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PDC implements Parcelable {
    @SerializedName("ID")
    @Expose
    private String ID;

    @SerializedName("User ID")
    @Expose
    private String UserID;

    @SerializedName("Transaction Date")
    @Expose
    private String TransactionDate;

    @SerializedName("Program Name")
    @Expose
    private String ProgramTitle;

    @SerializedName("Introduce")
    @Expose
    private Integer Introduce;

    @SerializedName("Follow Up")
    @Expose
    private Integer FollowUp;

    @SerializedName("Success")
    @Expose
    private Integer Success;

    @SerializedName("Succes More Than 1 Million")
    @Expose
    private Integer SuccessDonorMoreThan1Million;

    @SerializedName("Nominal")
    @Expose
    private Double Nominal;

    @SerializedName("Reference Number")
    @Expose
    private String ReferenceNumber;

    @SerializedName("Reference Name")
    @Expose
    private String ReferenceName;

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
    
    @SerializedName("Program")
    @Expose
    private Program Program;

    public PDC() {
    }

    protected PDC(Parcel parcel) {
        this.ID = parcel.readString();
        this.UserID = parcel.readString();
        this.ProgramTitle = parcel.readString();
        this.Introduce = parcel.readInt();
        this.FollowUp = parcel.readInt();
        this.Success = parcel.readInt();
        this.SuccessDonorMoreThan1Million = parcel.readInt();
        this.Nominal = parcel.readDouble();
        this.ReferenceNumber = parcel.readString();
        this.ReferenceName = parcel.readString();
        this.CreatedDate = parcel.readString();
        this.CreatedBy = parcel.readString();
        this.CreatedIP = parcel.readString();
        this.CreatedPosition = parcel.readString();
        this.ModifiedBy = parcel.readString();
        this.ModifiedIP = parcel.readString();
        this.ModifiedPosition = parcel.readString();
        this.ModifiedDate = parcel.readString();
    }

    public PDC(String ID, String UserID, String TransactionDate, Program Program,
               Double Nominal, Integer Introduce, Integer FollowUp,
               Integer Success, Integer SuccessDonorMoreThan1Million,
               String ReferenceNumber, String ReferenceName,
               String CreatedBy, String CreatedIP, String CreatedPosition, String CreatedDate,
               String ModifiedBy, String ModifiedIP, String ModifiedPosition, String ModifiedDate,
               boolean IsActive) {
        this.ID = ID;
        this.UserID = UserID;
        this.TransactionDate = TransactionDate;
        this.Program = Program;
        this.Introduce = Introduce;
        this.FollowUp = FollowUp;
        this.Nominal = Nominal;

        this.Success = Success;
        this.SuccessDonorMoreThan1Million = SuccessDonorMoreThan1Million;

        this.ReferenceNumber = ReferenceNumber;
        this.ReferenceName = ReferenceName;

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
    
    public String getProgramTitle() {
        return ProgramTitle;
    }

    public void setProgramTitle(String programName) {
        this.ProgramTitle = programName;
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

    public Program getProgram() {
        return Program;
    }

    public void setProgram(Program Program) {
        this.Program = Program;
    }

    public Integer getIntroduce() {
        return Introduce;
    }

    public void setIntroduce(Integer Introduce) {
        this.Introduce = Introduce;
    }

    public Integer getFollowUp() {
        return FollowUp;
    }

    public void setFollowUp(Integer FollowUp) {
        this.FollowUp = FollowUp;
    }

    public Integer getSuccess() {
        return Success;
    }

    public void setSuccess(Integer Success) {
        this.Success = Success;
    }

    public Integer getSuccessDonorMoreThan1Million() {
        return SuccessDonorMoreThan1Million;
    }

    public void setSuccessDonorMoreThan1Million(Integer SuccessDonorMoreThan1Million) {
        this.SuccessDonorMoreThan1Million = SuccessDonorMoreThan1Million;
    }

    public Double getNominal() {
        return Nominal;
    }

    public void setNominal(Double Nominal) {
        this.Nominal = Nominal;
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
        parcel.writeString(getProgramTitle());
        parcel.writeInt(getIntroduce());
        parcel.writeInt(getFollowUp());
        parcel.writeInt(getSuccess());
        parcel.writeDouble(getNominal());
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

    public static final Creator<PDC> CREATOR = new Creator<PDC>() {
        @Override
        public PDC createFromParcel(Parcel parcel) {
            return new PDC(parcel);
        }

        @Override
        public PDC[] newArray(int i) {
            return new PDC[i];
        }
    };
}
