package com.rahul.newsdroid.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rahul.newsdroid.Data.DataNewsComments;
import com.rahul.newsdroid.R;

import java.util.List;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.ViewHolder> {

    public List<DataNewsComments> commentsList;
    public Context context;

    public AdapterComments(List<DataNewsComments> commentsList){

        this.commentsList = commentsList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        String commentMessage = commentsList.get(position).getMessage();
        String comment_username = commentsList.get(position).getUser_id();

        holder.setComment_message(commentMessage,"Sports Lover");

    }


    @Override
    public int getItemCount() {
        if(commentsList != null) {
            return commentsList.size();
        } else {
            return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView comment_message, comment_username;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setComment_message(String message,String user_name){

            comment_username = mView.findViewById(R.id.comment_username);
            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);
            comment_username.setText(user_name);

        }
    }
}
