package com.example.businesstrans.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Debtor implements Parcelable {
    private String id;
    private String debtor_company_name;
    private String debtor_type_of_transaction;
    private String debtor_value_of_transaction;
    private String debtor_location_of_transaction;
    private String debtor_date_of_transaction;

    public  Debtor(){

    }

    public Debtor(String id, String debtor_company_name, String debtor_type_of_transaction, String debtor_value_of_transaction, String debtor_location_of_transaction, String debtor_date_of_transaction) {
        this.id = id;
        this.debtor_company_name = debtor_company_name;
        this.debtor_type_of_transaction = debtor_type_of_transaction;
        this.debtor_value_of_transaction = debtor_value_of_transaction;
        this.debtor_location_of_transaction = debtor_location_of_transaction;
        this.debtor_date_of_transaction = debtor_date_of_transaction;
    }

    protected Debtor(Parcel in) {
        id = in.readString();
        debtor_company_name = in.readString();
        debtor_type_of_transaction = in.readString();
        debtor_value_of_transaction = in.readString();
        debtor_location_of_transaction = in.readString();
        debtor_date_of_transaction = in.readString();
    }

    public static final Creator<Debtor> CREATOR = new Creator<Debtor>() {
        @Override
        public Debtor createFromParcel(Parcel in) {
            return new Debtor(in);
        }

        @Override
        public Debtor[] newArray(int size) {
            return new Debtor[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDebtor_company_name() {
        return debtor_company_name;
    }

    public void setDebtor_company_name(String debtor_company_name) {
        this.debtor_company_name = debtor_company_name;
    }

    public String getDebtor_type_of_transaction() {
        return debtor_type_of_transaction;
    }

    public void setDebtor_type_of_transaction(String debtor_type_of_transaction) {
        this.debtor_type_of_transaction = debtor_type_of_transaction;
    }

    public String getDebtor_value_of_transaction() {
        return debtor_value_of_transaction;
    }

    public void setDebtor_value_of_transaction(String debtor_value_of_transaction) {
        this.debtor_value_of_transaction = debtor_value_of_transaction;
    }

    public String getDebtor_location_of_transaction() {
        return debtor_location_of_transaction;
    }

    public void setDebtor_location_of_transaction(String debtor_location_of_transaction) {
        this.debtor_location_of_transaction = debtor_location_of_transaction;
    }

    public String getDebtor_date_of_transaction() {
        return debtor_date_of_transaction;
    }

    public void setDebtor_date_of_transaction(String debtor_date_of_transaction) {
        this.debtor_date_of_transaction = debtor_date_of_transaction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(debtor_company_name);
        parcel.writeString(debtor_type_of_transaction);
        parcel.writeString(debtor_value_of_transaction);
        parcel.writeString(debtor_location_of_transaction);
        parcel.writeString(debtor_date_of_transaction);
    }

    @Override
    public String toString() {
        return "Debtor{" +
                "id='" + id + '\'' +
                ", debtor_company_name='" + debtor_company_name + '\'' +
                ", debtor_type_of_transaction='" + debtor_type_of_transaction + '\'' +
                ", debtor_value_of_transaction='" + debtor_value_of_transaction + '\'' +
                ", debtor_location_of_transaction='" + debtor_location_of_transaction + '\'' +
                ", debtor_date_of_transaction='" + debtor_date_of_transaction + '\'' +
                '}';
    }
}
