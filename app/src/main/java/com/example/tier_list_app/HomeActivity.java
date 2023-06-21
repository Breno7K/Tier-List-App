package com.example.tier_list_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tier_list_app.database.DBHelper;
import com.example.tier_list_app.model.TierList;
import com.example.tier_list_app.model.User;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private static final int EDIT_TIER_LIST_REQUEST = 1;

    private TextView txtNome;
    private ListView listTierLists;
    private DBHelper helper;
    private TierList tierList;

    private ArrayList<TierList> arrayListTierList;
    private ArrayAdapter<TierList> arrayAdapterTierList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtNome = findViewById(R.id.txtNome);
        listTierLists = findViewById(R.id.listContatos);

        registerForContextMenu(listTierLists);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            String name = args.getString("chave_usuario");
            txtNome.setText("Bem vindo " + name);
            fillList(name);
        }

        listTierLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                TierList tierListEnviada = arrayAdapterTierList.getItem(position);
                Intent intent = new Intent(HomeActivity.this, RegistryTierListActivity.class);
                intent.putExtra("chave_usuario", txtNome.getText().toString().replace("Bem vindo ", ""));
                intent.putExtra("chave_tier_list_id", tierListEnviada.getId());
                startActivity(intent);
            }
        });

        listTierLists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                tierList = arrayAdapterTierList.getItem(position);
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_TIER_LIST_REQUEST && resultCode == RESULT_OK) {
            String name = txtNome.getText().toString().replace("Bem vindo ", "");
            fillList(name);
        }
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
                        arrayListTierList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                            convertView = getLayoutInflater().inflate(R.layout.list_item_tier, null);
                        }

                        TextView textView = convertView.findViewById(R.id.textViewTier);
                        textView.setText(arrayListTierList.get(position).getName());

                        Button editButton = convertView.findViewById(R.id.buttonEdit);
                        Button deleteButton = convertView.findViewById(R.id.buttonDelete);

                        editButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TierList selectedTierList = arrayListTierList.get(position);
                                Intent intent = new Intent(HomeActivity.this, RegistryTierListActivity.class);
                                intent.putExtra("chave_tier_list_id", selectedTierList.getId());
                                intent.putExtra("chave_usuario", selectedTierList.getUsername());
                                startActivityForResult(intent, EDIT_TIER_LIST_REQUEST);
                            }
                        });

                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TierList selectedTierList = arrayListTierList.get(position);
                                long rowsAffected = helper.excluirTierList(selectedTierList);
                                if (rowsAffected != -1) {
                                    fillList(name);
                                } else {
                                    Toast.makeText(HomeActivity.this, "Failed to delete tier list", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        return convertView;
                    }
                };

                listTierLists.setAdapter(arrayAdapterTierList);
            }
        } else {
            System.out.println("Erro");
        }
    }

    public void cadastrar(View view) {
        String name = getIntent().getStringExtra("chave_usuario");
        Intent intent = new Intent(HomeActivity.this, RegistryTierListActivity.class);
        intent.putExtra("chave_usuario", name);
        startActivityForResult(intent, EDIT_TIER_LIST_REQUEST);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Options");
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Cancel");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        if (item.getTitle().equals("Delete")) {
            TierList selectedTierList = arrayListTierList.get(position);
            helper = new DBHelper(HomeActivity.this);
            long rowsAffected = helper.excluirTierList(selectedTierList);
            helper.close();

            if (rowsAffected == -1) {
                alert("Erro de exclusão!");
            } else {
                alert("Registro excluído com sucesso!");
            }

            fillList(txtNome.getText().toString().replace("Bem vindo ", ""));
        } else if (item.getTitle().equals("Cancel")) {
            // Handle cancel option if needed
        }

        return super.onContextItemSelected(item);
    }

    private void alert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
