package com.bhola.desiKahaniya;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class admin_panel extends AppCompatActivity {
    public static int counter = 0;

    DatabaseReference mref;
    TextView Users_Counters;
    EditText title, heading, date;
    Button insertBTN, BTN1, BTN2, BTN3, Refer_App_url_BTN, STory_Switch_Active_BTN;
    Switch switch_Exit_Nav, switch_Activate_Ads, switch_Sex_Story;
    Button Ad_Network;

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

        mref = FirebaseDatabase.getInstance().getReference();
        Ad_Network = findViewById(R.id.Ad_Network);
        BTN1 = findViewById(R.id.DATE);
        BTN2 = findViewById(R.id.TITLE);
        BTN3 = findViewById(R.id.HEADING);
        insertBTN = findViewById(R.id.insert);
        Users_Counters = findViewById(R.id.Users_Counters);
        STory_Switch_Active_BTN = findViewById(R.id.XXXSTory_Switch_Active);
        switch_Activate_Ads = findViewById(R.id.Activate_Ads);
        switch_Sex_Story = findViewById(R.id.Sex_Story);
        switch_Exit_Nav = findViewById(R.id.switch_Exit_Nav);
        Refer_App_url_BTN = findViewById(R.id.Refer_App_url_BTN);
        title = findViewById(R.id.title_story);
        heading = findViewById(R.id.headingofstory);
        date = findViewById(R.id.dateofstory);


    }

    private void Add_Stories_to_Notification_Buttons() {
        BTN1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData abc = myClipboard.getPrimaryClip();
                ClipData.Item item = abc.getItemAt(0);
                String text = item.getText().toString();
                date.setText(text);

            }
        });


        BTN2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData abc = myClipboard.getPrimaryClip();
                ClipData.Item item = abc.getItemAt(0);
                String text = item.getText().toString();
                title.setText(text);

            }
        });


        BTN3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData abc = myClipboard.getPrimaryClip();
                ClipData.Item item = abc.getItemAt(0);
                String text = item.getText().toString();
                heading.setText(text);
            }
        });


        insertBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasteAndRuncode();
            }
        });

    }


    private void Ad_Network_Selection() {


        Ad_Network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Ad_Network.getText().toString().equals("admob")) {
                    mref.child("shareapp_url").child("Ad_Network").setValue("facebook");
                    Ad_Network.setBackgroundColor(Color.parseColor("#D11A1A"));

                } else {
                    mref.child("shareapp_url").child("Ad_Network").setValue("admob");
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

                mref.child("Notification").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String keys = ds.getKey();
                            mref.child("Notification").child(keys).removeValue();
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

        String titlee = title.getText().toString();
        String headingg = heading.getText().toString();
        String datee = date.getText().toString();


        if (!title.getText().toString().isEmpty() && !heading.getText().toString().isEmpty() && !date.getText().toString().isEmpty()) {

            String Push_ID = mref.push().getKey();
            mref.child("Notification").child(Push_ID).child("Title").setValue(titlee);
            mref.child("Notification").child(Push_ID).child("Heading").setValue(headingg);
            mref.child("Notification").child(Push_ID).child("Date").setValue(datee);
            Toast.makeText(getApplicationContext(), "Data is Successfully Added", Toast.LENGTH_SHORT).show();

            title.getText().clear();
            date.getText().clear();
            heading.getText().clear();

        } else {
            Toast.makeText(getApplicationContext(), "Add Key", Toast.LENGTH_SHORT).show();

        }

    }


    private void appControl() {
        checkButtonState();
        EditText Refer_App_url2;

        Refer_App_url2 = findViewById(R.id.Refer_App_url2);

        Refer_App_url_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Refer_App_url2.length() >2) {
                    mref.child("shareapp_url").child("Refer_App_url2").setValue(Refer_App_url2.getText().toString());
                    Toast.makeText(admin_panel.this, "Refer_App_url2 ADDED", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(admin_panel.this, "Field is Empty", Toast.LENGTH_SHORT).show();
            }

        });

        switch_Exit_Nav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    mref.child("shareapp_url").child("switch_Exit_Nav").setValue("active");
                } else {
                    mref.child("shareapp_url").child("switch_Exit_Nav").setValue("inactive");
                }

            }
        });

        switch_Activate_Ads.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    mref.child("shareapp_url").child("Ads").setValue("active");
                } else {
                    mref.child("shareapp_url").child("Ads").setValue("inactive");
                }

            }
        });
        switch_Sex_Story.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (SplashScreen.Sex_Story_Switch_Open.equals("active")) {
                        mref.child("shareapp_url").child("Sex_Story").setValue("active");
                    }
                } else {
                    mref.child("shareapp_url").child("Sex_Story").setValue("inactive");
                }
            }
        });


        STory_Switch_Active_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (STory_Switch_Active_BTN.getText().toString().trim().equals("Disabled")) {
                    mref.child("shareapp_url").child("Sex_Story_Switch_Open").setValue("active");
                } else
                    mref.child("shareapp_url").child("Sex_Story_Switch_Open").setValue("inactive");
            }
        });


    }

    private void checkButtonState() {

        mref.child("shareapp_url").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                Ad_Network.setText(SplashScreen.Ad_Network_Name);
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


}