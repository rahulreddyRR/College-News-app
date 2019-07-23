package com.rahul.newsdroid.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;
import com.rahul.newsdroid.ActivityHomeCategory;
import com.rahul.newsdroid.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterCategory extends ExpandableRecyclerAdapter<AdapterCategory.PeopleListItem> {
    public static final int TYPE_PERSON = 1001;


    public AdapterCategory(Context context) {
        super(context);

        setItems(getSampleItems());
    }

    public static class PeopleListItem extends ExpandableRecyclerAdapter.ListItem {
        public String Text;

        public PeopleListItem(String group) {
            super(TYPE_HEADER);

            Text = group;
        }

        public PeopleListItem(String first, String last) {
            super(TYPE_PERSON);

            //Text = first + " " + last;
            Text = first;

        }
    }

    public class HeaderViewHolder extends ExpandableRecyclerAdapter.HeaderViewHolder {
        TextView name;

        public HeaderViewHolder(View view) {
            super(view, (ImageView) view.findViewById(R.id.header_image));
            name =  view.findViewById(R.id.header_name);
        }

        public void bind(final int position) {
            super.bind(position);

            name.setText(visibleItems.get(position).Text);
        }
    }

    public class PersonViewHolder extends ExpandableRecyclerAdapter.ViewHolder {
        TextView name;
        LinearLayout selectedLnr;

        public PersonViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.header_sub_name);
            selectedLnr = view.findViewById(R.id.selectedItem);
        }

        public void bind(final int position) {
            name.setText(visibleItems.get(position).Text);
            selectedLnr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String finalCatString = visibleItems.get(position).Text;
                    Intent passCatToHome = new Intent(mContext,ActivityHomeCategory.class);
                    passCatToHome.putExtra("CategoryPassed",finalCatString);
                    mContext.startActivity(passCatToHome);
                }
            });
        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflate(R.layout.list_item_categories, parent));
            case TYPE_PERSON:
            default:
                return new PersonViewHolder(inflate(R.layout.list_item_categories_sub, parent));
        }
    }

    @Override
    public void onBindViewHolder(ExpandableRecyclerAdapter.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                ((HeaderViewHolder) holder).bind(position);
                break;
            case TYPE_PERSON:
            default:
                ((PersonViewHolder) holder).bind(position);
                break;
        }
    }

    private List<PeopleListItem> getSampleItems() {
        List<PeopleListItem> items = new ArrayList<>();

        items.add(new PeopleListItem("BCA"));
        items.add(new PeopleListItem("BCA Staff Admin", ""));
        items.add(new PeopleListItem("BCA Student", ""));

        items.add(new PeopleListItem("MCA"));
        items.add(new PeopleListItem("MCA Staff Admin", ""));
        items.add(new PeopleListItem("MCA Student", ""));


        items.add(new PeopleListItem("B.Sc"));
        items.add(new PeopleListItem("B.Sc Staff Admin", ""));
        items.add(new PeopleListItem("B.Sc Student", ""));

        items.add(new PeopleListItem("M.Sc"));
        items.add(new PeopleListItem("M.Sc Staff Admin", ""));
        items.add(new PeopleListItem("M.Sc Student", ""));


        //items.add(new PeopleListItem("Even More"));
       //items.add(new PeopleListItem("Badminton", ""));
        //items.add(new PeopleListItem("Chess", ""));
       // items.add(new PeopleListItem("Fanzone", ""));
        //items.add(new PeopleListItem("Kabaddi", ""));
        //items.add(new PeopleListItem("Rugby", ""));
        //items.add(new PeopleListItem("Volleyball", ""));
       // items.add(new PeopleListItem("Weightlifting", ""));
       // items.add(new PeopleListItem("Wrestling", ""));
       // items.add(new PeopleListItem("Others", ""));

        return items;
    }
}