package com.example.businesstrans;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetailsFragment extends Fragment {


    private Event mEvent;
    private EditText mNameOfEvent;
    private TextView mDateOfEvent;
    private EditText mVenueOfEvent;
    private Button updateEvent;
    private Handler mHandler;
    private Runnable mRunnable;
    private boolean mSubmitted;

    public EventDetailsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mEvent = bundle.getParcelable("event_object");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.event_details_toolbar);
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


        mDateOfEvent = view.findViewById(R.id.event_details_date_of_event);
        mNameOfEvent = view.findViewById(R.id.event_details_name_of_event);
        mVenueOfEvent = view.findViewById(R.id.event_details_venue_of_event);
        updateEvent = view.findViewById(R.id.update_event);
        mDateOfEvent.setOnClickListener(new View.OnClickListener() {
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
        mNameOfEvent.setText(mEvent.getName_of_event());
        mDateOfEvent.setText(mEvent.getDate_of_event());
        mVenueOfEvent.setText(mEvent.getVenue_of_event());
        updateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginPage.isEmpty(mNameOfEvent.getText().toString()) || LoginPage.isEmpty(mDateOfEvent.getText().toString())
                        || LoginPage.isEmpty(mVenueOfEvent.getText().toString())){
                    Snackbar.make(view,"You must submit data into all fields",Snackbar.LENGTH_LONG).show();
                }else{
                    mHandler.postDelayed(mRunnable,10000);
                    ControllerActivity.showLoadingDialog(getActivity(),"Updating Event");
                    Event event =  new Event();
                    event.setName_of_event(mNameOfEvent.getText().toString());
                    event.setDate_of_event(mDateOfEvent.getText().toString());
                    event.setVenue_of_event(mVenueOfEvent.getText().toString());

                    FirebaseDatabase.getInstance().getReference()
                            .child(getString(R.string.db_usernode))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getString(R.string.db_eventnode))
                            .child(mEvent.getId())
                            .setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mSubmitted = true;
                            mHandler.removeCallbacks(mRunnable);
                            if(task.isSuccessful()){
                                ControllerActivity.removeLoadingDialog();
                                Toast.makeText(getActivity(),"Event successfully updated",Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }
                            else{
                                ControllerActivity.removeLoadingDialog();
                                Toast.makeText(getActivity(),"Event not successfully updated",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mSubmitted = true;
                            mHandler.removeCallbacks(mRunnable);
                            Toast.makeText(getActivity(),"Couldn't update Evrnt",Toast.LENGTH_SHORT).show(); }
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
