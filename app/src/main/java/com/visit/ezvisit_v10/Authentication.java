package com.visit.ezvisit_v10;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;

import java.util.Locale;
import java.util.Map;

public class Authentication extends AppCompatActivity {
    private final String TAG = Authentication.class.getSimpleName();
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        AppHelper.init(getApplicationContext());
        findCurrent();

        AWSMobileClient.getInstance().initialize(this, new Callback<UserStateDetails>() {

            @Override
            public void onResult(UserStateDetails result) {
                Log.i(TAG, result.getUserState().toString());
                switch (result.getUserState()) {
                    case SIGNED_IN:

                        Intent i = new Intent(Authentication.this, MainActivity.class);
                        startActivity(i);
                        break;
                    case SIGNED_OUT:
                        showSignIn();
                        break;
                    default:
                        AWSMobileClient.getInstance().signOut();
                        showSignIn();
                        break;
                }
            }

            @Override
            public void onError(Exception e) {
                Log.i(TAG, "Error");

            }

            private void showSignIn() {
                try {
                    AWSMobileClient.getInstance().showSignIn(Authentication.this,
                            SignInUIOptions.builder().nextActivity(MainActivity.class).build());
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }


        });
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.d(TAG, " -- Auth Success");
            AppHelper.setCurrSession(cognitoUserSession);
            AppHelper.newDevice(device);

        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {

            Locale.setDefault(Locale.US);
            getUserAuthentication(authenticationContinuation, username);
        }


        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation continuation) {

        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {

        }

        @Override
        public void onFailure(Exception exception) {

        }
    };

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        if (username != null) {
            this.username = username;
            AppHelper.setUser(username);
        }
        if (this.password == null) {
            if (password == null) {
                return;
            }
            if (password.length() < 1) {
                return;
            }
        }
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(this.username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    private void findCurrent() {
        CognitoUser user = AppHelper.getPool().getCurrentUser();
        username = user.getUserId();
        if (username != null) {
            AppHelper.setUser(username);
            user.getSessionInBackground(authenticationHandler);
        }
        AppHelper.getPool().getUser(username).getDetailsInBackground(detailsHandler);
    }

    GetDetailsHandler detailsHandler = new GetDetailsHandler() {
        @Override
        public void onSuccess(CognitoUserDetails cognitoUserDetails) {
//            closeWaitDialog();
            // Store details in the AppHandler
            AppHelper.setUserDetails(cognitoUserDetails);
//            Log.i(TAG, " user itemsrrrrrrrrr "+ cognitoUserDetails);
            String aemail = AppHelper.getUserDetails().toString();
//            Log.i(TAG, " user itemsrrrrrrrrr "+ aemail);
            Map<String, String> user = cognitoUserDetails.getAttributes().getAttributes();
            for (Map.Entry<String, String> entry : user.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.d("rrrrkey", "key: " + key);
                Log.d("rrrrkey", "value: " + value);
                if (key.contains("given_name")) {
                    AppHelper.setUsername(value);
                } else if (key.contains("phone_number")) {
                    AppHelper.setNumber(value);
                }
            }
//            showAttributes();
        }

        @Override
        public void onFailure(Exception exception) {
        }


    };
}