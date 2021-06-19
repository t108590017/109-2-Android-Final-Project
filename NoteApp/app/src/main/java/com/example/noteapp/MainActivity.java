package com.example.noteapp;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements
        OnItemClickListener, OnItemLongClickListener {

    private ListView listview;
    private SimpleAdapter simple_adapter;
    private List<Map<String, Object>> dataList;
    private FloatingActionButton addNote,deleteALL;
    private TextView tv_content;
    private NoteDateBaseHelper DbHelper;
    private SQLiteDatabase DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitView();
    }

    //在activity顯示的時候更新listview
    @Override
    protected void onStart() {
        super.onStart();
        RefreshNotesList();
    }


    private void InitView() {
        tv_content = (TextView) findViewById(R.id.tv_content);
        listview = (ListView) findViewById(R.id.listview);
        dataList = new ArrayList<Map<String, Object>>();
        addNote = findViewById(R.id.btn_editnote);
        deleteALL = findViewById(R.id.btn_delete_all);
        DbHelper = new NoteDateBaseHelper(this);
        DB = DbHelper.getReadableDatabase();

        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);
        addNote.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, noteEdit.class);
                Bundle bundle = new Bundle();
                bundle.putString("title","");
                bundle.putString("info", "");
                bundle.putInt("enter_state", 0);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        deleteALL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Builder builder = new Builder(MainActivity.this);
                builder.setTitle("全部刪除");
                builder.setMessage("確認刪除嗎？");
                builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DB.delete("note", null,null);
                        RefreshNotesList();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create();
                builder.show();
            }
        });

    }


    //重新整理listview
    public void RefreshNotesList() {
        //如果dataList已經有的內容，全部刪掉
        //並且更新simp_adapter
        int size = dataList.size();
        if (size > 0) {
            dataList.removeAll(dataList);
            simple_adapter.notifyDataSetChanged();
        }

        //從資料庫讀取資訊
        Cursor cursor = DB.query("note", null, null, null, null, null, null);
        startManagingCursor(cursor);
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tv_title", title);
            map.put("tv_content",content);
            map.put("tv_date", date);
            dataList.add(map);
        }
        simple_adapter = new SimpleAdapter(this, dataList, R.layout.item,
                new String[]{"tv_title", "tv_date"}, new int[]{
                R.id.tv_content, R.id.tv_date});
        listview.setAdapter(simple_adapter);
    }



    // 點選listview中某一項的點選監聽事件
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        //獲取listview中此個item中的內容
        String content = listview.getItemAtPosition(arg2) + "";
        String content2 = content.substring(content.indexOf("tv_title=") + 9,content.indexOf(", tv_date"));
        String content1 = content.substring(content.indexOf("tv_content=") + 11,content.indexOf("}"));

        Intent myIntent = new Intent(MainActivity.this, noteEdit.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", content2);
        bundle.putString("info", content1);
        bundle.putInt("enter_state", 1);
        myIntent.putExtras(bundle);
        startActivity(myIntent);

    }

    // 點選listview中某一項長時間的點選事件
    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2,
                                   long arg3) {
        Builder builder = new Builder(this);
        builder.setTitle("刪除該項目");
        builder.setMessage("確認刪除嗎？");
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //獲取listview中此個item中的內容
                //刪除該行後重新整理listview的內容
                String content = listview.getItemAtPosition(arg2) + "";
                String content1 = content.substring(content.indexOf("tv_content=") + 11,content.indexOf("}"));
               // DB.delete("note", null,null);
                DB.delete("note", "content = ?", new String[]{content1});
                RefreshNotesList();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create();
        builder.show();
        return true;
    }
    public void deleteAll(){
         DB.delete("note", null,null);
         RefreshNotesList();
    }
}