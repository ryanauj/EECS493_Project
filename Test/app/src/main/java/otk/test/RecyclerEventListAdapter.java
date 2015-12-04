package otk.test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rj on 12/3/15.
 */
public class RecyclerEventListAdapter extends RecyclerView.Adapter<RecyclerEventListAdapter.ViewHolder> {
    Context context;
    int layoutResourceId;
    List<EventData> data=null;

// Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtCreator,txtDescription;
        public ViewHolder(View v) {
            super(v);
            this.txtCreator = (TextView) v.findViewById(R.id.eventCreator);
            this.txtDescription = (TextView) v.findViewById(R.id.eventTitle);
        }
    }

    public RecyclerEventListAdapter(Context context, int layoutResourceId, List<EventData> data) {
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerEventListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(this.layoutResourceId, parent, false);
        // set the view's size, margins, paddings and layout parameters
        // ...

        ViewHolder vh = new ViewHolder(v);
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
