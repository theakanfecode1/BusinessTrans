package com.example.businesstrans;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.businesstrans.adapters.EventAdapter;
import com.example.businesstrans.utils.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventPageFragment extends Fragment {

    ArrayList<Event> mEvents = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private TextView mNoEvent;
    private TextView mNoEventNetwork;
    private DatabaseReference mConnectedRef;
    private ValueEventListener mValueEventListener;
    private Handler mHandler;
    private Runnable mRunnable;
    private boolean mDataIsLoaded = false;
    private FragmentInflater mFragmentInflater;


    public EventPageFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mFragmentInflater = (FragmentInflater) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_page, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.event_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ControllerActivity.showLoadingDialog(getActivity(), "Fetching Events");
        mRecyclerView = view.findViewById(R.id.event_recyclerview);
        mNoEvent = view.findViewById(R.id.no_event);
        mNoEventNetwork = view.findViewById(R.id.no_event_network);
        mNoEventNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ControllerActivity.showLoadingDialog(getActivity(), "Fetching Events");
                getUserEventData();
            }
        });
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mDataIsLoaded) {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    ControllerActivity.removeLoadingDialog();
                    mNoEventNetwork.setVisibility(View.VISIBLE);
                    mNoEvent.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "Check your Network Connection.", Toast.LENGTH_SHORT).show();
                }

            }
        };

        getUserEventData();

        return view;
    }

    private void getUserEventData() {

        mHandler.postDelayed(mRunnable, 10000);
        mNoEventNetwork.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_usernode))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.db_eventnode));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mEvents.clear();
                mHandler.removeCallbacks(mRunnable);
                mDataIsLoaded =  true;
                for (DataSnapshot dataSnapshott : dataSnapshot.getChildren()) {
                    Event event = dataSnapshott.getValue(Event.class);
                    event.setId(dataSnapshott.getKey());
                    mEvents.add(event);
                }
                if (mEvents.size() < 1) {
                    mNoEvent.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    ControllerActivity.removeLoadingDialog();
                } else {
                    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                    EventAdapter eventAdapter = new EventAdapter(getActivity(), mEvents);
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setAdapter(eventAdapter);
                    ControllerActivity.removeLoadingDialog();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Database Error occurred.", Toast.LENGTH_SHORT).show();
                ControllerActivity.removeLoadingDialog();
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    ControllerActivity controllerActivity = new ControllerActivity();
                    controllerActivity.clearBackStack();
                    mFragmentInflater.inflateHomeFragment();
                    return true;
                }

                return false;
            }
        });
    }

}
