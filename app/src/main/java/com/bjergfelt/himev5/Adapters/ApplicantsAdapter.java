package com.bjergfelt.himev5.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bjergfelt.himev5.Model.Applicant;
import com.bjergfelt.himev5.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by andersbjergfelt on 06/05/2016.
 */
public class ApplicantsAdapter extends RecyclerView.Adapter<ApplicantsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Applicant> listOfApplicantArrayList = new ArrayList<>();
    private static String today;


    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, template, timestamp;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            template = (TextView) itemView.findViewById(R.id.extraText_applicants);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);


        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Create an instance of SimpleDateFormat used for formatting
        // the string representation of date (month/day/year)
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.GERMANY);

        // Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();
        // Using DateFormat format method we can create a string
        // representation of a date with the defined format.
        String reportDate = df.format(today);
        Applicant applicant = listOfApplicantArrayList.get(position);
        holder.name.setText(applicant.getName());
        holder.template.setText(applicant.getTemplate());

        holder.timestamp.setText(reportDate);


    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);

    }


    @Override
    public int getItemCount() {
        return listOfApplicantArrayList.size();
    }


    public ApplicantsAdapter(Context context, ArrayList<Applicant> listOfApplicantArrayList){
        this.context = context;
        this.listOfApplicantArrayList = listOfApplicantArrayList;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.applicants_list_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;

    }

    public static class RecyclerTouchListener implements  RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }


        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;

        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }





}
