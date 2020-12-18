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
import android.widget.Spinner;
import android.widget.Toast;

import com.example.businesstrans.utils.Debtor;
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
public class TransactionDetailsFragment extends Fragment {


    private Transaction mTransaction;
    private Spinner mTransactionStatusSpinner;
    private EditText mCompanyName;
    private EditText mTypeOfTransaction;
    private EditText mValueOfTransaction;
    private EditText mLocationOfTransaction;
    private Button mUpdateButton;
    private String mTypeOfTransactionEntry;
    private String mCompanyNameEntry;
    private String mValueOfEntry;
    private String mLocationEntry;
    private String mSelectedStatus;
    private Handler mHandler;
    private Runnable mRunnable;
    private boolean mSubmitted;

    public TransactionDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mTransaction = bundle.getParcelable("transaction_object");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_details, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.transaction_details_toolbar);
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

        mTransactionStatusSpinner = view.findViewById(R.id.transaction_status_spinner);
        mCompanyName = view.findViewById(R.id.transaction_details_company_name);
        mTypeOfTransaction = view.findViewById(R.id.transaction_details_type_of_transaction);
        mValueOfTransaction = view.findViewById(R.id.transaction_details_value_of_transaction);
        mLocationOfTransaction = view.findViewById(R.id.transaction_details_location_of_transaction);
        mUpdateButton = view.findViewById(R.id.update_transaction);

        mCompanyName.setText(mTransaction.getCompany_name());
        mTypeOfTransaction.setText(mTransaction.getType_of_transaction());
        mValueOfTransaction.setText(mTransaction.getValue_of_transaction());
        mLocationOfTransaction.setText(mTransaction.getLocation_of_transaction());
        if(mTransaction.getStatus_of_transaction().equals("Pending")){
            mTransactionStatusSpinner.setSelection(0);
        }
        else if(mTransaction.getStatus_of_transaction().equals("Completed")){
            mTransactionStatusSpinner.setSelection(1);
        }
        else{
            mTransactionStatusSpinner.setSelection(2);
        }
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginPage.isEmpty(mCompanyName.getText().toString())||LoginPage.isEmpty(mTypeOfTransaction.getText().toString()) ||
                        LoginPage.isEmpty(mValueOfTransaction.getText().toString()) || LoginPage.isEmpty(mLocationOfTransaction.getText().toString())
                ){
                    Snackbar.make(view,"You must submit data into all fields",Snackbar.LENGTH_LONG).show();

                }else{
                    mHandler.postDelayed(mRunnable,10000);
                    ControllerActivity.showLoadingDialog(getActivity(),"Updating Transaction");
                    getUserEntry();
                    String dateLocale = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
                    int monthInt = Calendar.MONTH;
                    String month = theMonth(monthInt);
                    String[] dateArray = dateLocale.split("/");
                    String  date = dateArray[2]+" "+month+" "+dateArray[0];
                    Transaction transaction = new Transaction();
                    transaction.setCompany_name(mCompanyName.getText().toString());
                    transaction.setType_of_transaction(mTypeOfTransaction.getText().toString());
                    transaction.setValue_of_transaction(ControllerActivity.returnValueString(mValueOfTransaction.getText().toString()));
                    transaction.setLocation_of_transaction(mLocationOfTransaction.getText().toString());
                    transaction.setStatus_of_transaction(mSelectedStatus);
                    transaction.setDate_of_tansaction(date);
                    FirebaseDatabase.getInstance().getReference()
                            .child(getString(R.string.db_usernode))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getString(R.string.db_transactionnode))
                            .child(mTransaction.getId())
                            .setValue(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mSubmitted = true;
                            mHandler.removeCallbacks(mRunnable);
                            if(task.isSuccessful()){
                                ControllerActivity.removeLoadingDialog();
                                Toast.makeText(getActivity(),"Transaction successfully updated",Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }
                            else{
                                ControllerActivity.removeLoadingDialog();
                                Toast.makeText(getActivity(),"Transaction not successfully updated",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mSubmitted = true;
                            mHandler.removeCallbacks(mRunnable);
                            Toast.makeText(getActivity(),"Couldn't update Transaction",Toast.LENGTH_SHORT).show(); }
                    });;
                }

            }
        });



        return view;
    }

    private void getUserEntry() {
        int selectedPositon = mTransactionStatusSpinner.getSelectedItemPosition();
        if(selectedPositon == 0){
            mSelectedStatus = "Pending";
        }
        else if(selectedPositon == 1){
            mSelectedStatus = "Completed";

        }
        else{
            mSelectedStatus = "Failed";
        }
        mTypeOfTransactionEntry = mTypeOfTransaction.getText().toString();
        mCompanyNameEntry = mCompanyName.getText().toString();
        mValueOfEntry = mValueOfTransaction.getText().toString();
        mLocationEntry = mLocationOfTransaction.getText().toString();


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
