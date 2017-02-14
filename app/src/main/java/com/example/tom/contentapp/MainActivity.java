package com.example.tom.contentapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.WRITE_CONTACTS;

public class MainActivity extends AppCompatActivity {
       //代表向使用者要求讀取聯絡人的辨識值
       private static final int REQUEST_CONTACTS=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //檢查應用程式是否已向使用者要求讀取聯絡人的權限
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);
        //判斷檢查後的結果permission
//      //未取得權限 向使用者要求允許權限
        if(permission != PackageManager.PERMISSION_GRANTED) {
            /*
            ActivityCompat.requestPermissions(Context context,
                String[] permissions(欲要求的權限),
                int requestCode(本次請求的辨識編號))
                *第三個參數目的是設定一個當使用者決定後返回
                 onRequestPermissionsResult方法時的辨識號碼
                 應在類別中定義符合其功能的常數名稱 以提高可讀性
                 需先定義一int值 值可以是 0 or 1 等不與其他權限相關重複地即可
             */
            //若尚未取得權限則向使用者要求允許聯絡人讀取與寫入的權限
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_CONTACTS, WRITE_CONTACTS},
                    REQUEST_CONTACTS);
        }
        //已有權限 可進行檔案存取
        else {
            //alt+enter 選擇產生的readContacts的方法
            readContacts();
        }
    }
    //當使用者按下允許或拒絕時 會自動執行onRequestPermissionsResult
    //請複寫此方法 alt+o
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CONTACTS:
                if (grantResults.length>0 && grantResults[0]==
                        PackageManager.PERMISSION_GRANTED){
                    readContacts();
                }else {
                    new AlertDialog.Builder(this)
                            .setMessage("必須允許聯絡人權限才能顯示資料")
                            .setPositiveButton("ok",null)
                            .show();
                }
                return;
        }
    }

    private void readContacts() {
        
    }
}
