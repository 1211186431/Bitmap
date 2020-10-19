package com.example.scrollview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.scrollview.db.ListDB;
import com.example.scrollview.otherActivity.DrawActivity;
import com.example.scrollview.otherActivity.MusicActivity;
import com.example.scrollview.otherActivity.PhotoActivity;
import com.example.scrollview.otherActivity.VideoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ListActivity extends AppCompatActivity {
    Boolean needRefresh=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ListDB listDB=new ListDB(this);
        ArrayList<Map<String, String>> items = listDB.getAlllist();
        SimpleAdapter adapter = new SimpleAdapter(ListActivity.this, items, R.layout.item_list,
                new String[]{"l_id","myText"},
                new int[]{R.id.textId, R.id.textViewWord});
        needRefresh=true;
        ListView list = (ListView)findViewById(R.id.list);
        registerForContextMenu(list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView txtId=view.findViewById(R.id.textId);
                TextView t2=view.findViewById(R.id.textViewWord);
                String id=txtId.getText().toString();
                String myText=t2.getText().toString();
                Intent intent=
                        new Intent(ListActivity.this,MainActivity.class);  //没有直接传回去，传sheet。直接传有问题
                intent.putExtra("l_id",id);
                intent.putExtra("myText",myText);
                Log.v("Tag","1+"+id);
                startActivity(intent);
            }
        });
        FloatingActionButton fab = findViewById(R.id.insert);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickinsert();
            }
        });
    }

    public void onclickinsert() {
        ListDB listDB=new ListDB(this);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String l_id=listDB.InsertUserSql("",-1,timeStamp);
        Intent intent=new Intent(ListActivity.this,MainActivity.class);
        intent.putExtra("l_id",l_id);
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
                ListDB listDB=new ListDB(ListActivity.this);
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
    @Override
    protected void onResume() {
        super.onResume();
        if (needRefresh) {
            refreshList(new ListDB(this));
        }
    }
}