package com.example.layersdiseasedetection.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MedicationDetails implements Parcelable {

    private String disease;
    private String medicationId;
    private String medicationName;
    private String medicationDescription;
    private String imageUrl;

    public MedicationDetails() {
        // Default constructor required for Firebase
    }

    public MedicationDetails(String medicationId,String disease, String medicationName, String medicationDescription, String imageUrl) {
        this.disease=disease;
        this.medicationId = medicationId;
        this.medicationName = medicationName;
        this.medicationDescription = medicationDescription;
        this.imageUrl = imageUrl;

    }

    protected MedicationDetails(Parcel in) {
        disease = in.readString();
        medicationId = in.readString();
        medicationName = in.readString();
        medicationDescription = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<MedicationDetails> CREATOR = new Creator<MedicationDetails>() {
        @Override
        public MedicationDetails createFromParcel(Parcel in) {
            return new MedicationDetails(in);
        }

        @Override
        public MedicationDetails[] newArray(int size) {
            return new MedicationDetails[size];
        }
    };

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(String medicationId) {
        this.medicationId = medicationId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getMedicationDescription() {
        return medicationDescription;
    }

    public void setMedicationDescription(String medicationDescription) {
        this.medicationDescription = medicationDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(disease);
        dest.writeString(medicationId);
        dest.writeString(medicationName);
        dest.writeString(medicationDescription);
        dest.writeString(imageUrl);
    }
}
