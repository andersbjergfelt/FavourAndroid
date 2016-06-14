package com.bjergfelt.himev5.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bjergfelt.himev5.Model.Job;
import com.bjergfelt.himev5.R;

import java.util.List;

/**
 * Created by ABjergfelt on 26-05-2016.
 */
public class OwnJobsAdapter extends RecyclerView.Adapter<OwnJobsAdapter.MyViewHolder> {

private Context mContext;
private List<Job> ownJobList;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView title, count;
    public ImageView thumbnail, overflow;

    public MyViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        count = (TextView) view.findViewById(R.id.count);
        thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        overflow = (ImageView) view.findViewById(R.id.overflow);
    }
}


    public OwnJobsAdapter(Context mContext, List<Job> ownJobList) {
        this.mContext = mContext;
        this.ownJobList = ownJobList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.own_jobs_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Job job = ownJobList.get(position);
        holder.title.setText(job.getName());
        holder.count.setText(job.getDescription());

        // loading album cover using Glide library
        //Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);
        byte[] imageBytes = Base64.decode(job.getPhoto(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.thumbnail.setImageBitmap(bitmap);
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_own_jobs, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

/**
 * Click listener for popup menu items
 */
class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

    public MyMenuItemClickListener() {
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            /*case R.id.action_delete_job:
                Toast.makeText(mContext, "Delete", Toast.LENGTH_SHORT).show();
                return true;*/
            default:
        }
        return false;
    }

}

    @Override
    public int getItemCount() {
        return ownJobList.size();
    }


}
