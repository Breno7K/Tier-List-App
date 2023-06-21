package com.example.tier_list_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tier_list_app.database.DBHelper;
import com.example.tier_list_app.model.TierList;
import com.example.tier_list_app.model.User;

import java.util.ArrayList;
public class HomeActivity extends AppCompatActivity {

    private TextView txtNome;
    private ListView listTierLists;
    DBHelper helper;
    TierList tierList;

    ArrayList<TierList> arrayListTierList;
    ArrayAdapter<TierList> arrayAdapterTierList;
    private int id1,id2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        txtNome = findViewById(R.id.txtNome);

        Bundle args = getIntent().getExtras();
        String name = args.getString("chave_usuario");

        txtNome.setText("Bem vindo " + name);
        listTierLists = findViewById(R.id.listContatos);

        registerForContextMenu(listTierLists);

        listTierLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int i, long l) {
                TierList tierListEnviada = arrayAdapterTierList.getItem(i);
                Intent intent = new Intent(HomeActivity.this, RegistryTierListActivity.class);
                intent.putExtra("chave_usuario", name);
                intent.putExtra("chave_tier_list_id", tierListEnviada.getId());
                startActivity(intent);
            }
        });
        listTierLists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView,View view, int
                    position, long id){
                tierList = arrayAdapterTierList.getItem(position);
                return false;
            }
        });
    }
    public void fillList(String name) {
        helper = new DBHelper(HomeActivity.this);
        User loggedUser = helper.buscarUser(name);

        if (loggedUser != null) {
            arrayListTierList = helper.buscarTierLists(loggedUser);
            helper.close();

            if (listTierLists != null) {
                arrayAdapterTierList = new ArrayAdapter<TierList>(
                        HomeActivity.this,
                        android.R.layout.simple_list_item_1,
                        arrayListTierList);
                listTierLists.setAdapter(arrayAdapterTierList);
            }
        } else {
            System.out.println("Erro");
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        Bundle args = getIntent().getExtras();
        String name = args.getString("chave_usuario");
        fillList(name);
    }

    public void cadastrar(View view) {
        String name = getIntent().getStringExtra("chave_usuario");
        Intent intent = new Intent(HomeActivity.this, RegistryTierListActivity.class);
        intent.putExtra("chave_usuario", name);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View
            v, ContextMenu.ContextMenuInfo menuInfo){

        MenuItem mDelete = menu.add(Menu.NONE, 1, 1,"Deleta Registro");
        MenuItem mSair = menu.add(Menu.NONE, 2, 2,"Cancela");

        mDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                long rowsAffected = 1;

                helper = new DBHelper(HomeActivity.this);
                rowsAffected = helper.excluirTierList(tierList);
                helper.close();

                if(rowsAffected == -1){
                    alert("Erro de exclusão!");
                }
                else{
                    alert("Registro excluído com sucesso!");
                }
                Bundle args = getIntent().getExtras();
                String name = args.getString("chave_usuario");
                fillList(name);

                return false;
            }
        });
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    private void alert(String s){
        Toast.makeText(this,s, Toast.LENGTH_SHORT).show();
    }
}