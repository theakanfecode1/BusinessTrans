package com.example.businesstrans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.businesstrans.utils.Debtor;
import com.example.businesstrans.utils.Event;
import com.example.businesstrans.utils.Transaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ControllerActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,FragmentInflater{

    private static final String TAG = "ControllerActivity";
    private HomePageFragment mHomePageFragment;
    private TransactionPageFragment mTransactionPageFragment;
    private DebtorPageFragment mDebtorPageFragment;
    private EventPageFragment mEventPageFragment;
    private BottomNavigationView mBottomNavigationView;
    private TransactionDetailsFragment mTransactionDetailsFragment;
    private DebtorDetailsFragment mDebtorDetailsFragment;
    private EventDetailsFragment mEventDetailsFragment;
    public static Dialog loadingDialog;
    private static boolean sConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        mBottomNavigationView =findViewById(R.id.bottom_nav);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        inflateHomeFragment();



    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.home_navbutton:
                Log.d(TAG, "onNavigationItemSelected: "+ "Home ");

                inflateHomeFragment();
                item.setChecked(true);
            break;
            case R.id.transaction_navbutton:
                Log.d(TAG, "onNavigationItemSelected: "+ "Transaction ");
                showTransactionsFragment();
                item.setChecked(true);
            break;

            case R.id.debtor_navbutton:
                Log.d(TAG, "onNavigationItemSelected: "+ "Debtor ");
                showDebtorFragment();
                item.setChecked(true);
            break;

            case R.id.event_navbutton:
                Log.d(TAG, "onNavigationItemSelected: "+ "Event ");
                showEventFragment();
                item.setChecked(true);

             break;

        }

        return false;
    }

    private void showEventFragment() {
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mEventPageFragment = new EventPageFragment();
            fragmentTransaction.replace(R.id.main_content,mEventPageFragment,getResources().getString(R.string.event_tag));
            fragmentTransaction.addToBackStack(getString(R.string.event_tag));
            fragmentTransaction.commit();

    }

    private void showDebtorFragment() {
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            mDebtorPageFragment = new DebtorPageFragment();
            fragmentTransaction.replace(R.id.main_content,mDebtorPageFragment,getResources().getString(R.string.debtor_tag));
            fragmentTransaction.addToBackStack(getString(R.string.debtor_tag));
            fragmentTransaction.commit();

    }

    private void showTransactionsFragment() {
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            mTransactionPageFragment = new TransactionPageFragment();
            fragmentTransaction.replace(R.id.main_content,mTransactionPageFragment,getResources().getString(R.string.transaction_tag));
            fragmentTransaction.addToBackStack(getString(R.string.transaction_tag));
            fragmentTransaction.commit();


    }


    @Override
    public void inflateTransactionDetailsFragment(Transaction transaction) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mTransactionDetailsFragment = new TransactionDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("transaction_object",transaction);
        mTransactionDetailsFragment.setArguments(args);
        fragmentTransaction.replace(R.id.main_content,mTransactionDetailsFragment,getResources().getString(R.string.transaction_details));
        fragmentTransaction.addToBackStack(getString(R.string.transaction_details));
        fragmentTransaction.commit();
    }

    @Override
    public void inflateDebtorDetailsFragment(Debtor debtor) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mDebtorDetailsFragment = new DebtorDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("debtor_object",debtor);
        mDebtorDetailsFragment.setArguments(args);
        fragmentTransaction.replace(R.id.main_content,mDebtorDetailsFragment,getResources().getString(R.string.debtor_details));
        fragmentTransaction.addToBackStack(getString(R.string.debtor_details));
        fragmentTransaction.commit();
    }

    @Override
    public void inflateEventDetailsFragment(Event event) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mEventDetailsFragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("event_object",event);
        mEventDetailsFragment.setArguments(args);
        fragmentTransaction.replace(R.id.main_content,mEventDetailsFragment,getResources().getString(R.string.event_details));
        fragmentTransaction.addToBackStack(getString(R.string.event_details));
        fragmentTransaction.commit();

    }

    @Override
    public void inflateHomeFragment() {
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mHomePageFragment = new HomePageFragment();
        fragmentTransaction.replace(R.id.main_content,mHomePageFragment,getResources().getString(R.string.home_tag));
        fragmentTransaction.addToBackStack(getString(R.string.home_tag));
        fragmentTransaction.commit();
    }

    private void checkAuthenticationState(){
        Log.d(TAG, "checkAuthenticationState: checking authentication state");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Log.d(TAG, "checkAuthenticationState: user is null , navigation back to login screen.");
            Intent intent = new Intent(ControllerActivity.this,LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{
            Log.d(TAG, "checkAuthenticationState: user is authenticated");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }
    public static void showLoadingDialog(Activity activity, String message) {
        loadingDialog = new Dialog(activity,R.style.DialogTheme);
        loadingDialog.setContentView(R.layout.loading_screen);
        TextView messageTitle = loadingDialog.findViewById(R.id.dialog_message);
        messageTitle.setText(message);

        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();

    }
    public static void removeLoadingDialog(){
        loadingDialog.dismiss();
    }

    public void clearBackStack(){
        while (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStackImmediate();
        }
    }
    public static String returnValueString(String string){
        String holder = string.replaceAll(",","");
        return holder;
    }

}
