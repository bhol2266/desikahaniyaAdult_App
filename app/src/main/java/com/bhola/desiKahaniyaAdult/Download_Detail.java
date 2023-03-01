package com.bhola.desiKahaniyaAdult;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class Download_Detail extends AppCompatActivity {


    String Ads_State;
    List<StoryItemModel> collectonData;
    public static Download_story_ADAPTER adapter2;
    String message;
    ImageView back;
    private AdView mAdView;
    RecyclerView recyclerView;
    TextView emptyView;

    com.facebook.ads.InterstitialAd facebook_IntertitialAds;
    com.facebook.ads.AdView facebook_adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download);

        try {
            if (SplashScreen.Ads_State.equals("active")) {
                showAds(SplashScreen.Ad_Network_Name, this);
            }
        } catch (Exception e) {

        }

        actionBar();
        initViews();


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        collectonData = new ArrayList<StoryItemModel>();

        getDataFromDatabase();


        checkCollectionDataEmpty();


        adapter2 = new Download_story_ADAPTER(collectonData, message, Ads_State);
        recyclerView.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();


    }

    private void showAds(String Ad_Network_Name, Context mContext) {


        if (Ad_Network_Name.equals("admob")) {
            mAdView = findViewById(R.id.adView);
            ADS_ADMOB.BannerAd(this, mAdView);
        } else {

            LinearLayout facebook_bannerAd_layput, facebook_bannerAd_layput_2;
            facebook_bannerAd_layput = findViewById(R.id.banner_container);


//
            ADS_FACEBOOK.bannerAds(mContext, facebook_adView, facebook_bannerAd_layput, getString(R.string.Facebbok_BannerAdUnit_1));

        }


    }


    private void getDataFromDatabase() {


        Cursor cursor = new DatabaseHelper(Download_Detail.this, SplashScreen.DB_NAME, SplashScreen.DB_VERSION, "StoryItems").readLikedStories();
        try {
            while (cursor.moveToNext()) {
                StoryItemModel storyItemModel = new StoryItemModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9), cursor.getString(10), cursor.getInt(11), cursor.getInt(12), cursor.getString(13), cursor.getInt(14));
                collectonData.add(storyItemModel);
            }

        } finally {
            cursor.close();
        }
    }


    private void checkCollectionDataEmpty() {
        if (collectonData.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

        }
    }


    private void actionBar() {
        back = findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Collection_GridView.class);
                intent.putExtra("Ads_Status", Ads_State);
                startActivity(intent);
                finish();
            }
        });

    }


    private void initViews() {
        emptyView = findViewById(R.id.emptyView);
        recyclerView = findViewById(R.id.recyclerView_Downloads);
    }

}


class Download_story_ADAPTER extends RecyclerView.Adapter<Download_story_ADAPTER.viewholder> {
    List<StoryItemModel> collectonData;
    DatabaseReference mref, mref_download;

    String message;
    AlertDialog dialog;
    String Ads_State;

    public Download_story_ADAPTER(List<StoryItemModel> collectonData, String message, String ads_State) {
        this.collectonData = collectonData;
        this.message = message;
        this.Ads_State = ads_State;
    }


    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.grid_layout, parent, false);
        return new Download_story_ADAPTER.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, @SuppressLint("RecyclerView") int position) {

        StoryItemModel storyItemModel = (StoryItemModel) collectonData.get(position);
        holder.title.setText(SplashScreen.decryption(storyItemModel.getTitle()));
        holder.date.setText(storyItemModel.getDate());


        holder.recyclerview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final Vibrator vibe = (Vibrator) v.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(80);//80 represents the milliseconds (the duration of the vibration)


                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                View promptView = inflater.inflate(R.layout.delete, null);
                builder.setView(promptView);
                builder.setCancelable(false);
                Button delete = promptView.findViewById(R.id.DELETE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final MediaPlayer mp = MediaPlayer.create(v.getContext(), R.raw.sound);
                        mp.start();

                        String res = new DatabaseHelper(v.getContext(), SplashScreen.DB_NAME, SplashScreen.DB_VERSION, "StoryItems").updaterecord(SplashScreen.decryption(storyItemModel.getTitle()), 0);
                        Toast.makeText(v.getContext(), "Removed from Offline Stories"+res, Toast.LENGTH_SHORT).show();

                        collectonData.remove(position);
                        Download_Detail.adapter2.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                Button cancel = promptView.findViewById(R.id.CANCEL);


                dialog = builder.create();
                dialog.show();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });


                return false;
            }
        });



        holder.recyclerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StoryPage.class);
                intent.putExtra("category",  storyItemModel.getCategory());
                intent.putExtra("title", SplashScreen.decryption(storyItemModel.getTitle()));
                intent.putExtra("date", storyItemModel.getDate());
                intent.putExtra("href", SplashScreen.decryption(storyItemModel.getHref()));
                intent.putExtra("relatedStories", storyItemModel.getRelatedStories());
                intent.putExtra("storiesInsideParagraph", storyItemModel.getStoriesInsideParagraph());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });

    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView title;
        TextView index, heading, date;
        LinearLayout recyclerview;


        public viewholder(@NonNull View itemView) {
            super(itemView);
            recyclerview = itemView.findViewById(R.id.recyclerviewLayout);
            title = itemView.findViewById(R.id.titlee);
            date = itemView.findViewById(R.id.date_recyclerview);

        }
    }

    @Override
    public int getItemCount() {
        return collectonData.size();
    }
}
