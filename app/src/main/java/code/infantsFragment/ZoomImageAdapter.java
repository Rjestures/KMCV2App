package code.infantsFragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kmcapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableCounsellingPosters;

import static code.database.AppSettings.kmcPosition;
import static code.infantsFragment.InfantDetailFragment.kmcPostion;


public class ZoomImageAdapter extends PagerAdapter {

    Context context;
    private Activity _activity;
    List<Item> imageList;
    private LayoutInflater inflater;

    long endTime=0;
    long startTime=0;

    // constructor
    public ZoomImageAdapter(Activity activity, List<Item> imageList) {
        this._activity = activity;
        this.imageList = imageList;
    }
    @Override
    public int getCount() {
        return this.imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ZoomImageView imgDisplay;

        inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen, container,false);

        imgDisplay = (ZoomImageView) viewLayout.findViewById(R.id.imgDisplay);

        byte[] decodedString = Base64.decode(imageList.get(position).posterUrl_base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imgDisplay.setImageBitmap(decodedByte);

        startTime= System.currentTimeMillis();
        new ZoomImageView(_activity);
        ((ViewPager) container).addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
        endTime=System.currentTimeMillis();

        if(endTime!=0) {
            long spendTime = endTime - startTime;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(spendTime);
            startTime = 0;
            String id = imageList.get(position).photoId;
            String videoName = imageList.get(position).videoName;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            String babyId = AppSettings.getString(AppSettings.babyId);
            ContentValues mContentValuesPoster = new ContentValues();
            mContentValuesPoster.put(TableCounsellingPosters.tableColumn.posterId.toString(), id);
            mContentValuesPoster.put(TableCounsellingPosters.tableColumn.posterTitle.toString(), videoName);
            mContentValuesPoster.put(TableCounsellingPosters.tableColumn.ConsumeTime.toString(), seconds);
            mContentValuesPoster.put(TableCounsellingPosters.tableColumn.type.toString(), kmcPostion);
            mContentValuesPoster.put(TableCounsellingPosters.tableColumn.babyId.toString(), babyId);
            mContentValuesPoster.put(TableCounsellingPosters.tableColumn.date.toString(), currentDateandTime);

            endTime=0;
            DatabaseController.insertData(mContentValuesPoster, TableCounsellingPosters.tableName);
        }

    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(_activity).inflate(R.layout.tab_layout, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        if (imageList.get(position).photoUrl.length() > 0) {
            Glide.with(_activity).load(imageList.get(position).photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(icon);
        }

        return view;
    }


}
