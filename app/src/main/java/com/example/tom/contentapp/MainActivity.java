package com.example.tom.contentapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

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
        //未取得權限 向使用者要求允許權限
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

    //用readContacts來進行查詢
    //顯示聯絡人電話時(不顯示無電話連絡人)用Implict John
    private void readContacts() {
        //先取得ContentResolver物件
        ContentResolver resolver = getContentResolver();
       /*
       準備將在query方法出現的欄位參數
       因為使用跨表格查詢 所以將欄位的鍵值Contacts._ID納入
       再將未來需要顯示到畫面中的Contacts.DISPLAY_NAME聯絡人名單
       Phone.NUMBER電話號碼加入欄位陣列中
        */
        String[] projection = {ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        //查詢使用的是query方法
        /*
        public final Cursor query(
        Uri uri(欲查詢的Uri),
        String[] projection(查詢回傳的表格欄位),
        String[] selection(SQL的where),
        String[] selectionArgs(當where中有參數的時候 在此),
        String sortOrder(查詢結果排序字串"ASC"小到大 "DESC"大到小)
         */
        //查詢手機中的所有聯絡人 並得到cursor物件
        Cursor cursor = resolver.query(
                /* 顯示聯絡人清單
                ContactsContract.Contacts.CONTENT_URI,
                null,null,null,null);
                 */
                //顯示聯絡人電話
                /*
                查詢語法 得到Cursor
                使用電話號麻的URI 為Phone.CONTENT_URI
                傳入準備好的欄位名稱陣列 projection
                 */
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null);

        //cursor.moveToNext 可將Cursor向下移動一筆(處理每一筆資料)
        /*
        有資料則回傳true無怎回傳false,用while迴圈依序處理查詢每一筆資料
         */
        /* (清單元件的資料來源是查詢Cursor物件 因此使用SimpleCursorAdapter 所以先把while註解掉)
        while (cursor.moveToNext()){
            //利用Cursor的getString,getInt,getFloat等方法取得該筆資料的欄位值
            //getXX都需要int整數型態的參數 代表的是該欄位在查詢結果中的索引值Column Index
            //想得到欄位在查詢結果的索引值 可呼叫cursor.getColumnIndex 並提供欄位名稱參數
            int id = cursor.getInt(cursor.getColumnIndex(ContactsContract
            .Contacts._ID));
            String name = cursor.getString(
                    cursor.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));
            Log.d("RECORD",id+"/"+name);

        }
        */
        //SimpleCursorAdapter是將資料庫查詢的結果顯示在ListView的每一列
        /*
        所需參數
        Context context this
        int layout 每一列的版面配置檔的資源ID
        Cursor cursor 查詢內容提供者聯絡人所得到的Cursor物件
        String[] from 資料查詢結果Cursor中想要顯示的欄位名稱
        String[] to 資料顯示的元件ID
        int flag 給0 表示資料庫中的紀錄如果被更動 ListView 將不自動重新查詢並更新資料
         */

        ListView list = (ListView)findViewById(R.id.list);
        //顯示聯絡人電話
        //建立SimpleCursorAdapter物件
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                //每列顯示兩欄資料
                android.R.layout.simple_list_item_2,
                //傳入查詢結果Cursor
                cursor,
                //傳入cusor物件中的欄位字串陣列
                new String[]{
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER},
                //欄位對應到畫面上應顯示的元件ID值陣列(配合android.R.layout.simple_list_item_2)
                new int[]{android.R.id.text1,android.R.id.text2},
                1);
        list.setAdapter(adapter);

        /*顯示聯絡人清單
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                cursor,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                new int[]{android.R.id.text1},
                0);*/
    }
}
