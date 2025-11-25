package com.example.duan1_catmusic.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.duan1_catmusic.DAO.nhacDAO;
import com.example.duan1_catmusic.R;
import com.example.duan1_catmusic.model.Nhac;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import android.os.Handler;
import android.os.Looper;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Nhac> list;
    private nhacDAO nhacDAO;
    private List<Nhac> filteredList;
    private OnItemClickListener onItemClickListener;
    private Map<Integer, Integer> clickCountMap = new HashMap<>(); // Đếm số lần click cho từng item
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable sortRunnable;
    private   boolean isImageOne = true;

    public MusicAdapter(Context context, List<Nhac> list) {
        this.context = context;
        this.list = list;
        this.nhacDAO = new nhacDAO(context);
        this.filteredList = new ArrayList<>(list);

        for (int i = 0; i < list.size(); i++) {
            clickCountMap.put(i, 0);
        }
    }

    public void updateData(List<Nhac> newList) {
        this.list = newList;
        clickCountMap.clear();
        for (int i = 0; i < list.size(); i++) {
            clickCountMap.put(i, 0);
        }
        notifyDataSetChanged();  // Cập nhật giao diện người dùng với dữ liệu mới
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_bai_nhac, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Nhac nhac = list.get(position);
        holder.tvTenNhac.setText(nhac.getTenNhac());
        holder.tvNgheSi.setText(nhac.getTenCaSi());

        // Hiển thị hình ảnh
        String imgNhac = nhac.getHinhNhac();
        int resID = context.getResources().getIdentifier(imgNhac, "drawable", context.getPackageName());
        holder.imgNhac.setImageResource(resID);

        holder.imLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isImageOne){
                    holder.imLove.setImageResource(R.drawable.love);
                    SharedPreferences sharedPreferences ;
                    Toast toast = Toast.makeText(context,"Bạn Đã thêm bài hát vào Danh Sách Yêu Thích",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.LEFT | Gravity.TOP, 20, 30);
                    toast.show();

                }else {
                    holder.imLove.setImageResource(R.drawable.love1);
                    Toast toast = Toast.makeText(context,"Bạn đã bỏ bài hát ra khỏi Danh Sách Yêu Thích",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.LEFT | Gravity.TOP, 20, 30);
                    toast.show();
                }
                isImageOne = !isImageOne;
            }
        });
        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // Cập nhật số lần click
                    int currentCount = clickCountMap.get(pos);
                    clickCountMap.put(pos, currentCount + 1);

                    // Hủy bỏ bất kỳ tác vụ sắp xếp nào đã được lên lịch trước đó
                    if (sortRunnable != null) {
                        handler.removeCallbacks(sortRunnable);
                    }

                    // Lên lịch sắp xếp danh sách sau 3 giây
                    sortRunnable = () -> {
                        // Sắp xếp danh sách theo số lần click
                        Collections.sort(list, (o1, o2) -> {
                            int index1 = list.indexOf(o1);
                            int index2 = list.indexOf(o2);
                            int count1 = clickCountMap.get(index1);
                            int count2 = clickCountMap.get(index2);

                            return Integer.compare(count2, count1); // Sắp xếp giảm dần
                        });
                        notifyDataSetChanged(); // Cập nhật giao diện người dùng
                    };
                    handler.postDelayed(sortRunnable, 3000); // Đợi 3 giây trước khi thực hiện sắp xếp

                    onItemClickListener.onItemClick(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                List<Nhac> filtered = null;
                if (filterPattern.isEmpty()) {
                    list = filteredList;
                } else {
                    filtered = new ArrayList<>();
                    for (Nhac item : filteredList) {
                        if (item.getTenNhac().toLowerCase().contains(filterPattern)) {
                            filtered.add(item);
                        }
                    }
                    list = filtered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
//                filterResults.count = filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                list = (List<Nhac>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgNhac, imLove;
        TextView tvTenNhac;
        TextView tvNgheSi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNhac = itemView.findViewById(R.id.img_item_listnhac);
            tvTenNhac = itemView.findViewById(R.id.tvTenNhac);
            tvNgheSi = itemView.findViewById(R.id.tvNgheSi);
            imLove = itemView.findViewById(R.id.imgLove);




        }

    }
}
