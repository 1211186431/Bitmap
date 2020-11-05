package com.example.scrollview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.scrollview.db.InforDB;
import com.example.scrollview.db.ListDB;
import com.example.scrollview.db.javabean.MyImage;
import com.example.scrollview.db.javabean.MyList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListActivity extends AppCompatActivity {
    Boolean needRefresh=false;
    Boolean isStar=false;
    SearchView mSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ListDB listDB=new ListDB(this);
        ArrayList<MyList> items = listDB.getAllList();  //获取列表
        ListAdapter listAdapter=new ListAdapter(items,ListActivity.this);
        needRefresh=true;
        ListView list = (ListView)findViewById(R.id.list);
        registerForContextMenu(list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView txtId=view.findViewById(R.id.textId);
                TextView t2=view.findViewById(R.id.mytext);
                String id=txtId.getText().toString();
                ListDB  listDB1=new ListDB(ListActivity.this);
                String myText=t2.getText().toString();
                Intent intent=
                        new Intent(ListActivity.this,MainActivity.class);
                intent.putExtra("l_id",id);   //把id和内容传过去
                intent.putExtra("myText",myText);
                Log.v("Tag","1+"+id);
                startActivity(intent);
            }
        });
        FloatingActionButton fab = findViewById(R.id.insert);   //悬浮的增加按钮
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickinsert();
            }
        });


    }

    public void onclickinsert() {
        ListDB listDB=new ListDB(this);
        String timeStamp = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date());
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
                textId = (TextView) itemView.findViewById(R.id.textId);
                if (textId != null) {
                    onDeleteDialog(textId.getText().toString());
                }
                break;
            case  R.id.star:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                itemView = info.targetView;
                ImageView imageView=itemView.findViewById(R.id.starP);
                ListDB listDB=new ListDB(ListActivity.this);
                textId = (TextView) itemView.findViewById(R.id.textId);
                if(!isStar){
                    imageView.setImageResource(android.R.drawable.star_big_on);
                    listDB.insertStar(textId.getText().toString());
                    isStar=true;
                }
                else {
                    imageView.setImageBitmap(null);
                    listDB.deleteStar(textId.getText().toString());
                    isStar=false;
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
                InforDB inforDB=new InforDB(ListActivity.this);
                listDeleteFile(strId);   //删除文件
                inforDB.DeleteSql(strId);
                listDB.DeleteUseSql(strId);
                refreshList(listDB);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }
    public void listDeleteFile(String l_id){//删除时删除存的照片
        InforDB inforDB=new InforDB(this);
        ArrayList<MyImage> list=inforDB.getInf(l_id);
        for(int t=0;t<list.size();t++){
            if(list.get(t).getType()==1||list.get(t).getType()==4){
                String deletePath=list.get(t).getPath();
                File file=new File(deletePath);  //删除时把文件删了
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }
    public void refreshList(ListDB listDB){  //刷新界面
        ListView list = (ListView)findViewById(R.id.list);
        ArrayList<MyList> items = listDB.getAllList();
        ListAdapter listAdapter=new ListAdapter(items,ListActivity.this);
        list.setAdapter(listAdapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (needRefresh) {
            refreshList(new ListDB(this));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_list, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) searchItem.getActionView();
        //设置该SearchView显示搜索按钮
        mSearchView.setSubmitButtonEnabled(true);
        //设置默认提示文字
        mSearchView.setQueryHint("输入您想查找的内容");
        //配置监听器
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //点击搜索按钮时触发
            @Override
            public boolean onQueryTextSubmit(String query) {
                //此处添加查询开始后的具体时间和方法
                ListDB listDB=new ListDB(ListActivity.this);
                ListView list = (ListView)findViewById(R.id.list);
                ArrayList<MyList> items = listDB.SearchUseSql(query);
                ListAdapter listAdapter=new ListAdapter(items,ListActivity.this);
                list.setAdapter(listAdapter);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //如果newText长度不为0
                if (TextUtils.isEmpty(newText)){
                    refreshList(new ListDB(ListActivity.this));
                }else{
                    ListDB listDB=new ListDB(ListActivity.this);
                    ListView list = (ListView)findViewById(R.id.list);
                    ArrayList<MyList> items = listDB.SearchUseSql(newText);
                    ListAdapter listAdapter=new ListAdapter(items,ListActivity.this);
                    list.setAdapter(listAdapter);
                }
                return true;

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}