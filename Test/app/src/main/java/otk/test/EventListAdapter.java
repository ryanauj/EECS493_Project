package otk.test;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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

    public EventListAdapter(Context context, int layoutResourceId, List<EventData> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

//    @Override
//    public EventData getItem(int position) {
//        return data.get(position);
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        eventHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }

        EventData nextEvent = data.get(position);

        if(nextEvent!=null) {
            TextView tt1 = (TextView) row.findViewById(R.id.eventCreator);
            TextView tt2 = (TextView) row.findViewById(R.id.eventTitle);

            if(tt1 != null)
                tt1.setText(nextEvent.getCreator());
            else
                Log.e("Adapter", "Text Field Returned null");
            if(tt2 != null)
                tt2.setText(nextEvent.getTitle());
        }
        return row;
    }

    static class eventHolder
    {
        TextView txtCreator,txtDescription;
    }
}
