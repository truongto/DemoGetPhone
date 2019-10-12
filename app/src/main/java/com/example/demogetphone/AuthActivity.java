package com.example.demogetphone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {
    private EditText mphone, mcode;
    private ProgressBar progressBar, progressBar2;
    private Button button;
    FirebaseAuth mAuth;
    private TextView textView;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks;
    String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private int Type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mAuth = FirebaseAuth.getInstance();
        mphone = findViewById(R.id.nhapsdt);
        mcode = findViewById(R.id.nhapma);
        button = findViewById(R.id.dangnhap);
        textView = findViewById(R.id.err);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Type == 0) {
                    mphone.setEnabled(false);
                    button.setEnabled(false);

                    String phoneNumber = mphone.getText().toString();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+84" +
                                    phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            AuthActivity.this,
                            mcallbacks
                    );
                } else {
                    button.setEnabled(false);

                    String VerificationCode=mcode.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, VerificationCode);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });
        mcallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                textView.setText("there was error in Verification");
                textView.setEnabled(false);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                mVerificationId = verificationId;
                mResendToken = token;

                Type = 1;


                mcode.setVisibility(View.VISIBLE);

                button.setText("Verify code");
                button.setEnabled(true);
            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(AuthActivity.this, "there was error in login", Toast.LENGTH_SHORT).show();
                            textView.setText("there was error in login");
                            textView.setEnabled(false);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


}
