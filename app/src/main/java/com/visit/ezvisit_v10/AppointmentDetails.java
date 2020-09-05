package com.visit.ezvisit_v10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.GetDeliverQuery;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.Nonnull;

public class AppointmentDetails extends AppCompatActivity {
    String identify, Doctor, description, time, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);
        Intent intent = getIntent();
        identify = intent.getStringExtra("id");
        query();
    }

    public void query() {
        ClientFactory.appSyncClient().query(GetDeliverQuery.builder().id(identify).build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(queryCallback);
    }

    private GraphQLCall.Callback<GetDeliverQuery.Data> queryCallback = new GraphQLCall.Callback<GetDeliverQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<GetDeliverQuery.Data> response) {
            final String id10 = identify.substring(24, Math.max(identify.length(), 12));
            Doctor = response.data().getDeliver().doctorName();
            description = response.data().getDeliver().appointmentInfo();
            date = (String) response.data().getDeliver().appointmentDate();
            time = response.data().getDeliver().appointmentTime();
            String parts[] = date.split("/");

            final int day = Integer.parseInt(parts[0]);
            final int month = Integer.parseInt(parts[1]);
            final int year = Integer.parseInt(parts[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month-1);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            final long milliTime = calendar.getTimeInMillis();

            Log.i("DATE", "day0"+milliTime);

            final CalendarView mCalendarView = findViewById(R.id.calendarView2);
            mCalendarView.setDate (milliTime, true, true);
//            mCalendarView.setMaxDate(milliTime);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);

//                    mCalendarView.setMaxDate(13);
                }
            });

            final TextView doctor_Name = findViewById(R.id.txtVisitName);
            final TextView doctor_info = findViewById(R.id.txtVisitPurpose);
            final TextView arrival_time = findViewById(R.id.txtArrivalTime);
            final TextView Appointment_date = findViewById(R.id.txtAppoinmentDate);


//            String dtStart = datetime;
//            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//            input.setTimeZone(TimeZone.getTimeZone("UTC"));
//            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");
//            SimpleDateFormat sdf1 = new SimpleDateFormat("h:mm a");
//            Date d = null;
//            Date t = null;
//            try {
//                d = input.parse(dtStart);
//                t = input.parse(dtStart);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            TimeZone tz = TimeZone.getTimeZone("Asia/Kuala_Lumpur");
//            output.setTimeZone(tz);
//            sdf1.setTimeZone(tz);
//            final String formattedDay = output.format(d);
//            final String formattedTime = sdf1.format(t);
//            Log.i("DATE", "day0" + formattedDay);
            runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  doctor_Name.setText(Doctor);
                                  doctor_info.setText(description);
                                  arrival_time.setText(time);
                                  Appointment_date.setText(date);
//                                  title_Agent_Size.setText(property_size + " sq");
//                                  title_Agent_description.setText(description);
//                                  title_Agent_bathroom.setText(bathroom + " bathroom");
//                                  title_Agent_room.setText(rooms + " rooms");
//                                  title_Agent_Furnishing.setText(Furnishing);
//                                  title_day.setText(formattedDay);
                              }
                          }
            );
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {

        }
    };
}