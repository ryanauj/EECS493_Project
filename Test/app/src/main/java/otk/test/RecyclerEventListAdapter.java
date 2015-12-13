package otk.test;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by rj on 12/3/15.
 */
public class RecyclerEventListAdapter extends RecyclerView.Adapter<RecyclerEventListAdapter.ViewHolder> {
    Context context;
    int layoutResourceId;
    List<EventData> data=null;
    int userColor;

    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }



// Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        // each data item is just a string in this case
        public TextView txtCreator, txtDescription, txtDate, txtAttendance;
        public OnItemClickListener clickListener;
        public OnItemLongClickListener longClickListener;
        public LinearLayout borderColor;

        public ViewHolder(View v, OnItemClickListener itemClickListener, OnItemLongClickListener itemLongClickListener) {
            super(v);
            this.clickListener = itemClickListener;
            this.longClickListener = itemLongClickListener;
            this.txtCreator = (TextView) v.findViewById(R.id.eventCreator);
            this.txtDescription = (TextView) v.findViewById(R.id.eventTitle);
            this.txtDate = (TextView) v.findViewById(R.id.Time_slot);
            this.txtAttendance = (TextView) v.findViewById(R.id.Attendance);
            this.borderColor = (LinearLayout) v.findViewById(R.id.bordercolor);
            //this.rsvpCount = (TextView) v.findViewById(R.id.Attendance);
            //this.txtTime = (TextView) v.findViewById(R.id.Time_slot);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, this.getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            this.longClickListener.onItemLongClick(v, this.getAdapterPosition());
            this.borderColor =  (LinearLayout) v.findViewById(R.id.bordercolor);
            return true;
        }


    }

    public RecyclerEventListAdapter(Context context, int layoutResourceId, List<EventData> data, int color,
                                    OnItemClickListener itemClickListener, OnItemLongClickListener itemLongClickListener) {
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.userColor = color;
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
    }

    public void setList(List<EventData> data)
    {
        this.data=data;
        return;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerEventListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(this.layoutResourceId, parent, false);
        // set the view's size, margins, paddings and layout parameters
        // ...

        ViewHolder vh = new ViewHolder(v, itemClickListener, itemLongClickListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        EventData posData = this.data.get(position);
        holder.txtCreator.setText(posData.getCreator());
        holder.txtDescription.setText(posData.getTitle());

        Calendar time = posData.getTime();
        holder.txtDate.setText(getDateString(time)+" "+getTimeString(time));

        String maxAttendees = context.getResources().getString(R.string.infinity);
        if (posData.getMaxAttendees() != 0) {
            maxAttendees = String.valueOf(posData.getMaxAttendees());
        }
        holder.txtAttendance.setText(String.valueOf(posData.getAttendees().size()) +
                                     " / " + posData.getMaxAttendees());

        if(holder.borderColor!=null)
            holder.borderColor.setBackgroundColor(ContextCompat.getColor(context,posData.getColor()));
    }

    public String getTimeString(Calendar cal) {
        Date date = cal.getTime();
        SimpleDateFormat sdf;
        if (cal.get(Calendar.HOUR) > 9) {
            sdf = new SimpleDateFormat("hh:mm aa");
        }
        else {
            sdf = new SimpleDateFormat("h:mm aa");
        }
        return sdf.format(date);
    }

    public String getDateString(Calendar cal) {
        Date date = cal.getTime();
        SimpleDateFormat sdf;
        if (cal.get(Calendar.DAY_OF_MONTH) > 9) {
            sdf = new SimpleDateFormat("EEEE, MMM dd");
        }
        else {
            sdf = new SimpleDateFormat("EEEE, MMM d");
        }
        return sdf.format(date);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public EventData getItemAtPosition(int position) {
        return this.data.get(position);
    }
}
