package com.example.businesstrans;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.businesstrans.adapters.DebtorAdapter;
import com.example.businesstrans.utils.Debtor;
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
public class DebtorPageFragment extends Fragment {

    private ArrayList<Debtor> mDebtors = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private TextView mNoDebtor;
    private TextView mNoDebtorNewtork;
    private boolean mDataIsLoaded = false;
    private Handler mHandler;
    private Runnable mRunnable;
    private FragmentInflater mFragmentInflater;


    public DebtorPageFragment() {
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
        View view = inflater.inflate(R.layout.fragment_debtor_page, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.debtor_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ControllerActivity.showLoadingDialog(getActivity(), "Fetching Debtors");
        mRecyclerView = view.findViewById(R.id.debtor_recyclerview);
        mNoDebtor = view.findViewById(R.id.no_debtor);
        mNoDebtorNewtork = view.findViewById(R.id.no_debtor_network);
        mNoDebtorNewtork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ControllerActivity.showLoadingDialog(getActivity(), "Fetching Debtors");
                getUserDebtorData();
            }
        });
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mDataIsLoaded) {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    ControllerActivity.removeLoadingDialog();
                    mNoDebtorNewtork.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Check your Network Connection.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        getUserDebtorData();

        return view;
    }

    private void getUserDebtorData() {

        mHandler.postDelayed(mRunnable, 10000);
        mNoDebtorNewtork.setVisibility(View.INVISIBLE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_usernode))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.db_debtornode));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDebtors.clear();
                mDataIsLoaded = true;
                mHandler.removeCallbacks(mRunnable);
                for (DataSnapshot dataSnapshott : dataSnapshot.getChildren()) {
                    Debtor debtor = dataSnapshott.getValue(Debtor.class);
                    debtor.setId(dataSnapshott.getKey());
                    mDebtors.add(debtor);
                }
                if (mDebtors.size() < 1) {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    mNoDebtor.setVisibility(View.VISIBLE);
                    ControllerActivity.removeLoadingDialog();
                } else {

                    DebtorAdapter debtorAdapter = new DebtorAdapter(getActivity(), mDebtors);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setAdapter(debtorAdapter);
                    ControllerActivity.removeLoadingDialog();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
