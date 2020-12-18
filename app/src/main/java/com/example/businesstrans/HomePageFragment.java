package com.example.businesstrans;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.businesstrans.utils.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment {

    private static final String TAG = "HomePageFragment";


    private CardView mAddTransactionButton;
    private CardView mAddDebtorButton;
    private CardView mAddEventButton;
    private AddDebtorPageFragment mAddDebtorPageFragment;
    private AddTransactionPageFragment mAddTransactionPageFragment;
    private AddEventPageFragment mAddEventPageFragment;
    private TextView mLogOut;
    private TextView mGoToSettings;
    private TextView mUserName;
    private String userNameString;
    private int mBackInt;
    private TextView mTotalTransaction;
    private TextView mTryAgainBtn;
    private Handler mHandler;
    private Runnable mRunnable;
    private boolean mDataIsLoaded = false;
    private long mTotalValue;
    ArrayList<Transaction> mTransactions = new ArrayList<>();

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAuthenticationState();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        mUserName = view.findViewById(R.id.username_home);
        mAddTransactionButton = view.findViewById(R.id.add_transaction);
        mAddDebtorButton = view.findViewById(R.id.add_debtor);
        mAddEventButton = view.findViewById(R.id.add_event);
        mLogOut = view.findViewById(R.id.logout);
        mTotalTransaction = view.findViewById(R.id.total_transaction_value_home);
        mTryAgainBtn = view.findViewById(R.id.try_again_home);
        mGoToSettings = view.findViewById(R.id.go_to_settings);
        ControllerActivity.showLoadingDialog(getActivity(),"Fetching History");
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mDataIsLoaded) {
                    ControllerActivity.removeLoadingDialog();
                    mTryAgainBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Check your Network Connection.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        mTryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ControllerActivity.showLoadingDialog(getActivity(),"Fetching History");
                getTotalTransaction();
            }
        });
        mUserName.setText(userNameString);
        getTotalTransaction();
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(),LoginPage.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        mGoToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),SettingPage.class);
                startActivity(intent);

            }
        });

        mAddTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    mAddTransactionPageFragment = new AddTransactionPageFragment();
                    fragmentTransaction.replace(R.id.main_content,mAddTransactionPageFragment,getString(R.string.add_transaction));
                    fragmentTransaction.addToBackStack(getResources().getString(R.string.add_transaction));
                    fragmentTransaction.commit();

            }
        });
        mAddDebtorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    mAddDebtorPageFragment = new AddDebtorPageFragment();
                    fragmentTransaction.replace(R.id.main_content,mAddDebtorPageFragment,getString(R.string.add_debtor));
                    fragmentTransaction.addToBackStack(getResources().getString(R.string.add_debtor));
                    fragmentTransaction.commit();

            }
        });
        mAddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    mAddEventPageFragment = new AddEventPageFragment();
                    fragmentTransaction.replace(R.id.main_content,mAddEventPageFragment,getString(R.string.add_event));
                    fragmentTransaction.addToBackStack(getResources().getString(R.string.add_event));
                    fragmentTransaction.commit();

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void getTotalTransaction() {

        mHandler.postDelayed(mRunnable,10000);
        mTryAgainBtn.setVisibility(View.INVISIBLE);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query query = db.child(getString(R.string.db_usernode))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.db_transactionnode));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mDataIsLoaded = true;
                        mHandler.removeCallbacks(mRunnable);
                        mTransactions.clear();

                        for(DataSnapshot dataSnapshott : dataSnapshot.getChildren()){
                            Transaction transaction = dataSnapshott.getValue(Transaction.class);
                            mTransactions.add(transaction);

                        }
                        if(mTransactions.size() < 1){
                            Toast.makeText(getActivity(), "No Transaction yet.", Toast.LENGTH_SHORT).show();
                            mTotalTransaction.setText("₦ 0.00");
                            ControllerActivity.removeLoadingDialog();
                        }else{
                            mTotalValue = 0;
                            for(Transaction transaction : mTransactions){
                                String valueString = transaction.getValue_of_transaction();
                                mTotalValue += Integer.parseInt(valueString);
                                DecimalFormat decimalFormat = new DecimalFormat();
                                String formatted =  decimalFormat.format(mTotalValue);
                                mTotalTransaction.setText("₦ "+formatted);
                                ControllerActivity.removeLoadingDialog();

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void checkAuthenticationState(){
        Log.d(TAG, "checkAuthenticationState: checking authentication state");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Log.d(TAG, "checkAuthenticationState: user is null , navigation back to login screen.");
            Intent intent = new Intent(getActivity(),LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }else{
            userNameString = "Hello "+user.getDisplayName();
            Log.d(TAG, "checkAuthenticationState: user is authenticated");
        }

    }
    @Override
    public void onResume() {

        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    mBackInt++;
                    if(mBackInt ==2 ){
                        getActivity().finish();
                    }else{
                        Toast.makeText(getActivity(), "Tap again to close the app.", Toast.LENGTH_SHORT).show();
                    }

                    return true;

                }

                return false;
            }
        });
    }

}
