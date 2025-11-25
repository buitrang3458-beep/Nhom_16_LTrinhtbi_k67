package com.example.duan1_catmusic.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.duan1_catmusic.R;
import com.example.duan1_catmusic.fragment.Premium_fm;
import com.example.duan1_catmusic.fragment.Thuvien_fm;
import com.example.duan1_catmusic.fragment.Tim_kiem_fm;
import com.example.duan1_catmusic.fragment.Trangchu_fm;
import com.example.duan1_catmusic.model.Nhac;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class TRANGCHU extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private static final String PREFS_NAME = "MusicPrefs";
    private static final String KEY_TRACK_INDEX = "currentTrackIndex";
    private static final String KEY_POSITION = "currentPosition";
    private static final String KEY_IS_PLAYING = "isPlaying";

    private BottomNavigationView bottomNavigationView;
    private List<Nhac> audioFiles;
    private int currentTrackIndex;
    private ImageView img_choi_nhac, img_next_choi_nhac, img_play_choi_nhac, img_prev_choi_nhac;
    private TextView tv_ten_bai_hat, tv_ten_ca_si;
    private SeekBar seebar_choi_nhac;
    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable updateSeekBarRunnable;
    private RelativeLayout choi_nhac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gd_trangchu);

        img_choi_nhac = findViewById(R.id.img_choi_nhac);
        img_next_choi_nhac = findViewById(R.id.img_next_choi_nhac);
        img_play_choi_nhac = findViewById(R.id.img_play_choi_nhac);
        img_prev_choi_nhac = findViewById(R.id.img_prev_choi_nhac);
        tv_ten_bai_hat = findViewById(R.id.tv_ten_bai_hat);
        tv_ten_ca_si = findViewById(R.id.tv_ten_ca_si);
        seebar_choi_nhac = findViewById(R.id.seebar_choi_nhac);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        choi_nhac = findViewById(R.id.choi_nhac);

        handler = new Handler();

        // Chế độ toàn màn hình
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Khôi phục dữ liệu từ Intent
        Intent intent = getIntent();
        audioFiles = (ArrayList<Nhac>) intent.getSerializableExtra("playlist");
        currentTrackIndex = intent.getIntExtra("currentTrackIndex", 0);

        // Khôi phục trạng thái từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.contains(KEY_TRACK_INDEX)) {
            currentTrackIndex = prefs.getInt(KEY_TRACK_INDEX, 0);
            int savedPosition = prefs.getInt(KEY_POSITION, 0);
            boolean wasPlaying = prefs.getBoolean(KEY_IS_PLAYING, false);

            if (audioFiles != null && !audioFiles.isEmpty()) {
                updateUI(currentTrackIndex);

                // Initialize MediaPlayer only if it's null or if the track index has changed
                if (mediaPlayer == null || currentTrackIndex != prefs.getInt(KEY_TRACK_INDEX, 0)) {
                    releaseMediaPlayer(); // Release any existing MediaPlayer before creating a new one
                    int residnhac = getResources().getIdentifier(audioFiles.get(currentTrackIndex).getFileNhac(), "raw", getPackageName());
                    mediaPlayer = MediaPlayer.create(this, residnhac);
                    seebar_choi_nhac.setMax(mediaPlayer.getDuration());
                }

                if (wasPlaying) {
                    mediaPlayer.seekTo(savedPosition);
                    mediaPlayer.start();
                    isPlaying = true;
                    img_play_choi_nhac.setImageResource(R.drawable.ic_pause);
                    updateSeekBar();
                    showMusicControls(true);
                } else {
                    showMusicControls(true);
                    // Ensure that SeekBar updates correctly even if the track is not playing
                    seebar_choi_nhac.setProgress(savedPosition);
                }
            }
        } else {
            if (audioFiles != null && !audioFiles.isEmpty()) {
                updateUI(currentTrackIndex);
                showMusicControls(true);
                playTrack();
            } else {
                showMusicControls(false);
            }
        }

        choi_nhac.setOnClickListener(v -> {
            Intent newIntent = new Intent(TRANGCHU.this, Screen_listening_music.class);
            newIntent.putExtra("playlist", new ArrayList<>(audioFiles));
            newIntent.putExtra("currentTrackIndex", currentTrackIndex);
            startActivityForResult(newIntent, REQUEST_CODE);
        });

        img_play_choi_nhac.setOnClickListener(v -> {
            if (isPlaying) {
                pauseTrack();
            } else {
                playTrack();
            }
        });

        img_next_choi_nhac.setOnClickListener(v -> nextTrack());
        img_prev_choi_nhac.setOnClickListener(v -> prevTrack());

        setupSeekBar(); // Gọi hàm thiết lập SeekBar

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navHome) {
                loadFragment(new Trangchu_fm(), false);
            } else if (itemId == R.id.navTimKiem) {
                loadFragment(new Tim_kiem_fm(), false);
            } else if (itemId == R.id.navThuVien) {
                loadFragment(new Thuvien_fm(), false);
            } else {
                loadFragment(new Premium_fm(), false);
            }
            return true;
        });

        loadFragment(new Trangchu_fm(), true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer(); // Release MediaPlayer when destroying the activity
        handler.removeCallbacks(updateSeekBarRunnable);

        // Lưu trạng thái vào SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_TRACK_INDEX, currentTrackIndex);
        if (mediaPlayer != null) {
            editor.putInt(KEY_POSITION, mediaPlayer.getCurrentPosition());
            editor.putBoolean(KEY_IS_PLAYING, mediaPlayer.isPlaying());
        } else {
            editor.putInt(KEY_POSITION, 0);
            editor.putBoolean(KEY_IS_PLAYING, false);
        }
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
            img_play_choi_nhac.setImageResource(R.drawable.ic_pause);
            updateSeekBar(); // Resume SeekBar updates
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playTrack() {
        if (audioFiles == null || audioFiles.isEmpty()) return;

        if (mediaPlayer != null) {
            releaseMediaPlayer();
        }

        Nhac track = audioFiles.get(currentTrackIndex);
        int residnhac = getResources().getIdentifier(track.getFileNhac(), "raw", getPackageName());
        mediaPlayer = MediaPlayer.create(this, residnhac);
        seebar_choi_nhac.setMax(mediaPlayer.getDuration());
        mediaPlayer.setOnCompletionListener(mp -> {
            Log.d("MusicPlayer", "Track completed. Moving to next track.");
            nextTrack();
        });

        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            img_play_choi_nhac.setImageResource(R.drawable.ic_pause);
            updateSeekBar();
            Log.d("MusicPlayer", "Playing track index: " + currentTrackIndex);
        }
    }

    private void pauseTrack() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            img_play_choi_nhac.setImageResource(R.drawable.ic_play);
            handler.removeCallbacks(updateSeekBarRunnable); // Dừng cập nhật SeekBar khi tạm dừng nhạc
        }
    }

    private void nextTrack() {
        if (audioFiles != null && !audioFiles.isEmpty()) {
            currentTrackIndex = (currentTrackIndex + 1) % audioFiles.size();
            updateUI(currentTrackIndex);
            playTrack();
            Log.d("MusicPlayer", "Next track index: " + currentTrackIndex);
        }
    }

    private void prevTrack() {
        if (audioFiles != null && !audioFiles.isEmpty()) {
            currentTrackIndex = (currentTrackIndex - 1 + audioFiles.size()) % audioFiles.size();
            updateUI(currentTrackIndex);
            playTrack();
            Log.d("MusicPlayer", "Previous track index: " + currentTrackIndex);
        }
    }

    private void setupSeekBar() {
        seebar_choi_nhac.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    Log.d("SeekBar", "Progress changed: " + progress);
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    pauseTrack(); // Tạm dừng khi bắt đầu kéo thanh seekbar
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && !isPlaying) {
                    playTrack(); // Tiếp tục phát khi dừng kéo thanh seekbar
                }
            }
        });
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    private void showMusicControls(boolean show) {
        img_choi_nhac.setVisibility(show ? View.VISIBLE : View.GONE);
        img_next_choi_nhac.setVisibility(show ? View.VISIBLE : View.GONE);
        img_play_choi_nhac.setVisibility(show ? View.VISIBLE : View.GONE);
        img_prev_choi_nhac.setVisibility(show ? View.VISIBLE : View.GONE);
        tv_ten_bai_hat.setVisibility(show ? View.VISIBLE : View.GONE);
        tv_ten_ca_si.setVisibility(show ? View.VISIBLE : View.GONE);
        seebar_choi_nhac.setVisibility(show ? View.VISIBLE : View.GONE);

        // Hiển thị RelativeLayout sau khi dữ liệu đã được xử lý
        choi_nhac.setVisibility(View.VISIBLE);
    }

    private void updateUI(int trackIndex) {
        if (audioFiles == null || audioFiles.isEmpty()) return;

        Nhac track = audioFiles.get(trackIndex);
        tv_ten_bai_hat.setText(track.getTenNhac());
        tv_ten_ca_si.setText(track.getTenCaSi());
        String hinhNhac = track.getHinhNhac();
        int resID = getResources().getIdentifier(hinhNhac, "drawable", getPackageName());
        img_choi_nhac.setImageResource(resID != 0 ? resID : R.drawable.rosealbum);
    }

    private void updateSeekBar() {
        // Cập nhật SeekBar liên tục
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    seebar_choi_nhac.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(updateSeekBarRunnable);
    }
}
