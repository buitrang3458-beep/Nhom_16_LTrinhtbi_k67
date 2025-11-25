package com.example.duan1_catmusic.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.duan1_catmusic.database.Dbhelper;

public class NguoiDungDAO {
    private Dbhelper dbhelper;
    SharedPreferences sharedPreferences;

    public NguoiDungDAO(Context context) {
        dbhelper = new Dbhelper(context);
        sharedPreferences = context.getSharedPreferences("dataUser", Context.MODE_PRIVATE);
    }
    // đăng kys
    public int Dangky(String Gmail, String MatKhaudk ,String NamSinh,String GioiTinh, String TenUserdk ){
        SQLiteDatabase sqLiteDatabase = dbhelper.getWritableDatabase();
        // kiêểm tra
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM User WHERE Gmail = ? OR TenUser = ?", new String[]{Gmail,TenUserdk});
        if (cursor.getCount()>0){
            return 0;
        }else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("Gmail", Gmail);
            contentValues.put("MatKhau", MatKhaudk);
            contentValues.put("NamSinh", NamSinh);
            contentValues.put("GioiTinh", GioiTinh);
            contentValues.put("TenUser",TenUserdk);
            long check = sqLiteDatabase.insert("User", null, contentValues);
            return (check == -1) ? -1 : 1;
        }
    }


    // kiểm tra thông tin đăng nhập
    public boolean KiemTraDangNhap(String TenUserOrGmail, String MatKhau) {
        SQLiteDatabase sqLiteDatabase = dbhelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM User WHERE (TenUser = ? OR Gmail = ?) AND MatKhau = ?",
                new String[]{TenUserOrGmail, TenUserOrGmail, MatKhau}
        );

        boolean result = false;

        if (cursor != null && cursor.moveToFirst()) {
            // Lưu role acc
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("role", cursor.getInt(7));
            editor.apply();
            result = true;
        }

        if (cursor != null) {
            cursor.close();
        }

        return result;
    }
}
