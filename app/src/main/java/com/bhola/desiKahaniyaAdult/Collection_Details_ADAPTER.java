package com.bhola.desiKahaniyaAdult;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class Collection_Details_ADAPTER extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MENU_ITEM_VIEW_TYPE = 0;

    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;
    List<Object> collectonData;

    Context context;
    String Collection_DB_Table_Name;
    String Ads_State;
    String title_category;

    public Collection_Details_ADAPTER(List<Object> collectonData, Context context, String message, String ads_State, String title_category) {
        this.collectonData = collectonData;
        this.context = context;
        this.Collection_DB_Table_Name = message;
        this.Ads_State = ads_State;
        this.title_category = title_category;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View Story_ROW_viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_layout, parent, false);
        return new Collection_Details_ADAPTER.Story_ROW_viewHolder(Story_ROW_viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Collection_Details_ADAPTER.Story_ROW_viewHolder storyRowViewHolder = (Story_ROW_viewHolder) holder;
        FirebaseData firebaseData = (FirebaseData) collectonData.get(position);

        storyRowViewHolder.title.setText(firebaseData.getTitle());
        storyRowViewHolder.date.setText(firebaseData.getDate());


        storyRowViewHolder.recyclerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StoryPage.class);
                intent.putExtra("Collection", Collection_DB_Table_Name);
                intent.putExtra("bhola2", title_category);
                intent.putExtra("_id", firebaseData.getId());
                intent.putExtra("DB_TABLENUMBER", Collection_DB_Table_Name);
                intent.putExtra("title", firebaseData.getTitle());
                intent.putExtra("Story", firebaseData.getHeading());
                intent.putExtra("date", firebaseData.getDate());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return collectonData.size();
    }


    public class Story_ROW_viewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView index, heading, date;

        LinearLayout recyclerview;


        public Story_ROW_viewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerview = itemView.findViewById(R.id.recyclerviewLayout);
            title = itemView.findViewById(R.id.titlee);
            date = itemView.findViewById(R.id.date_recyclerview);

        }
    }
}
