package com.visit.ezvisit_v10.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.amplify.generated.graphql.CreateDeliverMutation;
import com.amazonaws.amplify.generated.graphql.ListDeliversQuery;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.visit.ezvisit_v10.AppointmentDetails;
import com.visit.ezvisit_v10.BookAppointment;
import com.visit.ezvisit_v10.ClientFactory;
import com.visit.ezvisit_v10.MainActivity;
import com.visit.ezvisit_v10.R;
import com.visit.ezvisit_v10.RecyclerAdapter;
import com.visit.ezvisit_v10.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Objects;

import javax.annotation.Nonnull;

import type.CreateDeliverInput;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    RecyclerView recyclerView;
    RecyclerAdapter myAdapter;
    private ArrayList<ListDeliversQuery.Item> mitems;
    private final String TAG = MainActivity.class.getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recyclerView = root.findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myAdapter = new RecyclerAdapter(getContext());
        Button button =root.findViewById(R.id.btnAppointment);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BookAppointment.class);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(myAdapter);
        ClientFactory.init(getContext());
        setHasOptionsMenu(true);


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String id = mitems.get(position).id();
//                        List<ListDeliversQuery.Item> x = myAdapter.filters_search("bung",mitems);
//                        String id =x.get(position).id();
                        Toast.makeText(getContext(), "u have clicked..." + id, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), AppointmentDetails.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Toast.makeText(getContext(), "long pressed", Toast.LENGTH_SHORT).show();
                        myAdapter.removeItem(position);
                    }
                })
        );

        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        query();
    }

    public void query() {


        ClientFactory.appSyncClient().query(ListDeliversQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(queryCallback);
        Log.i(TAG, "Retrieved list itemsrrrrrrrrrrrrrr: 123");

    }


    public void createquery() {

        AWSAppSyncClient client = ClientFactory.appSyncClient();
        CreateDeliverMutation.Data expected = new CreateDeliverMutation.Data(null);

        CreateDeliverInput deliver = CreateDeliverInput.builder()
                .doctorName("DR richard")
                .visitPurpose("Dialysis")
                .appointmentDate("12/04/2021")
                .appointmentInfo("Dialysis up")
                .temp("36")
                .arrivalDatetime("12")
                .checkInDatetime("10")
                .appointmentTime("10AM")
                .userName("Nity")
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
                                Toast.makeText(getContext(), "Added!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Failed to add!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d(TAG, "add failed with [" + e.getLocalizedMessage() + "]");
                        e.printStackTrace();
                    }
                });
    }

    private GraphQLCall.Callback<ListDeliversQuery.Data> queryCallback = new GraphQLCall.Callback<ListDeliversQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListDeliversQuery.Data> response) {
            assert response.data() != null;

            mitems = new ArrayList<>(Objects.requireNonNull(Objects.requireNonNull(response.data().listDelivers()).items()));
            Log.i(TAG, "Retrieved list itemsrrrrrrrrrrrrrr: repeattt");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myAdapter.setItems(mitems);
                    Log.i(TAG, "rrrr" + mitems.toString());

                    myAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, e.toString());
        }
    };

}