package code.infantsFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kmcapp.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import code.database.AppSettings;
import code.database.DatabaseController;
import code.main.FullScreenImageActivity;
import code.main.ReferralListActivity;
import code.utils.AppConstants;
import code.utils.AppUtils;
import code.view.BaseFragment;

import static code.checkIn.NurseSelfieFragment.MyPREFERENCES;


public class SelectBannerFragment extends BaseFragment implements View.OnClickListener {

    RecyclerView  recyclerView;
    private GridLayoutManager mGridLayoutManager;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList();
    private Adapter adapter;
    String kmcpostion;
    List<Item> getimageList= new ArrayList<Item>();
    List<Item> sliderImageList = new ArrayList<Item>();
    BitmapFactory.Options options;
    String imageInSD;
    public SelectBannerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_selectbaner, container, false);
        getViews(view);
        return view;
    }


    private void getViews(View view) {

        SharedPreferences sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        kmcpostion=sharedpreferences.getString("kmcpostion", "");

        recyclerView=view.findViewById(R.id.recyclerView);

        arrayList.clear();
        sliderImageList.clear();
        getimageList.clear();

        arrayList.addAll(DatabaseController.getCounsellingPoster(kmcpostion));

        for (int i=0; i<arrayList.size();i++){
            Item item = new Item();
            item.photoUrl=arrayList.get(i).get("videoUrl");
            item.photoId=arrayList.get(i).get("posterId");
            item.videoName=arrayList.get(i).get("videoName");
            item.posterUrl_base64=arrayList.get(i).get("posterUrl_base64");
            sliderImageList.add(item);

        }

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);

        if(sliderImageList.size()!=0){
            adapter = new Adapter(getContext(),sliderImageList);
            recyclerView.setAdapter(adapter);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recyclerView:
                break;
        }
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        Context context;
        LayoutInflater inflater;


        public Adapter(Context context, List<Item> imageList) {
            try {
                this.context = context;
                getimageList = imageList;
                this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            }catch (Exception e){

            }
        }

        @NonNull
        @Override
        public Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_row_selectbanner, viewGroup, false);
            return new Adapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.MyViewHolder holder, final int position) {

            try {

                byte[] decodedString = Base64.decode(getimageList.get(position).posterUrl_base64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imageView.setImageBitmap(decodedByte);

            } catch (Exception e) {
            }



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//
//                    JSONObject jsonObject = new JSONObject();
//                    JSONArray jsonArray = new JSONArray();
//
//                    try {
//                        for (Item item : getimageList){
//
//                            JSONObject object = new JSONObject();
//                            object.put("popup", item.photoUrl);
//                            object.put("photoId", item.photoId);
//                            object.put("videoName", item.videoName);
//                            object.put("posterUrl_base64", item.posterUrl_base64);
//
//                            jsonArray.put(object);
//                        }
//                        jsonObject.put("jsonArray",jsonArray);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                    Intent intent = new Intent(getContext(), FullScreenImageActivity.class);
                    //intent.putExtra("jsonObject", sliderImageList.toString());
                    intent.putExtra("setSelection", position);
                    getContext().startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return getimageList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            //ImageView
            ImageView imageView;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                //ImageView
                imageView = itemView.findViewById(R.id.imageView);

            }
        }
    }


}