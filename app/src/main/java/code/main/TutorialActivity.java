package code.main;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kmcapp.android.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import code.common.LocaleHelper;
import code.database.DatabaseController;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class TutorialActivity extends BaseActivity implements View.OnClickListener {

    //RecyclerView
    RecyclerView recyclerView;

    //GridLayoutManager
    GridLayoutManager mGridLayoutManager;

    Adapter adapter;

    ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    TextView tvNoDataFound,tvHeading;

    //LinearLayout
    LinearLayout llBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        initialize();
        setLearningVideos();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void initialize() {

        //RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        //TextView
        tvNoDataFound = findViewById(R.id.tvNoDataFound);
        tvHeading = findViewById(R.id.tvHeading);

        //LinearLayout
        llBack = findViewById(R.id.llBack);

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);

        tvHeading.setText(getString(R.string.tutorial));

        //setOnClickListener
        llBack.setOnClickListener(this);
    }

    private void setLearningVideos() {

        arrayList.clear();
        arrayList.addAll(DatabaseController.getCounsellingVideo());

        if(arrayList.size()==0)
        {
            tvNoDataFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else
        {
            tvNoDataFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter = new Adapter(arrayList);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.llBack:

                onBackPressed();

                break;

            default:

                break;
        }
    }


    private class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public Adapter(ArrayList<HashMap<String, String>> favList) {
            data = favList;
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_video, parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(Holder holder, final int position) {

            holder.tvName.setText(data.get(position).get("videoName"));
            holder.tvTime.setText(data.get(position).get("addDate"));

            holder.rlMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    VideoActivity.videoURL = data.get(position).get("videoLocation");
                    startActivity(new Intent(mActivity, VideoActivity.class));
                }
            });

            try {

                Bitmap bitmap = AppUtils.retriveVideoFrameFromVideo(data.get(position).get("videoLocation"));
                if (bitmap != null) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, 240, 240, false);
                    holder.ivPic.setImageBitmap(bitmap);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            /*try {
                Picasso.get().load(data.get(position).get("videoThumb")).into(holder.ivPic);
            } catch (Exception e) {
                holder.ivPic.setImageResource(R.mipmap.logo);
            }*/

            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(data.get(position).get("videoLocation"));
                long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                retriever.release();

                holder.tvDuration.setText(convertMillieToHMmSs(duration));

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        }

        public int getItemCount() {
            return data.size();
        }

        private class Holder extends RecyclerView.ViewHolder {

            //TextView
            TextView tvName, tvTime,tvDuration;

            //RelativeLayout
            RelativeLayout rlMain;

            //ImageView
            ImageView ivPic;

            public Holder(View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tvName);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvDuration = itemView.findViewById(R.id.tvDuration);

                //ImageView
                ivPic = itemView.findViewById(R.id.ivPic);

                rlMain = itemView.findViewById(R.id.rlMain);

            }
        }

    }

    public static String convertMillieToHMmSs(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;

        String result = "";
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
        else {
            return String.format("%02d:%02d" , minute, second);
        }

    }

}
