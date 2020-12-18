package com.example.businesstrans;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.businesstrans.utils.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTransactionPageFragment extends Fragment {


    private EditText mCompanyName;
    private EditText mTypeOfTransaction;
    private EditText mValue;
    private EditText mLocationOfTransaction;
    private Button mSubmitTransaction;
    private Handler mHandler;
    private Runnable mRunnable;
    private boolean mSubmitted = false;

    public AddTransactionPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction_page, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.add_transaction_toolbar);
        mCompanyName = view.findViewById(R.id.add_transaction_company_name);
        mTypeOfTransaction = view.findViewById(R.id.add_transaction_type_of_transaction);
        mValue = view.findViewById(R.id.add_transaction_value_of_transaction);
        mLocationOfTransaction = view.findViewById(R.id.add_transaction_location_of_transaction);
        mSubmitTransaction = view.findViewById(R.id.submit_transaction);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        toolbar.setTitle("");
        activity.setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(!mSubmitted){
                    Toast.makeText(getActivity(),"Check your internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        };
        mSubmitTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginPage.isEmpty(mCompanyName.getText().toString())||LoginPage.isEmpty(mTypeOfTransaction.getText().toString()) ||
                LoginPage.isEmpty(mValue.getText().toString()) || LoginPage.isEmpty(mLocationOfTransaction.getText().toString())
                ){
                    Snackbar.make(view,"You must submit data into all fields",Snackbar.LENGTH_LONG).show();

                }
                else{
                    mHandler.postDelayed(mRunnable,10000);
                    ControllerActivity.showLoadingDialog(getActivity(),"Inserting Transaction");
                    Transaction transaction = new Transaction();
                    String dateLocale = new SimpleDateFormat("yyyy/MM/dd",Locale.getDefault()).format(new Date());
                    int monthInt = Calendar.MONTH;
                    String month = theMonth(monthInt);
                    String[] dateArray = dateLocale.split("/");
                    String  date = dateArray[2]+" "+month+" "+dateArray[0];
                    transaction.setCompany_name(mCompanyName.getText().toString());
                    transaction.setType_of_transaction(mTypeOfTransaction.getText().toString());
                    transaction.setValue_of_transaction(ControllerActivity.returnValueString(mValue.getText().toString()));
                    transaction.setLocation_of_transaction(mLocationOfTransaction.getText().toString());
                    transaction.setStatus_of_transaction("Pending");
                    transaction.setDate_of_tansaction(date);
                    FirebaseDatabase.getInstance().getReference()
                            .child(getString(R.string.db_usernode))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getString(R.string.db_transactionnode))
                            .push()
                            .setValue(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mSubmitted = true;
                            mHandler.removeCallbacks(mRunnable);
                            if(task.isSuccessful()){
                                ControllerActivity.removeLoadingDialog();
                                Toast.makeText(getActivity(),"Transaction successfully added",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                ControllerActivity.removeLoadingDialog();
                                Toast.makeText(getActivity(),"Transaction not successfully added",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mSubmitted = true;
                            mHandler.removeCallbacks(mRunnable);
                            ControllerActivity.removeLoadingDialog();
                            Toast.makeText(getActivity(),"Couldn't add Transaction",Toast.LENGTH_SHORT).show(); }
                    });
                }


            }
        });



        return view;
    }
    public static String theMonth(int month){
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }
    @Override
    public void onPause() {
        super.onPause();
        closeKeyBoard();
    }

    private void closeKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

}
