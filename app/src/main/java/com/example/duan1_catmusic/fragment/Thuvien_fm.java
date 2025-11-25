package com.example.duan1_catmusic.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.duan1_catmusic.Activity.DanhSachBaiHat;
import com.example.duan1_catmusic.Activity.Screen_ListSong_YeuThich;
import com.example.duan1_catmusic.Activity.Screen_ThuVien_NgheSi;
import com.example.duan1_catmusic.Activity.Screen_listening_music;
import com.example.duan1_catmusic.Activity.TRANGCHU;
import com.example.duan1_catmusic.Adapter.MusicAdapter;
import com.example.duan1_catmusic.Adapter.thuvienAdapter;
import com.example.duan1_catmusic.DAO.casiDAO;
import com.example.duan1_catmusic.DAO.nhacDAO;
import com.example.duan1_catmusic.R;
import com.example.duan1_catmusic.model.Casi;
import com.example.duan1_catmusic.model.Nhac;

import java.util.ArrayList;
import java.util.List;

public class Thuvien_fm extends Fragment {

    private RecyclerView  recyclerView;
//    private com.example.duan1_catmusic.DAO.casiDAO casiDAO;
    private List<Nhac> list;
    private nhacDAO nhac_DAO;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_thuvien, container, false);
        TextView tvListNgheSi = view.findViewById(R.id.tvListNgheSi);
        TextView tvListyeuThich = view.findViewById(R.id.tvListYeuThich);
                recyclerView = view.findViewById(R.id.rcvlistDanhsachnhac);
        Intent intent = new Intent();
        boolean showAllSongs = intent.getBooleanExtra("all_songs", false);
        list = new ArrayList<>();
        nhac_DAO = new nhacDAO(getContext());

//        list = nhac_DAO.getSongArtistList();
        list = nhac_DAO.getSongArtistList();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        MusicAdapter adapter = new MusicAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        SearchView edt_timkiem = view.findViewById(R.id.edt_timkiem);
        edt_timkiem.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return true;
            }
        });
        adapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), Screen_listening_music.class);
                intent.putExtra("playlist", (ArrayList<Nhac>) list); // Truyền danh sách phát
                intent.putExtra("currentTrackIndex", position); // Truyền vị trí bài hát hiện tại
                startActivity(intent);
            }
        });

        //Tạo Album ds bài hát + tạo dialog
//        add_ds.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });



        tvListNgheSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Screen_ThuVien_NgheSi.class);
                startActivity(intent);
            }
        });
//        ArrayList<Casi> list = casiDAO.getcasi();
//        LinearLayoutManager manager = new LinearLayoutManager(getContext());
//        Casi casi;
//                manager.setOrientation(RecyclerView.VERTICAL);
//        rvlistCasi.setLayoutManager(manager);
//
//        thuvienAdapter adapter = new thuvienAdapter(getContext(), list);
//        rvlistCasi.setAdapter(adapter);

        tvListyeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), Screen_ListSong_YeuThich.class);
                startActivity(intent);

            }
        });
        return view;

    }
}