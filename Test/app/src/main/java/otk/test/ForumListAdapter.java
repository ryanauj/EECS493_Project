package otk.test;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by rj on 12/9/15.
 */
public class ForumListAdapter extends ArrayAdapter<ForumPost> {
    Context context;
    int layoutResourceId;
    List<ForumPost> data=null;

    public ForumListAdapter(Context context, int layoutResourceId, List<ForumPost> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }

        ForumPost forumPost = data.get(position);

        if (forumPost != null) {
            TextView post_user = (TextView) row.findViewById(R.id.forum_post_user);
            TextView post_message = (TextView) row.findViewById(R.id.forum_post_message);
            TextView post_date = (TextView) row.findViewById(R.id.forum_post_date);

            if (post_user != null) { post_user.setText(forumPost.getUser()); }
            if (post_message != null) { post_message.setText(forumPost.getMessage()); }
            if (post_date != null) {
                post_date.setText(DateFormat.getDateTimeInstance().format(forumPost.getDate()));
            }
        }
        return row;
    }
}
