package com.example.logintestfb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private String phoneVerificationId;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

            verificationCallbacks;

    private PhoneAuthProvider.ForceResendingToken resendToken;

    TextView tv;
    EditText etPhone, etCode;
    private FirebaseAuth fbAuth;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phone_login);

        tv = (TextView) findViewById(R.id.tv);

        etPhone = (EditText) findViewById(R.id.etPhone);

        etCode = (EditText) findViewById(R.id.etCode);

    }

    public void sendCode(View view) {

        try{
            FirebaseApp.initializeApp(this);
            fbAuth = FirebaseAuth.getInstance();

            String phoneNumber = etPhone.getText().toString();

            setUpVerificatonCallbacks();

            PhoneAuthProvider.getInstance().verifyPhoneNumber(

                    phoneNumber, // Phone number to verify

                    60, // Timeout duration

                    TimeUnit.SECONDS, // Unit of timeout

                    this, // Activity (for callback binding)

                    verificationCallbacks);
        }catch(Exception e){
            tv.setText(e.toString());
        }
    }

    private void setUpVerificatonCallbacks() {

        verificationCallbacks =

                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override

                    public void onVerificationCompleted(PhoneAuthCredential credential) {

                        etCode.setText("");

                        signInWithPhoneAuthCredential(credential);

                    }

                    @Override

                    public void onVerificationFailed(FirebaseException e) {
                        tv.setText(e.toString());
                    }

                    @Override

                    public void onCodeSent(String verificationId,

                                           PhoneAuthProvider.ForceResendingToken token) {

                        phoneVerificationId = verificationId;

                        resendToken = token;
                        tv.setText("Code sent. Check SMS.");

                    }

                };

    }

    public void verifyCode(View view) {

        String code = etCode.getText().toString();

        PhoneAuthCredential credential =

                PhoneAuthProvider.getCredential(phoneVerificationId, code);

        // For Singin Buttton
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        fbAuth.signInWithCredential(credential)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {

                            etCode.setText("");

                            tv.setText("Signed In");

                            FirebaseUser user = task.getResult().getUser();
//ဒီမွာ startActivity() ေရးပါ


                        } else {


                        }

                    }
                });

    }

    public void resendCode(View view) {

        String phoneNumber = etPhone.getText().toString();

        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(

                phoneNumber,

                60,

                TimeUnit.SECONDS,

                this,

                verificationCallbacks,

                resendToken);

    }

    public void signOut(View view) {

        fbAuth.signOut();

        tv.setText("Signed Out");
    }

}