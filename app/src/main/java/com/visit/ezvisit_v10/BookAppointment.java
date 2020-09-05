package com.visit.ezvisit_v10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateDeliverMutation;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

import type.CreateDeliverInput;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class BookAppointment extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    String curDate,curMonth,curYear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        final CalendarView mCalendarView = findViewById(R.id.calendarView2);
        final Button button = findViewById(R.id.btnAppointment);



        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                 curDate = String.valueOf(dayOfMonth);
                curMonth = String.valueOf(month);
                curYear = String.valueOf(year);

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createquery();
                finish();
            }
        });
    }



    public void createquery() {

        String txtPurpose = ((EditText) findViewById(R.id.txtPurpose)).getText().toString();
        String txtVisitName = ((EditText) findViewById(R.id.txtVisitName)).getText().toString();
        String txtArrivalTime = ((EditText) findViewById(R.id.txtArrivalTime)).getText().toString();

        AWSAppSyncClient client = ClientFactory.appSyncClient();
        CreateDeliverMutation.Data expected = new CreateDeliverMutation.Data(null);

        CreateDeliverInput deliver = CreateDeliverInput.builder()
                .doctorName(txtVisitName)
                .visitPurpose(txtPurpose)
                .appointmentDate(curDate+"/"+curMonth+"/"+curYear)
                .appointmentInfo(txtPurpose)
                .temp("36")
//                .arrivalDatetime("12")
//                .checkInDatetime("10")
                .appointmentTime(txtArrivalTime)
                .userName(AppHelper.getCurrName())
                .faceId("ID")
                .build();


        CreateDeliverMutation addDeliverMutation = CreateDeliverMutation.builder().input(deliver).build();
        client.mutate(addDeliverMutation, expected)
                .enqueue(new GraphQLCall.Callback<CreateDeliverMutation.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<CreateDeliverMutation.Data> response) {
                        Log.d(TAG, "successfully added");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Added!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Failed to add!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d(TAG, "add failed with [" + e.getLocalizedMessage() + "]");
                        e.printStackTrace();
                    }
                });
    }
}