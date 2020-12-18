package com.example.businesstrans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {
    private Button createAccountButton, loginButton;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "LoginPage";
    private TextView mResendEmail;
    private EditText mPassword;
    private EditText mEmail;
    private AlertDialog mAlertDialog;
    private Context mContext;
    private TextView mForgotPassword;
    private String userEmail;
    private AlertDialog mForgotDialog;
    private Dialog loadingDialog;
    private Activity mActivity;
    private boolean mDialogStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        setUpfirebaseAuth();
        mContext = this;
        mActivity = this;
        createAccountButton = findViewById(R.id.create_account);
        loginButton = findViewById(R.id.sign_in);
        mEmail = findViewById(R.id.loginemail);
        mPassword = findViewById(R.id.passwordlogin);
        mForgotPassword = findViewById(R.id.forgot_password);
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                View view1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.forgot_password, null);
                builder.setView(view1);
                final EditText forgotEmail = view1.findViewById(R.id.forgot_password_email_edittext);
                final Button resetButton = view1.findViewById(R.id.forgot_password_button);
                resetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userEmail = forgotEmail.getText().toString();
                        sendResetPasswordLink();
                        mForgotDialog.dismiss();
                    }
                });
                mForgotDialog = builder.create();
                mForgotDialog.show();
            }
        });

        mResendEmail = findViewById(R.id.resend_trigger);
        mResendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                View view1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.resend_email, null);
                builder.setView(view1);
                final EditText resendEmail = view1.findViewById(R.id.resend_email_edittext);
                final EditText resendPassword = view1.findViewById(R.id.resend_passsword_edittext);
                Button resendButton = view1.findViewById(R.id.resend_button);
                mAlertDialog = builder.create();
                mAlertDialog.show();
                resendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = resendEmail.getText().toString();
                        String userPassword = resendPassword.getText().toString();
                        resendEmail(userEmail, userPassword);
                    }
                });
            }
        });
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, CreateAccount.class);
                startActivity(intent);

            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmpty(mEmail.getText().toString()) || isEmpty(mPassword.getText().toString())){
                    Snackbar.make(view,"You must submit data into all fields",Snackbar.LENGTH_LONG).show();
                }else{
                    mDialogStatus = true;
                    showLoadingDialog(mActivity,"Logging in");
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginPage.this, "Couldn't login.", Toast.LENGTH_SHORT).show();
                                removeLoadingDialog();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            removeLoadingDialog();
                            Toast.makeText(LoginPage.this, "Couldn't login.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });

    }

    private void setUpfirebaseAuth() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {
                        Toast.makeText(LoginPage.this, "Authenticated with " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onAuthStateChanged: signed_in" + user.getUid());
                        if(mDialogStatus){
                            removeLoadingDialog();
                        }
                        Intent intent = new Intent(LoginPage.this, ControllerActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginPage.this, "Check Your Email Inbox for a verification Link", Toast.LENGTH_SHORT).show();
                        removeLoadingDialog();
                        FirebaseAuth.getInstance().signOut();
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    private void resendEmail(String email, String password) {
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: reauthenticate success.");
                    sendVerificationEmail();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginPage.this, "Invalid Credentials \n Reset your password and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(LoginPage.this, "Sent Verification Email", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
                                FirebaseAuth.getInstance().signOut();


                            } else {
                                Toast.makeText(LoginPage.this, "Couldn't send Email", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginPage.this, "Couldn't send Email Verification Link", Toast.LENGTH_SHORT).show();

                }
            });

        }
    }

    private void sendResetPasswordLink() {
        FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Password reset email sent.");
                    Toast.makeText(LoginPage.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onComplete: No user associated with that email.");

                    Toast.makeText(LoginPage.this, "No user associated with that email.", Toast.LENGTH_SHORT).show();

                }
            }
        });
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

    public static boolean isEmpty(String string){

        return string.equals("");

    }

}

