package code.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.tabs.TabLayout;
import com.kmcapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarException;

import javax.security.auth.callback.Callback;

import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableCounsellingPosters;
import code.infantsFragment.Item;
import code.infantsFragment.ZoomImageAdapter;
import code.view.BaseActivity;

import static code.checkIn.NurseSelfieFragment.MyPREFERENCES;
import static code.infantsFragment.InfantDetailFragment.kmcPostion;

public class FullScreenImageActivity extends BaseActivity implements View.OnClickListener {

    private ZoomImageAdapter adapter;
    private ViewPager viewPager;
    JSONArray jsonArray;

    int setSelection = 0;
    List<Item> imageList;
    TabLayout tabLayout;
    String postion="";

    long endTime=0;
    long startTime=0;
    int posi=0;
    String kmcpostion;

    RecyclerView list;
    PageChangeListener_Adapter booking_adapter;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList();

    //ImageView
    ImageView imgPrevious,imgForword,btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        getViews();
    }

    private void getViews() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //findViewbyId
        viewPager = findViewById(R.id.pager);
        list=findViewById(R.id.list);
        tabLayout=findViewById(R.id.tabLayout1);

        //imageView
        imgPrevious = findViewById(R.id.imgPrevious);
        imgForword = findViewById(R.id.imgForword);
        btnClose = findViewById(R.id.btnClose);

        //get Value
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        postion=sharedpreferences.getString("kmcpostion", "");

        Bundle extras = getIntent().getExtras();
        if (! extras.isEmpty()){
            setSelection = extras.getInt("setSelection");
        }

        jsonArray = new JSONArray();
        imageList = new ArrayList<Item>();
        imageList.clear();
        getdata();

        adapter = new ZoomImageAdapter(this, imageList);
        viewPager.setAdapter(adapter);
        startTime=System.currentTimeMillis();
        tabLayout.setupWithViewPager(viewPager);
        int length = tabLayout.getTabCount();

        for (int i = 0; i < length; i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, 15, 0);
            tab.requestLayout();
            tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i));
        }

        viewPager.setCurrentItem(setSelection, true);

        if(viewPager.getCurrentItem()==0) {
            imgPrevious.setVisibility(View.INVISIBLE);
        }else  {
            imgPrevious.setVisibility(View.VISIBLE);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {

            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                endTime=System.currentTimeMillis();
//
//                long spendTime=endTime-startTime;
//                long seconds = TimeUnit.MILLISECONDS.toSeconds(spendTime);
//
//                startTime= 0;
//
//                String id =imageList.get(position).photoId;
//                String videoName =imageList.get(position).videoName;
//
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//                String currentDateandTime = sdf.format(new Date());
//                String babyId=AppSettings.getString(AppSettings.babyId);
//                ContentValues mContentValuesPoster = new ContentValues();
//                mContentValuesPoster.put(TableCounsellingPosters.tableColumn.posterId.toString(), id);
//                mContentValuesPoster.put(TableCounsellingPosters.tableColumn.posterTitle.toString(),videoName);
//                mContentValuesPoster.put(TableCounsellingPosters.tableColumn.ConsumeTime.toString(),seconds);
//                mContentValuesPoster.put(TableCounsellingPosters.tableColumn.type.toString(),kmcPostion );
//                mContentValuesPoster.put(TableCounsellingPosters.tableColumn.babyId.toString(),babyId);
//                mContentValuesPoster.put(TableCounsellingPosters.tableColumn.date.toString(),currentDateandTime );
//
//                DatabaseController.insertData(mContentValuesPoster, TableCounsellingPosters.tableName);

            }

            public void onPageSelected(int position) {
                posi=position;
                list.smoothScrollToPosition(position);
                booking_adapter.notifyDataSetChanged();

                if (position == viewPager.getAdapter().getCount() - 1) {
                    if(postion.equals("1")){
                        AppSettings.putString(AppSettings.WhatisKmcViewer, "Yes");
                        AppSettings.putString(AppSettings.WhatisKmcposition, postion);
                    }
                    else if(postion.equals("2")){
                        AppSettings.putString(AppSettings.kmcPositionViewer, "Yes");
                        AppSettings.putString(AppSettings.kmcPositionposition, postion);

                    }
                    else if(postion.equals("3")){
                        AppSettings.putString(AppSettings.kmcNutitionViewer, "Yes");
                        AppSettings.putString(AppSettings.kmcNutitionposition, postion);


                    }else if(postion.equals("4")){
                        AppSettings.putString(AppSettings.kmcHygieneViewer, "Yes");
                        AppSettings.putString(AppSettings.kmcHygieneposition, postion);


                    }else if(postion.equals("5")){
                        AppSettings.putString(AppSettings.kmcMonitoringViewer, "Yes");
                        AppSettings.putString(AppSettings.kmcMonitoringposition, postion);


                    }else if(postion.equals("6")){
                        AppSettings.putString(AppSettings.kmcRespectViewer, "Yes");
                        AppSettings.putString(AppSettings.kmcRespectposition, postion);

                    }


                }

                if(position==0) {
                    imgPrevious.setVisibility(View.INVISIBLE);
                }else  {
                    imgPrevious.setVisibility(View.VISIBLE);
                }

                if(position < viewPager.getAdapter().getCount()-1 ) {
                    imgForword.setVisibility(View.VISIBLE);
                }else  {
                    imgForword.setVisibility(View.INVISIBLE);
                }

            }
        });


        list.setLayoutManager(new LinearLayoutManager(FullScreenImageActivity.this, LinearLayoutManager.HORIZONTAL, false));
        list.setNestedScrollingEnabled(false);

        booking_adapter = new PageChangeListener_Adapter(FullScreenImageActivity.this,imageList);
        list.setAdapter(booking_adapter);
        posi=setSelection;
        list.smoothScrollToPosition(setSelection);
        booking_adapter.notifyDataSetChanged();

        imgForword.setOnClickListener(this);
        imgPrevious.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    private void getdata() {
        SharedPreferences sharedpreferences = FullScreenImageActivity.this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        kmcpostion=sharedpreferences.getString("kmcpostion", "");
        arrayList.addAll(DatabaseController.getCounsellingPoster(kmcpostion));
        for (int i=0; i<arrayList.size();i++){
            Item item = new Item();
            item.photoUrl=arrayList.get(i).get("videoUrl");
            item.photoId=arrayList.get(i).get("posterId");
            item.videoName=arrayList.get(i).get("videoName");
            item.posterUrl_base64=arrayList.get(i).get("posterUrl_base64");
            imageList.add(item);
        }
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.imgPrevious:
               viewPager.setCurrentItem(viewPager.getCurrentItem()-1, true);
               break;
           case R.id.imgForword:
               viewPager.setCurrentItem(viewPager.getCurrentItem()+1, true);
               break;
           case R.id.btnClose:
               finish();
               break;


       }
    }

    public class PageChangeListener_Adapter extends RecyclerView.Adapter<PageChangeListener_Adapter.MyViewHolder>{
        Context context;
        String URl;
        List<Item> arrayList;


        public PageChangeListener_Adapter(Context context,List<Item> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_layout_new, parent, false);
            return new MyViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
//            if (imageList.get(position).photoUrl.length() > 0) {
//                Glide.with(context).load(imageList.get(position).photoUrl)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(holder.icon);
//            }

            byte[] decodedString = Base64.decode(imageList.get(position).posterUrl_base64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.icon.setImageBitmap(decodedByte);

            if (position==posi){
                holder.layout.setBackground(getDrawable(R.drawable.view_border_single_product_on));
            }else {
                holder.layout.setBackground(getDrawable(R.drawable.view_border_for_single_product_image_slider));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(position, true);
                    holder.layout.setBackground(getDrawable(R.drawable.view_border_single_product_on));
                    posi =position;
                    notifyDataSetChanged();
                }
            });


        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView icon;
            RelativeLayout layout;


            public MyViewHolder(View view) {
                super(view);
                icon = view.findViewById(R.id.icon);
                layout = view.findViewById(R.id.layout);


            }
        }
    }

}

