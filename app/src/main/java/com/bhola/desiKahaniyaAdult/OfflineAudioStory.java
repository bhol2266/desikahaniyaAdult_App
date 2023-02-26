package com.bhola.desiKahaniyaAdult;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OfflineAudioStory extends AppCompatActivity {
    String TAG = "taga";
    List<AudioCategoryModel> collectionData;


    ImageView back, share_ap;
    TextView messageTextview;
    private AdView mAdView;


    com.facebook.ads.InterstitialAd facebook_IntertitialAds;
    com.facebook.ads.AdView facebook_adView;

    OfflineAudioAdapter adapter2;
    RecyclerView recyclerView;
    LinearLayout progressBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_detail);
//        StatusBarTransparent();

        if (SplashScreen.Ads_State.equals("active")) {
            showAds();
        }

        actionBar();

        progressBar2 = findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.GONE);

        collectionData = new ArrayList<AudioCategoryModel>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (checkPermissions()) {
            loadSavedAudioFiles();
        } else {
            requestPermissions();
        }


    }

    private void showAds() {


        if (SplashScreen.Ad_Network_Name.equals("admob")) {
//NO BANNER AD IN STORY LIST

            mAdView = findViewById(R.id.adView);
            ADS_ADMOB.BannerAd(this, mAdView);
        } else {

            LinearLayout facebook_bannerAd_layput;
            facebook_bannerAd_layput = findViewById(R.id.banner_container);
            ADS_FACEBOOK.bannerAds(this, facebook_adView, facebook_bannerAd_layput, getString(R.string.Facebbok_BannerAdUnit_1));

        }


    }


    private void loadSavedAudioFiles() {
        List<String> name_array = new ArrayList<String>();
        List<String> path_array = new ArrayList<String>();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("Download", Context.MODE_PRIVATE);
        File[] files = directory.listFiles();

        for (File file : files) {
            name_array.add(file.getName());
            path_array.add(file.getPath());
        }


        ArrayList<Object> collectionData = new ArrayList<Object>();
        for (int i = 0; i < name_array.size(); i++) {
            com.bhola.desiKahaniyaAdult.AudioOfflineModel model = new com.bhola.desiKahaniyaAdult.AudioOfflineModel(name_array.get(i), path_array.get(i));
            collectionData.add(model);
        }
        if (collectionData.size() == 0) {

            messageTextview = findViewById(R.id.message);
            messageTextview.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }
        recyclerView.setVisibility(View.VISIBLE);
        adapter2 = new OfflineAudioAdapter(collectionData, this);
        recyclerView.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();
    }


    private void actionBar() {
        TextView title = findViewById(R.id.title_collection);
        title.setText("Offline Audio Stories");
        title.setTextSize(24);

        back = findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    private void StatusBarTransparent() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    public void setWindowFlag(Context activity, final int bits, boolean on) {
        Window win = OfflineAudioStory.this.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void requestPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11(R) and above
            try {
                Log.d(TAG, "requestPermissions: try");
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Log.d(TAG, "requestPermissions: ", e);
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);

            }
        } else {
            //Android is below 11(R)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
//            if (ActivityCompat.shouldShowRequestPermissionRationale(AudioPlayer.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                Toast.makeText(AudioPlayer.this, "Please Give Permission to download file", Toast.LENGTH_SHORT).show();
//            } else {
//                ActivityCompat.requestPermissions(AudioPlayer.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//            }
        }
    }

    private ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            //here we will handle result of intent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //Android is 11(R) and above
                if (Environment.isExternalStorageManager()) {
                    //Manage External storage permission is granted
                    Log.d(TAG, "onActivityResult: " + "permission is granted");
                    Toast.makeText(OfflineAudioStory.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                } else {
                    //Manage External storage permission is denied
                    Log.d(TAG, "onActivityResult: " + "permission is denied");
                    Toast.makeText(OfflineAudioStory.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            } else {
                //Android is below 11(R)

            }
        }
    });

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11(R) and above
            return Environment.isExternalStorageManager();
        } else {
            //Android is below 11(R)
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0) {
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (write && read) {
                    Log.d(TAG, "onActivityResult: " + "permission is granted");
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onActivityResult: " + "permission is Denied");

                }
            }
        }

    }


}


class OfflineAudioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    RewardedInterstitialAd mRewardedVideoAd;
    com.facebook.ads.InterstitialAd facebook_IntertitialAds;
    ArrayList<Object> collectionData = new ArrayList<Object>();


    public OfflineAudioAdapter(ArrayList<Object> data, FragmentActivity activity) {
        this.context = activity;
        this.collectionData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View Story_ROW_viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_layout, parent, false);
        return new Story_ROW_viewHolder(Story_ROW_viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        com.bhola.desiKahaniyaAdult.AudioOfflineModel model = (com.bhola.desiKahaniyaAdult.AudioOfflineModel) collectionData.get(holder.getAdapterPosition());
        Log.d("onBindViewHolder", "onBindViewHolder: "+model);
        ((Story_ROW_viewHolder) holder).title.setText(model.getName().replaceAll("_", " ").replace(".mp3", ""));
        ((Story_ROW_viewHolder) holder).title.setTextSize(18);
        ((Story_ROW_viewHolder) holder).delete.setVisibility(View.VISIBLE);

        ((Story_ROW_viewHolder) holder).delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextWrapper cw = new ContextWrapper(v.getContext());
                File directory = cw.getDir("Download", Context.MODE_PRIVATE);
                File file = new File(directory, model.getName());
                if (file.exists()) {
                    file.delete();
                    collectionData.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    Toast.makeText(cw, "Story Deleted", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ((Story_ROW_viewHolder) holder).imageview.setImageResource(R.drawable.folder);
        ((Story_ROW_viewHolder) holder).imageview.setPadding(0,5,0,5);
        ((Story_ROW_viewHolder) holder).date.setText("Downloaded");

        ((Story_ROW_viewHolder) holder).recyclerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AudioPlayerOffline.class);
                intent.putExtra("storyURL", model.getPath());
                intent.putExtra("storyName", model.getName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return collectionData.size();
    }


    public class Story_ROW_viewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;

        ImageView imageview, delete;
        LinearLayout recyclerview;


        public Story_ROW_viewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerview = itemView.findViewById(R.id.recyclerviewLayout);
            imageview = itemView.findViewById(R.id.imageview);
            delete = itemView.findViewById(R.id.delete);
            title = itemView.findViewById(R.id.titlee);
            date = itemView.findViewById(R.id.date_recyclerview);

        }
    }
}




