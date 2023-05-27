package com.frontendsourcecode.expenser.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.frontendsourcecode.expenser.R;
import com.frontendsourcecode.expenser.helper.PinEntryEditText;
import com.frontendsourcecode.expenser.model.User;
import com.frontendsourcecode.expenser.util.localstorage.LocalStorage;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private static final int CREDENTIAL_PICKER_REQUEST = 1;
    private static final String TAG = "LoginActivity====>";
    EditText phone;
    Button sendOTP;
    Button verifyOTP;
    String phoneNumber;
    String otp;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
   View loading;
    private String mVerificationId;
    LinearLayout otp_layout;
    PinEntryEditText pinEntry;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    Gson gson = new Gson();
    PhoneAuthCredential credential;
    LocalStorage localStorage;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        localStorage = new LocalStorage(getApplicationContext());
        phone = findViewById(R.id.login_phone);
        sendOTP = findViewById(R.id.send_otp);
        verifyOTP = findViewById(R.id.verify_otp);
        loading = findViewById(R.id.loading);
        otp_layout = findViewById(R.id.otp_layout);
        pinEntry = findViewById(R.id.txt_pin_entry);

        if (pinEntry != null) {
            pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str) {
                   otp = str.toString();
                }
            });
        }

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otp.length()<6){
                    Toast.makeText(LoginActivity.this, "Please enter 6 digit OTP.", Toast.LENGTH_SHORT).show();
                }else{
                    verifyPhoneNumberWithCode(mVerificationId,otp);
                }

            }
        });

       sendOTP.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               phoneNumber = phone.getText().toString();
               if(phoneNumber.length()<10){
                   Toast.makeText(LoginActivity.this, "Enter valid Phone number.", Toast.LENGTH_SHORT).show();
               }else{
                   loading.setVisibility(View.VISIBLE);
                   startPhoneNumberVerification("+91 "+phoneNumber);
               }

           }
       });



        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                   credential = credential;
                    signInWithPhoneAuthCredential(credential);
            }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                loading.setVisibility(View.GONE);
                sendOTP.setVisibility(View.GONE);
                otp_layout.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, "OTP send to your phone number.", Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
        // [END verify_with_code]
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser currentUser = task.getResult().getUser();
                            String userKey = currentUser.getUid();
                            String phoneNumber = currentUser.getPhoneNumber();
                            user = new User(userKey,phoneNumber);
                            String userString = gson.toJson(user);

                            localStorage.createUserLoginSession(userString);
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null){
            String userKey = currentUser.getUid();
            String phoneNumber = currentUser.getPhoneNumber();
            user = new User(userKey,phoneNumber);
            String userString = gson.toJson(user);
            localStorage.createUserLoginSession(userString);
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }


    }

}