package otk.test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by rj on 12/9/15.
 */
public class RecyclerForumAdapter extends RecyclerView.Adapter<RecyclerForumAdapter.ViewHolder> {
    Context context;
    int layoutResourceId;
    List<ForumPost> data=null;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView post_user,post_date, post_message;
        public ViewHolder(View v) {
            super(v);

            this.post_user = (TextView) v.findViewById(R.id.forum_post_user);
            this.post_date = (TextView) v.findViewById(R.id.forum_post_date);
            this.post_message = (TextView) v.findViewById(R.id.forum_post_message);
        }
    }

    public RecyclerForumAdapter(Context context, int layoutResourceId, List<ForumPost> data) {
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerForumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(this.layoutResourceId, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ForumPost posData = this.data.get(position);
        holder.post_user.setText(posData.getUser());
        holder.post_message.setText(posData.getMessage());
        holder.post_date.setText(DateFormat.getDateTimeInstance().format(posData.getTime().getTime()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
