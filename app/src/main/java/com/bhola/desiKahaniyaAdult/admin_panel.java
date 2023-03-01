package com.bhola.desiKahaniyaAdult;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class admin_panel extends AppCompatActivity {
    public static int counter = 0;

    DatabaseReference mref, notificationMref;
    EditText title_story, pragraphofstory, date, image_url;
    Button selectStory, insertBTN, Refer_App_url_BTN, STory_Switch_Active_BTN;
    Switch switch_Exit_Nav, switch_Activate_Ads, switch_Sex_Story;
    Button Ad_Network;
    static String uncensored_title = "";
    private String TAG="TAGA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminpanel);


        initViews();
        appControl();
        deleteNotification_Stories();
        Ad_Network_Selection();
        Add_Stories_to_Notification_Buttons();


    }


    private void initViews() {

        mref = FirebaseDatabase.getInstance().getReference().child("shareapp_url");
        notificationMref = FirebaseDatabase.getInstance().getReference();
        Ad_Network = findViewById(R.id.Ad_Network);
        selectStory = findViewById(R.id.selectStory);
        insertBTN = findViewById(R.id.insert);
        STory_Switch_Active_BTN = findViewById(R.id.XXXSTory_Switch_Active);
        switch_Activate_Ads = findViewById(R.id.Activate_Ads);
        switch_Sex_Story = findViewById(R.id.Sex_Story);
        switch_Exit_Nav = findViewById(R.id.switch_Exit_Nav);
        Refer_App_url_BTN = findViewById(R.id.Refer_App_url_BTN);
        title_story = findViewById(R.id.title_story);
        pragraphofstory = findViewById(R.id.pragraphofstory);
        date = findViewById(R.id.dateofstory);
        image_url = findViewById(R.id.image_url);


    }

    private void Add_Stories_to_Notification_Buttons() {


        selectStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<StoryItemModel> collectonData = new ArrayList<StoryItemModel>();

                Cursor cursor = new DatabaseHelper(admin_panel.this, SplashScreen.DB_NAME, SplashScreen.DB_VERSION, "StoryItems").readalldata();
                try {
                    while (cursor.moveToNext()) {
                        StoryItemModel storyItemModel = new StoryItemModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9), cursor.getString(10), cursor.getInt(11), cursor.getInt(12), cursor.getString(13), cursor.getInt(14));
                        collectonData.add(storyItemModel);
                    }

                } finally {
                    cursor.close();
                }

                int randomNum = (int) (Math.random() * (collectonData.size() - 1 - 0 + 1) + 0);
                StoryItemModel storyItemModel=collectonData.get(randomNum);
                title_story.setText(decryption(storyItemModel.getTitle()));
                date.setText(storyItemModel.getDate());
                if (storyItemModel.getStory().trim().length() == 0) {
                    fetchStoryAPI(storyItemModel.getTitle(),decryption(storyItemModel.getHref()));
                }

                uncensored_title =decryption(storyItemModel.getTitle());
            }
        });


        insertBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!image_url.getText().toString().isEmpty() && !pragraphofstory.getText().toString().isEmpty() && !title_story.getText().toString().isEmpty() && !date.getText().toString().isEmpty()) {
                    pasteAndRuncode();
                    FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender("/topics/all", uncensored_title, "पूरी कहानी पढ़ें", image_url.getText().toString(), "Notification_Story", admin_panel.this);
                    fcmNotificationsSender.SendNotifications();
                } else {
                    Toast.makeText(admin_panel.this, "Enter data", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    private void Ad_Network_Selection() {


        Ad_Network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Ad_Network.getText().toString().equals("admob")) {
                    mref.child("Ad_Network").setValue("facebook");
                    Ad_Network.setBackgroundColor(Color.parseColor("#D11A1A"));

                } else {
                    mref.child("Ad_Network").setValue("admob");
                    Ad_Network.setBackgroundColor(Color.parseColor("#4267B2"));
                }


            }
        });


    }

    private void deleteNotification_Stories() {

        Button deleteNotificationStories;
        deleteNotificationStories = findViewById(R.id.deleteNotificationStories);
        deleteNotificationStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notificationMref.child("Notification").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String keys = ds.getKey();
                            notificationMref.child("Notification").child(keys).removeValue();
                            Toast.makeText(admin_panel.this, "Deleted all Stories", Toast.LENGTH_SHORT).show();

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }


                });

            }
        });


    }

    void pasteAndRuncode() {

        String titlee = title_story.getText().toString();
        String paragrapg = pragraphofstory.getText().toString();
        String datee = date.getText().toString();

        String Push_ID = notificationMref.push().getKey();
        notificationMref.child("Notification").child(Push_ID).child("Title").setValue(titlee);
        notificationMref.child("Notification").child(Push_ID).child("Heading").setValue(paragrapg);
        notificationMref.child("Notification").child(Push_ID).child("Date").setValue(datee);
        mref.child("Notification_ImageURL").setValue(image_url.getText().toString());
        Toast.makeText(getApplicationContext(), "Data is Successfully Added", Toast.LENGTH_SHORT).show();

        title_story.getText().clear();
        date.getText().clear();
        pragraphofstory.getText().clear();

    }


    private void appControl() {
        checkButtonState();
        EditText Refer_App_url2;

        Refer_App_url2 = findViewById(R.id.Refer_App_url2);

        Refer_App_url_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Refer_App_url2.length() > 2) {
                    mref.child("Refer_App_url2").setValue(Refer_App_url2.getText().toString());
                    Toast.makeText(admin_panel.this, "Refer_App_url2 ADDED", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(admin_panel.this, "Field is Empty", Toast.LENGTH_SHORT).show();
            }

        });

        switch_Exit_Nav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    mref.child("switch_Exit_Nav").setValue("active");
                } else {
                    mref.child("switch_Exit_Nav").setValue("inactive");
                }

            }
        });

        switch_Activate_Ads.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    mref.child("Ads").setValue("active");
                } else {
                    mref.child("Ads").setValue("inactive");
                }

            }
        });
        switch_Sex_Story.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (SplashScreen.Sex_Story_Switch_Open.equals("active")) {
                        mref.child("Sex_Story").setValue("active");
                    }
                } else {
                    mref.child("Sex_Story").setValue("inactive");
                }
            }
        });


        STory_Switch_Active_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (STory_Switch_Active_BTN.getText().toString().trim().equals("Disabled")) {
                    mref.child("Sex_Story_Switch_Open").setValue("active");
                } else
                    mref.child("Sex_Story_Switch_Open").setValue("inactive");
            }
        });


    }

    private void checkButtonState() {

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                image_url.setText((String) snapshot.child("Notification_ImageURL").getValue().toString().trim());
                String match = (String) snapshot.child("switch_Exit_Nav").getValue().toString().trim();

                if (match.equals("active")) {
                    switch_Exit_Nav.setChecked(true);

                } else {

                    switch_Exit_Nav.setChecked(false);
                }

                String Ads = (String) snapshot.child("Ads").getValue().toString().trim();

                if (Ads.equals("active")) {
                    switch_Activate_Ads.setChecked(true);

                } else {
                    switch_Activate_Ads.setChecked(false);
                }
                String FakeStoryStatus = (String) snapshot.child("Sex_Story").getValue().toString().trim();
                if (FakeStoryStatus.equals("active")) {
                    switch_Sex_Story.setChecked(true);

                } else {
                    switch_Sex_Story.setChecked(false);
                }

                if (snapshot.child("Sex_Story_Switch_Open").getValue().toString().trim().equals("active")) {
                    STory_Switch_Active_BTN.setText("Enabled");
                    STory_Switch_Active_BTN.setBackgroundColor(Color.parseColor("#10FF00"));
                } else {
                    STory_Switch_Active_BTN.setText("Disabled");
                    switch_Sex_Story.setChecked(false);
                    STory_Switch_Active_BTN.setBackgroundColor(Color.parseColor("#FF0000"));
                }

                String Ad_Network_name = (String) snapshot.child("Ad_Network").getValue().toString().trim();

                Ad_Network.setText(Ad_Network_name);
                if (snapshot.child("Ad_Network").getValue().toString().trim().equals("admob")) {
                    Ad_Network.setBackgroundColor(Color.parseColor("#D11A1A"));
                } else {
                    Ad_Network.setBackgroundColor(Color.parseColor("#4267B2"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String decryption(String encryptedText) {

        int key = 5;
        String decryptedText = "";

        //Decryption
        char[] chars2 = encryptedText.toCharArray();
        for (char c : chars2) {
            c -= key;
            decryptedText = decryptedText + c;
        }
        return decryptedText;
    }

    private void fetchStoryAPI(String title,String href) {
        String API_URL = "https://clownfish-app-jn7w9.ondigitalocean.app/storiesDetails";
        RequestQueue requestQueue = Volley.newRequestQueue(admin_panel.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject json_obj = jsonObject.getJSONObject("data");

                    JSONArray storyArray = json_obj.getJSONArray("description");
                    ArrayList<String> storyArrayList = new ArrayList();
                    for (int j = 0; j < storyArray.length(); j++) {
                        storyArrayList.add(storyArray.getString(j));
                    }
                    String description = String.join("\n\n", storyArrayList);
                    pragraphofstory.setText(description.toString().trim().replaceAll("\\/", ""));

                    new DatabaseHelper(admin_panel.this, SplashScreen.DB_NAME, SplashScreen.DB_VERSION, "StoryItems").updateStoryParagraph(title, description);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAGA", "onErrorResponse: " + error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("href", href);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


}