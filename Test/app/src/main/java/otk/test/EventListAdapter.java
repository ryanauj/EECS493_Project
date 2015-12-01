package otk.test;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tim on 11/25/2015.
 */
public class EventListAdapter extends ArrayAdapter<EventData> {
    Context context;
    int layoutResourceId;
    List<EventData> data=null;
    public static final String EVENT_DATA_STRING = "EVENT_DATA_STRING";

    public EventListAdapter(Context context, int layoutResourceId, List<EventData> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public EventData getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        eventHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new eventHolder();
            holder.txtCreator = (TextView)row.findViewById(R.id.eventCreator);
            holder.txtDescription = (TextView)row.findViewById(R.id.eventTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (eventHolder)row.getTag();
        }

        EventData nextEvent = data.get(position);
        holder.txtCreator.setText(nextEvent.getCreator());
        holder.txtDescription.setText(nextEvent.getDescription());
        return row;
    }

    static class eventHolder
    {
        TextView txtCreator,txtDescription;
    }
}
