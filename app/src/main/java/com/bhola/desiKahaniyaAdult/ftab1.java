package com.bhola.desiKahaniyaAdult;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphTextView;

public class ftab1 extends Fragment {
    Context context = getActivity();
    soup.neumorphism.NeumorphCardView collection1, collection2, collection3, collection4, collection5, collection6;
    private String TAG = "TAGA";
    View view;

    public ftab1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_ftab1, container, false);

        if (SplashScreen.Login_Times > 3 && SplashScreen.Sex_Story.equals("active")) {
//            changeTitle_Textview(view);
        }

        gridItems();
        return view;
    }


    private void gridItems( ) {
        ArrayList<HashMap<String, String>> Category_List = new ArrayList<HashMap<String, String>>();

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                try (Cursor cursor = new DatabaseHelper(getActivity(), SplashScreen.DB_NAME, SplashScreen.DB_VERSION, "GridItems").readGridItems()) {
                    while (cursor.moveToNext()) {
                        HashMap<String, String> m_li = new HashMap<String, String>();
                        m_li.put("category_title", cursor.getString(0));
                        m_li.put("href", cursor.getString(1));
                        Category_List.add(m_li);
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createGridItems(Category_List);
                    }
                });
            }
        });


    }

    private void createGridItems(ArrayList<HashMap<String, String>> Category_List) {


        GridLayout gridLayout = view.findViewById(R.id.gridlayout);
        for (int i = 0; i < Category_List.size(); i++) {


            String category = Category_List.get(i).get("category_title").toString();
            String href = Category_List.get(i).get("href").toString();

            View vieww = getLayoutInflater().inflate(R.layout.homepage_griditem, null);
            TextView categoryTextView = vieww.findViewById(R.id.Textview1);
            NeumorphCardView cardView = vieww.findViewById(R.id.cardview);

            categoryTextView.setText(category);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            float requiredWidth = (float) (displayMetrics.widthPixels / 2.2);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) requiredWidth, 250);
            cardView.setLayoutParams(params);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), Collection_detail.class);
                    intent.putExtra("category", category);
                    intent.putExtra("href", href);
                    startActivity(intent);
                }
            });

            gridLayout.addView(vieww);
        }
    }


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = requireActivity().getAssets().open("GridItems.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
