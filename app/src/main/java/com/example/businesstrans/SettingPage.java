package com.example.businesstrans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Set;

public class SettingPage extends AppCompatActivity {

    private static final String TAG = "SettingPage";
    private EditText mEmailEditText;
    private EditText mPassWordEditText;
    private TextView mSendPasswordLink;
    private Button mSaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);
        mSendPasswordLink = findViewById(R.id.account_setting_resetpasswordlink);
        mEmailEditText = findViewById(R.id.account_setting_email);
        mEmailEditText.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        mEmailEditText.requestFocus();
        mPassWordEditText = findViewById(R.id.account_setting_password);
        mSaveBtn = findViewById(R.id.account_details_save);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginPage.isEmpty(mEmailEditText.getText().toString()) || LoginPage.isEmpty(mPassWordEditText.getText().toString())){
                    Snackbar.make(view,"You must submit data into all fields",Snackbar.LENGTH_LONG).show();

                }else{
                    ControllerActivity.showLoadingDialog(SettingPage.this,"Updating Email");
                    updateEmail();
                }
            }
        });
        mSendPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ControllerActivity.showLoadingDialog(SettingPage.this,"Sending reset link");
                sendResetPasswordLink();
            }
        });

    }

    private void updateEmail() {
        AuthCredential credential = EmailAuthProvider
                .getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(),mPassWordEditText.getText().toString());
        FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updateEmail(mEmailEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                sendVerificationEmail();
                                ControllerActivity.removeLoadingDialog();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(SettingPage.this, LoginPage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                ControllerActivity.removeLoadingDialog();
                                Toast.makeText(SettingPage.this,"Couldn't update email",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SettingPage.this,"Couldn't update email",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    ControllerActivity.removeLoadingDialog();
                    Toast.makeText(SettingPage.this,"Couldn't update email",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingPage.this,"Couldn't update email",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendResetPasswordLink() {
        FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: Password reset email sent.");
                    ControllerActivity.removeLoadingDialog();
                    Toast.makeText(SettingPage.this,"Password reset email sent",Toast.LENGTH_SHORT).show();
                }else{
                    ControllerActivity.removeLoadingDialog();
                    Log.d(TAG, "onComplete: No user associated with that email.");
                    Toast.makeText(SettingPage.this,"No user associated with that email.",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    private void checkAuthenticationState(){
        Log.d(TAG, "checkAuthenticationState: checking authentication state");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Log.d(TAG, "checkAuthenticationState: user is null , navigation back to login screen.");
            Intent intent = new Intent(SettingPage.this,LoginPage.class);
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
    private void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SettingPage.this,"Sent Verification Email",Toast.LENGTH_SHORT).show();
                                finish();

                            }else {
                                Toast.makeText(SettingPage.this,"Couldn't send Email",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }
    }
}
