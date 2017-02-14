package com.example.tom.contentapp;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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
        //未取得權限 向使顯示在第二個欄位TextView用者要求允許權限
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
                /*****************************************/
                //顯示聯絡人電話
                /*
                查詢語法 得到Cursor
                使用電話號麻的URI 為Phone.CONTENT_URI
                傳入準備好的欄位名稱陣列 projection
                 */
                ContactsContract.Contacts.CONTENT_URI,
                null,
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
                        ContactsContract.Contacts.HAS_PHONE_NUMBER,
                        },
                //欄位對應到畫面上應顯示的元件ID值陣列(配合android.R.layout.simple_list_item_2)
                new int[]{android.R.id.text1,android.R.id.text2},
                1){
            //複寫方法 bindView
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                super.bindView(view, context, cursor);
                //先取得單列中的第二個用來顯示電話號碼的TextView
                TextView phone = (TextView)view.findViewById(android.R.id.text2);
                //由cursor取得HAS_PHONE_NUMBER欄位的值 若該列的值是0 顯示空字串
                if (cursor.getInt(cursor.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        == 0) {
                    phone.setText("");
                }
                //不是0代表有電話號碼
                else {
                    //先取得聯絡人ID
                    int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    //進行二次查詢 查詢電話號碼表格
                    Cursor pCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            //查詢條件是電話表格中的外鍵值Phone.CONTACT_ID等於第25行聯絡人ID值
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?",
                            //將聯絡人id值轉為字串後 放在字串陣列中 對應到條件中的第一個問號位置
                            new String[]{String.valueOf(id)},
                            null);
                    //先將第二次查詢結果的pCursor往下移一筆 若有資料則取得第一個電話
                    if(pCursor.moveToFirst()){
                        String number = pCursor.getString(pCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.DATA
                        ));
                        phone.setText(number);
                    }
                }
            }
        };

        list.setAdapter(adapter);
        //執行後才會新增聯絡人
        insertContact();

        /*顯示聯絡人清單
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                cursor,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                new int[]{android.R.id.text1},
                0);*/
    }
    //新增一筆聯絡人方式
    private void insertContact() {
        //準備一個ArrayList集合 存放內容提供者操作指令(操作集合)
        ArrayList ops = new ArrayList();
        //準備索引值 預設值為0
        int index = ops.size();
        //建立一個新增資料操作 並加到操作集合中 資料對象是RawContacts
        //新增成功後 本操作會得到其ID值
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
        //建立一個新資料夾操作 並加到操作集合中 資料對象是ContactsContract.Data
        //取得上一個新增至RawContacts紀錄的ID值 此段最主要是要寫入聯絡人的姓名
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,index)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Jane").build());
        //建立一個新增到phone的電話號碼操作
        //使用第一個新增至RawContacts紀錄的ID值
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,index)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "0900112233")
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        //批次執行操作集合
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}
