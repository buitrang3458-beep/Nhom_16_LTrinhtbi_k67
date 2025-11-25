package com.example.duan1_catmusic.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.duan1_catmusic.Activity.DanhSachBaiHat;
import com.example.duan1_catmusic.Activity.Screen_listening_music;
import com.example.duan1_catmusic.Adapter.MusicAdapter;
import com.example.duan1_catmusic.Adapter.casiAdapter;
import com.example.duan1_catmusic.DAO.casiDAO;
import com.example.duan1_catmusic.DAO.nhacDAO;
import com.example.duan1_catmusic.R;
import com.example.duan1_catmusic.model.Casi;
import com.example.duan1_catmusic.model.Nhac;

import java.util.ArrayList;
import java.util.List;

public class Trangchu_fm extends Fragment {

    private RecyclerView rcvnghesi, rcvtop;
    private casiDAO casiDAO;
    private Button btn_allSong;
    private nhacDAO nhacDAO;
    private MusicAdapter adapternhac;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trangchu, container, false);

        rcvnghesi = view.findViewById(R.id.rcvnghesi);
        btn_allSong = view.findViewById(R.id.btn_allSong);

        casiDAO = new casiDAO(getContext());
        ArrayList<Casi> list = casiDAO.getcasi();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        rcvnghesi.setLayoutManager(manager);
        casiAdapter adapter = new casiAdapter(list, getContext());
        rcvnghesi.setAdapter(adapter);

        rcvtop = view.findViewById(R.id.rvNhac);

        //data
        nhacDAO = new nhacDAO(getContext());
        List<Nhac> listnhac = nhacDAO.getSongArtistList();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvtop.setLayoutManager(linearLayoutManager);
        adapternhac = new MusicAdapter(getContext(),listnhac); // Khởi tạo MusicAdapter
        rcvtop.setAdapter(adapternhac);


        // Đặt trình nghe sự kiện khi bấm vào item để mở Screen_listening_music
        adapternhac.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), Screen_listening_music.class);
                intent.putExtra("playlist", new ArrayList<>(listnhac)); // Truyền danh sách phát
                intent.putExtra("currentTrackIndex", position); // Truyền vị trí bài hát hiện tại
                startActivity(intent);
            }
        });

//        btn_allSong.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), DanhSachBaiHat.class);
//                intent.putExtra("all_songs", true);
//                startActivity(intent);
//            }
//        });

        return view;
    }


}
