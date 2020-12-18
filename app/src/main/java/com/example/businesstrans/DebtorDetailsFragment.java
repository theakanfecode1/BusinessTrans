package com.example.businesstrans;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.example.businesstrans.utils.Debtor;
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
public class DebtorDetailsFragment extends Fragment {


    private Debtor mDebtor;
    private EditText debtorCompanyName;
    private EditText debtorTypeOfTransaction;
    private EditText debtorAmountIndebted;
    private EditText debtorLocationOfTransaction;
    private Button updateButton;
    private Handler mHandler;
    private Runnable mRunnable;
    private boolean mSubmitted;

    public DebtorDetailsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mDebtor = bundle.getParcelable("debtor_object");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_debtor_details, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.debtor_details_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
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

        debtorCompanyName = view.findViewById(R.id.debtor_details_company_name);
        debtorTypeOfTransaction = view.findViewById(R.id.debtor_details_type_of_transaction);
        debtorAmountIndebted = view.findViewById(R.id.debtor_details_amount_indebted);
        debtorLocationOfTransaction = view.findViewById(R.id.debtor_details_location_of_transaction);
        updateButton = view.findViewById(R.id.update_debtor);
        debtorCompanyName.setText(mDebtor.getDebtor_company_name());
        debtorTypeOfTransaction.setText(mDebtor.getDebtor_type_of_transaction());
        debtorAmountIndebted.setText(mDebtor.getDebtor_value_of_transaction());
        debtorLocationOfTransaction.setText(mDebtor.getDebtor_location_of_transaction());
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginPage.isEmpty(debtorCompanyName.getText().toString())||LoginPage.isEmpty(debtorTypeOfTransaction.getText().toString()) ||
                        LoginPage.isEmpty(debtorAmountIndebted.getText().toString()) || LoginPage.isEmpty(debtorLocationOfTransaction.getText().toString())
                ){
                    Snackbar.make(view,"You must submit data into all fields",Snackbar.LENGTH_LONG).show();

                }else{
                    mHandler.postDelayed(mRunnable,10000);
                    ControllerActivity.showLoadingDialog(getActivity(),"Updating Debtor");
                    String dateLocale = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
                    int monthInt = Calendar.MONTH;
                    String month = theMonth(monthInt);
                    String[] dateArray = dateLocale.split("/");
                    String  date = dateArray[2]+" "+month+" "+dateArray[0];
                    Debtor debtor = new Debtor();
                    debtor.setDebtor_company_name(debtorCompanyName.getText().toString());
                    debtor.setDebtor_type_of_transaction(debtorTypeOfTransaction.getText().toString());
                    debtor.setDebtor_value_of_transaction(debtorAmountIndebted.getText().toString());
                    debtor.setDebtor_location_of_transaction(debtorLocationOfTransaction.getText().toString());
                    debtor.setDebtor_date_of_transaction(date);
                    FirebaseDatabase.getInstance().getReference()
                            .child(getString(R.string.db_usernode))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getString(R.string.db_debtornode))
                            .child(mDebtor.getId())
                            .setValue(debtor).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mSubmitted = true;
                            mHandler.removeCallbacks(mRunnable);
                            if(task.isSuccessful()){
                                ControllerActivity.removeLoadingDialog();
                                Toast.makeText(getActivity(),"Debtor successfully updated",Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }
                            else{
                                Toast.makeText(getActivity(),"Debtor not successfully updated",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mSubmitted = true;
                            mHandler.removeCallbacks(mRunnable);
                            Toast.makeText(getActivity(),"Couldn't update Debtor",Toast.LENGTH_SHORT).show(); }
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
