package com.rahul.newsdroid.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rahul.newsdroid.ActivityHomeCategory;
import com.rahul.newsdroid.R;
import java.util.ArrayList;

public class AdapterCategorySub extends RecyclerView.Adapter<AdapterCategorySub.MyViewHolder> {

    ArrayList<String> personNames;
    //ArrayList<Integer> personImages;
    Context context;

    public AdapterCategorySub(Context context, ArrayList<String> personNames/*, ArrayList<Integer> personImages*/) {
        this.context = context;
        this.personNames = personNames;
        //this.personImages = personImages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news_sub_cat, parent, false);
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.name.setText(personNames.get(position));
        //holder.image.setImageResource(personImages.get(position));
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String finalCatString = personNames.get(position);
                Intent passCatToHome = new Intent(context,ActivityHomeCategory.class);
                passCatToHome.putExtra("CategoryPassed",finalCatString);
                context.startActivity(passCatToHome);

            }
        });
    }

    @Override
    public int getItemCount() {
        return personNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView name;
        //ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            name = itemView.findViewById(R.id.news_sub_cat_text);
            //image = itemView.findViewById(R.id.image);

        }
    }
}
