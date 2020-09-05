package com.visit.ezvisit_v10.ui.profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.visit.ezvisit_v10.AppHelper;
import com.visit.ezvisit_v10.MainActivity;
import com.visit.ezvisit_v10.R;

import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private CognitoUser cognitoUser;
    private final String TAG = ProfileFragment.class.getSimpleName();
    String username,names,numbers;
    CognitoUser user;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

//        final String username = AppHelper.getCurrUser();
//        Log.i(TAG, "Retrieved user itemsrrrrrrrrr"+ username);
        final TextView email = root.findViewById(R.id.txtEmail);
        final TextView name = root.findViewById(R.id.txtName);
        final TextView number = root.findViewById(R.id.txtPhone);
        // Get the user name
        username = AppHelper.getCurrUser();
        names = AppHelper.getCurrName();
        numbers= AppHelper.getCurrNumber();

        getDetails();
        user = AppHelper.getPool().getUser(username);
        Log.i(TAG, " user itemsrrrrrrrrr "+ username);
        Log.i(TAG, " user itemsrrrrrrrrr "+ user);
        email.setText(username);
        name.setText(names);
        number.setText(numbers);

        return root;
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
//                if (key.contains("given_name")){
//                    TextView name = requireView().findViewById(R.id.txtName);
//                    name.setText(value);
//                } else if (key.contains("phone_number")){
//                    TextView number = requireView().findViewById(R.id.txtPhone);
//                    number.setText(value);
//                }
            }

//            showAttributes();

        }

        @Override
        public void onFailure(Exception exception) {
            closeWaitDialog();
            showDialogMessage("Could not fetch user details!", AppHelper.formatException(exception), true);
        }
    };

    private void getDetails() {
         AppHelper.getPool().getUser(username).getDetailsInBackground(detailsHandler);
    }
    private void exit () {
        Intent intent = new Intent();
        if(username == null)
            username = "";
        intent.putExtra("name",username);

    }

    private void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if(exit) {
                        exit();
                    }
                } catch (Exception e) {
                    // Log failure
                    Log.e(TAG," -- Dialog dismiss failed");
                    if(exit) {
                        exit();
                    }
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }
}