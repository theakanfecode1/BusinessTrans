package com.example.businesstrans;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.businesstrans.utils.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class AddEventPageFragment extends Fragment {
    private EditText mNameOfEvent;
    private TextView mDateOfEvent;
    private EditText mVenueOfEvent;
    private Button mSubmitEvent;
    private CardView mCardView;
    private Handler mHandler;
    private Runnable mRunnable;
    private boolean mSubmitted;

    public AddEventPageFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_event_page, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.add_event_toolbar);
        Activity context = getActivity();
        mNameOfEvent = view.findViewById(R.id.add_event_name_event);
        mCardView = view.findViewById(R.id.select_date_cardview);
        mDateOfEvent = view.findViewById(R.id.add_event_select_date);
        mVenueOfEvent = view.findViewById(R.id.add_event_venue_of_event);
        mSubmitEvent = view.findViewById(R.id.submit_event);
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
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String selectedDate = day+" "+theMonth(month)+" "+year;
                        mDateOfEvent.setText(selectedDate);

                    }
                },year,month,day);
                datePickerDialog.show();

            }
        });
        mSubmitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginPage.isEmpty(mNameOfEvent.getText().toString()) || LoginPage.isEmpty(mDateOfEvent.getText().toString())
                        || LoginPage.isEmpty(mVenueOfEvent.getText().toString())){
                    Snackbar.make(view,"You must submit data into all fields",Snackbar.LENGTH_LONG).show();
                }else {
                    mHandler.postDelayed(mRunnable,10000);
                    ControllerActivity.showLoadingDialog(getActivity(),"Inserting Event");
                    Event event = new Event();
                    event.setName_of_event(mNameOfEvent.getText().toString());
                    event.setDate_of_event(mDateOfEvent.getText().toString());
                    event.setVenue_of_event(mVenueOfEvent.getText().toString());
                    FirebaseDatabase.getInstance().getReference()
                            .child(getString(R.string.db_usernode))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getString(R.string.db_eventnode))
                            .push()
                            .setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mSubmitted = true;
                            mHandler.removeCallbacks(mRunnable);
                            if(task.isSuccessful()){
                                ControllerActivity.removeLoadingDialog();
                                Toast.makeText(getActivity(),"Event successfully added",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                ControllerActivity.removeLoadingDialog();
                                Toast.makeText(getActivity(),"Event not successfully added",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mSubmitted = true;
                            mHandler.removeCallbacks(mRunnable);
                            Toast.makeText(getActivity(),"Couldn't add Event",Toast.LENGTH_SHORT).show(); }
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
