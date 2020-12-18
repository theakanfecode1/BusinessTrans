package com.example.businesstrans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.businesstrans.utils.TotalTransaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccount extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button mCreateAccount;
    private String mEmailString;
    private String mPasswordString;
    private static final String TAG = "CreateAccount";
    private EditText mFirstName;
    private Dialog loadingDialog;
    private Activity mActivity;
    private EditText mSurname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mActivity = this;
        mFirstName = findViewById(R.id.first_name);
        mEmail = findViewById(R.id.email);
        mSurname = findViewById(R.id.last_name);
        mPassword = findViewById(R.id.password_create);
        mCreateAccount = findViewById(R.id.submit_create);

        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginPage.isEmpty(mEmail.getText().toString()) || LoginPage.isEmpty(mFirstName.getText().toString())
                        || LoginPage.isEmpty(mPassword.getText().toString())|| LoginPage.isEmpty(mSurname.getText().toString())){
                    Snackbar.make(view,"You must submit data into all fields",Snackbar.LENGTH_LONG).show();
                }else{
                    showLoadingDialog(mActivity,"Creating Account");
                    mEmailString = mEmail.getText().toString();
                    mPasswordString = mPassword.getText().toString();
                    createAccount(mEmailString,mPasswordString);
                }

            }
        });

    }
    private void createAccount(String email,String password){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "onComplete: "+task.isSuccessful());
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: AuthState:"+ FirebaseAuth.getInstance().getCurrentUser().getUid());
                    sendVerificationEmail();
                    removeLoadingDialog();
                    FirebaseAuth.getInstance().signOut();
                }
                else{
                    removeLoadingDialog();
                    Toast.makeText(CreateAccount.this,"Unable to Register",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void sendVerificationEmail() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(mFirstName.getText().toString())
                                        .build();
                                user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });
                                Toast.makeText(CreateAccount.this, "Sent Verification Email", Toast.LENGTH_SHORT).show();
                                finish();

                            } else {
                                Toast.makeText(CreateAccount.this, "Couldn't send Email", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }

    }

    private void showLoadingDialog(Activity activity, String message) {
        loadingDialog = new Dialog(activity,R.style.DialogTheme);
        loadingDialog.setContentView(R.layout.loading_screen);
        ProgressBar progressBar = loadingDialog.findViewById(R.id.progressBar_loading);
        TextView messageTitle = loadingDialog.findViewById(R.id.dialog_message);
        messageTitle.setText(message);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

    }
    private void removeLoadingDialog(){
        loadingDialog.dismiss();
    }




}
