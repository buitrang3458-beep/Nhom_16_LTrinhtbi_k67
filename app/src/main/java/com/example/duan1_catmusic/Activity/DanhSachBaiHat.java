package com.example.duan1_catmusic.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1_catmusic.Adapter.MusicAdapter;
import com.example.duan1_catmusic.DAO.nhacDAO;
import com.example.duan1_catmusic.R;
import com.example.duan1_catmusic.model.Nhac;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.List;

public class DanhSachBaiHat extends AppCompatActivity {
    private List<Nhac> list;
    private RecyclerView rcv_list_danh_sach_nhac;
    private nhacDAO nhac_DAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gd_danh_sach_bai_hat);
        ImageView back = findViewById(R.id.back);
        rcv_list_danh_sach_nhac = findViewById(R.id.rcvlistDanhsachnhac);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        boolean showAllSongs = intent.getBooleanExtra("all_songs", false);
        list = new ArrayList<>();
        nhac_DAO = new nhacDAO(this);
        if (showAllSongs) {
            loadAllData();
        } else {
            String artistName = intent.getStringExtra("ten_nghe_si");
            loaddata(artistName);
        }
    }

    private void loaddata(String artistName) {
        // Lọc danh sách bài hát theo tên nghệ sĩ
        list = nhac_DAO.getSongsByArtist(artistName);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        rcv_list_danh_sach_nhac.setLayoutManager(manager);

        MusicAdapter adapter = new MusicAdapter(this, list);
        rcv_list_danh_sach_nhac.setAdapter(adapter);

        // Set item click listener to open Screen_listening_music
        adapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(DanhSachBaiHat.this, Screen_listening_music.class);
                intent.putExtra("playlist", (ArrayList<Nhac>) list); // Truyền danh sách phát
                intent.putExtra("currentTrackIndex", position); // Truyền vị trí bài hát hiện tại
                startActivity(intent);
            }
        });
    }

    private void loadAllData(boolean shuffle) {
        // Tải tất cả bài hát
        list = nhac_DAO.getSongArtistList();

        if (shuffle) {
            Collections.shuffle(list); // Trộn danh sách
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        rcv_list_danh_sach_nhac.setLayoutManager(manager);

        MusicAdapter adapter = new MusicAdapter(this, list);
        rcv_list_danh_sach_nhac.setAdapter(adapter);

        // Đặt trình nghe sự kiện khi bấm vào item để mở Screen_listening_music
        adapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(DanhSachBaiHat.this, Screen_listening_music.class);
                intent.putExtra("playlist", (ArrayList<Nhac>) list); // Truyền danh sách phát
                intent.putExtra("currentTrackIndex", position); // Truyền vị trí bài hát hiện tại
                startActivity(intent);
            }
        });
    }

    // Phương thức gọi loadAllData với shuffle
    private void loadAllData() {
        loadAllData(true); // Mặc định không trộn
    }

    // Phương thức gọi loadAllData với shuffle được kích hoạt
    private void loadAllDataShuffled() {
        loadAllData(true); // Kích hoạt trộn danh sách
    }
    }
