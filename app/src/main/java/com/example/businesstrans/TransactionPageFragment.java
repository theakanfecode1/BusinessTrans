package com.example.businesstrans;


import android.app.Notification;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.businesstrans.adapters.TransactionAdapter;
import com.example.businesstrans.utils.Transaction;
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
public class TransactionPageFragment extends Fragment {
    private static final String TAG = "TransactionPageFragment";
    private RecyclerView mRecyclerView;
    private ArrayList<Transaction> mTransactions = new ArrayList<>();
    private TextView mNoTransaction;
    private TextView mNoTransactionNetwork;
    private DatabaseReference mReference;
    private boolean mDataIsLoaded = false;
    private CountDownTimer mCountDownTimer;
    private Handler mHandler;
    private Runnable mRunnable;
    private FragmentInflater mFragmentInflater;

    public TransactionPageFragment() {
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
        View view = inflater.inflate(R.layout.fragment_transaction_page, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.transaction_toolbar);
        mRecyclerView = view.findViewById(R.id.transaction_recyclerview);
        mNoTransaction = view.findViewById(R.id.no_transaction);
        mNoTransactionNetwork = view.findViewById(R.id.no_transaction_connection);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ControllerActivity.showLoadingDialog(getActivity(), "Fetching Transactions");
        mNoTransactionNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ControllerActivity.showLoadingDialog(getActivity(), "Fetching Transactions");
                getUserTransactionObject();
            }
        });

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mDataIsLoaded) {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    ControllerActivity.removeLoadingDialog();
                    mNoTransactionNetwork.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Check your Network Connection.", Toast.LENGTH_SHORT).show();
                }

            }
        };
        getUserTransactionObject();


        return view;
    }

    private void getUserTransactionObject() {

        mHandler.postDelayed(mRunnable, 10000);
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoTransactionNetwork.setVisibility(View.INVISIBLE);
        mReference = FirebaseDatabase.getInstance().getReference();
        Query query = mReference.child(getString(R.string.db_usernode))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.db_transactionnode));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTransactions.clear();
                mDataIsLoaded = true;
                mHandler.removeCallbacks(mRunnable);
                for (DataSnapshot dataSnapshott : dataSnapshot.getChildren()) {
                    Transaction transaction = dataSnapshott.getValue(Transaction.class);
                    transaction.setId(dataSnapshott.getKey());
                    mTransactions.add(transaction);
                }
                if (mTransactions.size() < 1) {
                    mNoTransaction.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    ControllerActivity.removeLoadingDialog();


                }else {
                    TransactionAdapter transactionAdapter = new TransactionAdapter(getActivity(), mTransactions);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setAdapter(transactionAdapter);
                    transactionAdapter.notifyDataSetChanged();
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
