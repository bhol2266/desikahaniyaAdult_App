package com.bhola.desiKahaniyaAdult;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.InterstitialAd;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Collection_detail extends AppCompatActivity {


    private List<NativeAd> mNativeAds = new ArrayList<NativeAd>();
    private AdLoader adLoader;
    LinearLayout progressBar2;
    TextView check_Internet_Connection;
    Button retryBtn;
    String TAG = "taga";
    List<Object> collectonData;
    Collection_Details_ADAPTER adapter2;
    DatabaseReference mref2;
    String Ads_State, title_category, href;
    Context context;
    ImageView back, share_ap;
    private AdView mAdView;


    InterstitialAd facebook_IntertitialAds;
    com.facebook.ads.AdView facebook_adView;

    RecyclerView recyclerView;
    LinearLayoutManager manager;
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    String token = "";
    int page = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_detail);

        try {
            if (SplashScreen.Ads_State.equals("active")) {
                showAds();
            }
        } catch (Exception e) {

        }

        initviews_Check_Internet_Connectivity_Actionbar();


    }

    private void showAds() {


        if (SplashScreen.Ad_Network_Name.equals("admob")) {
            mAdView = findViewById(R.id.adView);
            ADS_ADMOB.BannerAd(this, mAdView);
        } else {

            LinearLayout facebook_bannerAd_layput;
            facebook_bannerAd_layput = findViewById(R.id.banner_container);


            ADS_FACEBOOK.bannerAds(this, facebook_adView, facebook_bannerAd_layput, getString(R.string.Facebbok_BannerAdUnit_1));

        }


    }


    private void initviews_Check_Internet_Connectivity_Actionbar() {

        actionBar();

        recyclerView = findViewById(R.id.recyclerView);
        collectonData = new ArrayList<Object>();
        progressBar2 = findViewById(R.id.progressBar2);
        check_Internet_Connection = findViewById(R.id.check_Internet_Connection);
        retryBtn = findViewById(R.id.retryBtn);


        if (isInternetAvailable(Collection_detail.this)) {
//
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(new Runnable() {
                @Override
                public void run() {
                    getMoreDateFromDatabase();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Send_ALL_DATA_TO_RECYCLERVIEW();
                        }
                    });
                }
            });


        } else {
            check_Internet_Connection.setVisibility(View.VISIBLE);
            check_Internet_Connection.setText("No Internet Connection");
            retryBtn.setVisibility(View.VISIBLE);
        }

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }

        });


    }


    private void Send_ALL_DATA_TO_RECYCLERVIEW() {


        recyclerView.setVisibility(View.VISIBLE);
        progressBar2.setVisibility(View.GONE);

        manager = new LinearLayoutManager(this);

        adapter2 = new Collection_Details_ADAPTER(collectonData, context, "StoryItems", Ads_State, title_category);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    getMoreDate();
                }
            }
        });

    }

    private void getMoreDate() {
        page = page + 1;
        Log.d(TAG, "getMoreDate: " + page);
        progressBar2.setVisibility(View.VISIBLE);


        Cursor cursor = new DatabaseHelper(Collection_detail.this, SplashScreen.DB_NAME, SplashScreen.DB_VERSION, "StoryItems").readaDataByCategory(href, page);
        try {
            while (cursor.moveToNext()) {
                StoryItemModel storyItemModel = new StoryItemModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9), cursor.getString(10), cursor.getInt(11), cursor.getInt(12), cursor.getString(13));
                collectonData.add(storyItemModel);
            }

        } finally {
            cursor.close();
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter2.notifyDataSetChanged();
                progressBar2.setVisibility(View.GONE);
            }
        }, 800);


    }


    private void getMoreDateFromDatabase() {

        Cursor cursor = new DatabaseHelper(this, SplashScreen.DB_NAME, SplashScreen.DB_VERSION, "StoryItems").readaDataByCategory(href, 1);
        try {
            try {
                while (cursor.moveToNext()) {
                    StoryItemModel storyItemModel = new StoryItemModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9), cursor.getString(10), cursor.getInt(11), cursor.getInt(12), cursor.getString(13));
                    collectonData.add(storyItemModel);
                }

            } finally {
                cursor.close();
            }

        } catch (Exception ignored) {

        }


    }


    boolean isInternetAvailable(Context context) {
        if (context == null) return false;


        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_statut", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_statut", "" + e.getMessage());
                }
            }
        }
        Log.i("update_statut", "Network is available : FALSE ");
        return false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Collection_GridView.class);
        intent.putExtra("Ads_Status", Ads_State);
        startActivity(intent);
    }

    private void actionBar() {
        mref2 = FirebaseDatabase.getInstance().getReference();

        TextView title;
        title_category = getIntent().getStringExtra("category");
        href = getIntent().getStringExtra("href");
        title = findViewById(R.id.title_collection);
        title.setText(title_category);
        title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EditText passwordEdittext;
                Button passwordLoginBtn;


                AlertDialog dialog;

                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View promptView = inflater.inflate(R.layout.admin_panel_entry, null);
                builder.setView(promptView);
                builder.setCancelable(true);


                passwordEdittext = promptView.findViewById(R.id.passwordEdittext);
                passwordLoginBtn = promptView.findViewById(R.id.passwordLoginBtn);

                passwordLoginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (passwordEdittext.getText().toString().equals("5555")) {
                            startActivity(new Intent(getApplicationContext(), admin_panel.class));

                        } else {
                            Toast.makeText(v.getContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                dialog = builder.create();
                dialog.show();
                return false;
            }
        });
        back = findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Collection_GridView.class);
                intent.putExtra("Ads_Status", Ads_State);
                startActivity(intent);
            }
        });
        share_ap = findViewById(R.id.share_app);
        share_ap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_ap.setImageResource(R.drawable.favourite_active);
                Intent intent = new Intent(getApplicationContext(), Download_Detail.class);
                intent.putExtra("Ads_Status", Ads_State);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (facebook_adView != null) {
            facebook_adView.destroy();
        }

        if (facebook_IntertitialAds != null) {
            facebook_IntertitialAds.destroy();
        }
    }
}


class Collection_Details_ADAPTER extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
        StoryItemModel storyItemModel = (StoryItemModel) collectonData.get(position);

        storyRowViewHolder.title.setText(SplashScreen.decryption(storyItemModel.getTitle()));
        storyRowViewHolder.date.setText(storyItemModel.getDate());
        storyRowViewHolder.views.setText(storyItemModel.getViews());


        storyRowViewHolder.recyclerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StoryPage.class);
                intent.putExtra("category", title_category);
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


    @Override
    public int getItemCount() {
        return collectonData.size();
    }


    public class Story_ROW_viewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView index, heading, date, views;

        LinearLayout recyclerview;


        public Story_ROW_viewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerview = itemView.findViewById(R.id.recyclerviewLayout);
            title = itemView.findViewById(R.id.titlee);
            date = itemView.findViewById(R.id.date_recyclerview);
            views = itemView.findViewById(R.id.views);

        }
    }


}
