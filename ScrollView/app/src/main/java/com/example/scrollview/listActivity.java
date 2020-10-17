package com.example.scrollview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.scrollview.db.ListDB;

import java.util.ArrayList;
import java.util.Map;

public class listActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ListDB listDB=new ListDB(this);
        ArrayList<Map<String, String>> items = listDB.getAlllist();
        SimpleAdapter adapter = new SimpleAdapter(listActivity.this, items, R.layout.item_list,
                new String[]{"l_id","myText"},
                new int[]{R.id.textId, R.id.textViewWord});
        ListView list = (ListView)findViewById(R.id.list);
        registerForContextMenu(list);
        list.setAdapter(adapter);
    }

    public void onclickinsert(View view) {
        ListDB songsDB=new ListDB(this);
        Intent intent=new Intent(listActivity.this,MainActivity.class);
        startActivity(intent);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,

                                    ContextMenu.ContextMenuInfo menuInfo) {

        // Log.v(TAG, "WordItemFragment::onCreateContextMenu()");
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.contextmenu1, menu);
    }

    @Override

    public boolean onContextItemSelected(MenuItem item) {   //上下文菜单
        TextView textId = null;
        AdapterView.AdapterContextMenuInfo info = null;
        View itemView = null;

        switch (item.getItemId()) {
            case R.id.action_delete:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                itemView = info.targetView;
                //删除单词
                textId = (TextView) itemView.findViewById(R.id.textId);
                if (textId != null) {
                    onDeleteDialog(textId.getText().toString());
                }
                break;
            default:break;
        }
        return true;
    }
    public void onDeleteDialog(final String strId) {  //删除
        new android.app.AlertDialog.Builder(this).setTitle("delete").setMessage("是否真的删除?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ListDB listDB=new ListDB(listActivity.this);
                listDB.DeleteUseSql(strId);
                refreshList(listDB);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }
    public void refreshList(ListDB listDB){  //刷新界面
        ListView list = findViewById(R.id.list);
        ArrayList<Map<String, String>> items = listDB.getAlllist();
        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.item_list,
                new String[]{"l_id", "myText"},
                new int[]{R.id.textId, R.id.textViewWord});
        list.setAdapter(adapter);
    }
}