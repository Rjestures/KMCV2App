package code.checkOut;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmcapp.android.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import code.algo.SyncAllRecord;
import code.checkIn.CheckInActivity;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class NurseSelectionCheckOutFragment extends BaseFragment implements View.OnClickListener {

    //TextView
    private TextView tvNoDataFound;

    //RelativeLayout
    private RelativeLayout rlCircle,rlNext;

    //RecyclerView
    private RecyclerView recyclerView;

    //Adapter for Nurse Listing
    private Adapter adapter;

    //ArrayList for Saving Nurse
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_nurse_selection, container, false);

        initialize(v);

        return v;
    }

    private void initialize(View v) {

        //TextView
        tvNoDataFound   = v.findViewById(R.id.tvNoDataFound);

        //RecyclerView
        recyclerView = v.findViewById(R.id.recyclerView);

        //RelativeLayout
        rlCircle   = v.findViewById(R.id.rlCircle);
        rlNext   = v.findViewById(R.id.rlNext);

        //setOnClickListener
        rlNext.setOnClickListener(this);

        setAdapter();

    }

    private void setAdapter() {

        arrayList.clear();
        arrayList.addAll(DatabaseController.getAllCheckOutNurses());

        rlCircle.setBackgroundResource(R.drawable.circle_grey);

        AppSettings.putString(AppSettings.newNurseId,"");

        //GridLayoutManager
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);

        if(arrayList.size()>0)
        {
            tvNoDataFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new Adapter(arrayList);
            recyclerView.setAdapter(adapter);
        }
        else
        {
            tvNoDataFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.rlNext:


                if(AppSettings.getString(AppSettings.newNurseId).isEmpty())
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.selectNurse));
                }
                else
                {
                    ((CheckOutActivity)getActivity()).displayView(1);
                }

                break;

            default:

                break;
        }
    }

    private class Adapter extends RecyclerView.Adapter<Holder> {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public Adapter(ArrayList<HashMap<String, String>> favList) {
            data = favList;
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_nurse, parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(Holder holder, final int position) {

            try {
                Picasso.get().load(data.get(position).get("profile")).into(holder.ivPic);
            } catch (Exception e) {
                holder.ivPic.setImageResource(R.mipmap.nurse);
            }


            if (holder.ivPic.getDrawable()==null){
                holder.ivPic.setImageResource(R.mipmap.nurse);
            }

            if(data.get(position).get("status").equals("1"))
            {
                holder.llMain.setBackgroundResource(R.drawable.rectangle_teal_selected);
                holder.tvName.setTextColor(getResources().getColor(R.color.white));

                AppSettings.putString(AppSettings.newNurseId,data.get(position).get("id"));
                AppSettings.putString(AppSettings.uuid,data.get(position).get("uuid"));
            }
            else
            {
                holder.llMain.setBackgroundResource(R.drawable.rectangle_grey);
                holder.tvName.setTextColor(getResources().getColor(R.color.blackNew));
            }

            if(!data.get(position).get("name").isEmpty()){
                holder.tvName.setText(data.get(position).get("name"));
            }

            holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    for(int i=0;i<data.size();i++)
                    {
                        HashMap<String, String> hashlist = new HashMap();

                        hashlist.put("uuid", data.get(i).get("uuid"));
                        hashlist.put("id", data.get(i).get("id"));
                        hashlist.put("name",  data.get(i).get("name"));
                        hashlist.put("profile",  data.get(i).get("profile"));
                        hashlist.put("mobile",  data.get(i).get("mobile"));
                        hashlist.put("status", "0");

                        data.set(i,hashlist);
                    }

                    HashMap<String, String> hashlist = new HashMap();

                    hashlist.put("uuid", data.get(position).get("uuid"));
                    hashlist.put("id", data.get(position).get("id"));
                    hashlist.put("name",  data.get(position).get("name"));
                    hashlist.put("profile",  data.get(position).get("profile"));
                    hashlist.put("mobile",  data.get(position).get("mobile"));
                    hashlist.put("status", "1");

                    data.set(position,hashlist);

                    adapter.notifyDataSetChanged();

                    rlCircle.setBackgroundResource(R.drawable.circle_teal);
                }
            });
        }

        public int getItemCount() {
            return  data == null ? 0 : data.size();
        }

    }

    private class Holder extends RecyclerView.ViewHolder {

        //TextView
        TextView tvName;

        //LinearLayout
        LinearLayout llMain;

        //ImageView
        ImageView ivPic;

        public Holder(View itemView) {
            super(itemView);

            //TextView
            tvName =  itemView.findViewById(R.id.tvName);

            //LinearLayout
            llMain = itemView.findViewById(R.id.llMain);

            //ImageView
            ivPic = itemView.findViewById(R.id.ivPic);

        }
    }
}
