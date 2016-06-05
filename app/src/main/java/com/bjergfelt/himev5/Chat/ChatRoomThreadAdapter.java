package com.bjergfelt.himev5.Chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bjergfelt.himev5.R;
import com.bjergfelt.himev5.jobData.DataProvider;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by andersbjergfelt on 07/05/2016.
 */
public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = ChatRoomThreadAdapter.class.getSimpleName();

    private String userId;
    private int SELF = 100;
    private static String today;

    private Context context;
    private ArrayList<Message> messageArrayList;


    public ChatRoomThreadAdapter(Context context, ArrayList<Message> messageArrayList, String userId){
        this.context = context;
        this.messageArrayList = messageArrayList;
        this.userId = userId;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get((Calendar.DAY_OF_MONTH)));


    }

    public ChatRoomThreadAdapter(Context context, ArrayList<Message> messageArrayList) {
        this.context = context;
        this.messageArrayList = messageArrayList;


        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get((Calendar.DAY_OF_MONTH)));

    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (message.getUser().getId().equals(userId)) {
            return SELF;
        }

        return position;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        ((ViewHolder)holder).message.setText(message.getCreatedAt());

        String timestamp = getTimeStamp(message.getCreatedAt());

        if (message.getUser().getName() != null)
            timestamp = message.getUser().getName() + ", " + timestamp;

        ((ViewHolder) holder).timestamp.setText(timestamp);

    }

    private String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try{
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView message, timestamp;
        public ViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        }
    }

}

