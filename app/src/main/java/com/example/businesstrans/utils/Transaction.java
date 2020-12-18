package com.example.businesstrans.utils;


import android.os.Parcel;
import android.os.Parcelable;

public class Transaction implements Parcelable {
    private String id;
    private String company_name;
    private String type_of_transaction;
    private String value_of_transaction;
    private String location_of_transaction;
    private String date_of_tansaction;
    private String status_of_transaction;

    public  Transaction(){

    }

    public Transaction(String id, String company_name, String type_of_transaction, String value_of_transaction, String location_of_transaction, String date_of_tansaction, String status_of_transaction) {
        this.id = id;
        this.company_name = company_name;
        this.type_of_transaction = type_of_transaction;
        this.value_of_transaction = value_of_transaction;
        this.location_of_transaction = location_of_transaction;
        this.date_of_tansaction = date_of_tansaction;
        this.status_of_transaction = status_of_transaction;
    }

    protected Transaction(Parcel in) {
        id = in.readString();
        company_name = in.readString();
        type_of_transaction = in.readString();
        value_of_transaction = in.readString();
        location_of_transaction = in.readString();
        date_of_tansaction = in.readString();
        status_of_transaction = in.readString();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getType_of_transaction() {
        return type_of_transaction;
    }

    public void setType_of_transaction(String type_of_transaction) {
        this.type_of_transaction = type_of_transaction;
    }

    public String getValue_of_transaction() {
        return value_of_transaction;
    }

    public void setValue_of_transaction(String value_of_transaction) {
        this.value_of_transaction = value_of_transaction;
    }

    public String getLocation_of_transaction() {
        return location_of_transaction;
    }

    public void setLocation_of_transaction(String location_of_transaction) {
        this.location_of_transaction = location_of_transaction;
    }

    public String getDate_of_tansaction() {
        return date_of_tansaction;
    }

    public void setDate_of_tansaction(String date_of_tansaction) {
        this.date_of_tansaction = date_of_tansaction;
    }

    public String getStatus_of_transaction() {
        return status_of_transaction;
    }

    public void setStatus_of_transaction(String status_of_transaction) {
        this.status_of_transaction = status_of_transaction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(company_name);
        parcel.writeString(type_of_transaction);
        parcel.writeString(value_of_transaction);
        parcel.writeString(location_of_transaction);
        parcel.writeString(date_of_tansaction);
        parcel.writeString(status_of_transaction);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", company_name='" + company_name + '\'' +
                ", type_of_transaction='" + type_of_transaction + '\'' +
                ", value_of_transaction='" + value_of_transaction + '\'' +
                ", location_of_transaction='" + location_of_transaction + '\'' +
                ", date_of_tansaction='" + date_of_tansaction + '\'' +
                ", status_of_transaction='" + status_of_transaction + '\'' +
                '}';
    }
}

