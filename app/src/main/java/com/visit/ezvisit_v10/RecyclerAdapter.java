package com.visit.ezvisit_v10;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.amplify.generated.graphql.ListDeliversQuery;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import static android.content.ContentValues.TAG;
import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private List<ListDeliversQuery.Item> mData = new ArrayList<>();
    private List<ListDeliversQuery.Item> copy = new ArrayList<>();
    private LayoutInflater mInflater;
    private RecyclerItemClickListener.OnItemClickListener listener;

    public RecyclerAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            holder.bindData(mData.get(position));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemChanged(mData.size());
    }


    public void setItems(List<ListDeliversQuery.Item> items) {
        mData = items;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text3, text4, text5, text6, text7, text8, text9, text10, text11, text12, text13;
        ImageView img1;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
//            img1 = itemView.findViewById(R.id.imageView2);
//            text3 = itemView.findViewById(R.id.Price_Prop);
//            text4 = itemView.findViewById(R.id.text_location2);
            text5 = itemView.findViewById(R.id.appointment_info);
            text6 = itemView.findViewById(R.id.doctor_name);
//            text8 = itemView.findViewById(R.id.text_time_row);
            text9 = itemView.findViewById(R.id.text_day_row);
        }

        public void bindData(ListDeliversQuery.Item item) throws ParseException {

//            Picasso.get().load(item.image()).into(img1);
//            text3.setText("RM " + item.price());
            text9.setText(item.appointmentDate());
            text5.setText(item.appointmentInfo());
//            String stringdouble = (item.Size() + " sqt");
            text6.setText(item.doctorName());
//            String dtStart = (String) item.createdAt();
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            input.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("h:mm a");
            Date d = null;
            Date t = null;
            TimeZone tz = TimeZone.getTimeZone("Asia/Kuala_Lumpur");
            output.setTimeZone(tz);
            sdf1.setTimeZone(tz);

        }
    }
}

