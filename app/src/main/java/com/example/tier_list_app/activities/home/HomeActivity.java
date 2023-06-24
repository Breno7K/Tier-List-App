package com.example.tier_list_app.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tier_list_app.R;
import com.example.tier_list_app.activities.tier_list.RegistryTierListActivity;
import com.example.tier_list_app.activities.tier_list.TierListViewActivity;
import com.example.tier_list_app.database.DBHelper;
import com.example.tier_list_app.model.TierList;
import com.example.tier_list_app.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_TIER_LIST = 1;

    private TextView txtNome;
    private ListView listTierLists;
    private DBHelper helper;
    private TierList tierList;

    private ArrayList<TierList> arrayListTierList;
    private ArrayAdapter<TierList> arrayAdapterTierList;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tier_list_list_view);

        firestore = FirebaseFirestore.getInstance();


        txtNome = findViewById(R.id.txtNome);
        listTierLists = findViewById(R.id.listTierLists);

        registerForContextMenu(listTierLists);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            String name = args.getString("chave_usuario");
            txtNome.setText("Bem vindo " + name);
            fillList(name);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TIER_LIST) {
            if (resultCode == RESULT_OK) {
                String username = getIntent().getStringExtra("chave_usuario");
                fillList(username);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle args = getIntent().getExtras();
        String name = args.getString("chave_usuario");
        fillList(name);
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
                            convertView = getLayoutInflater().inflate(R.layout.tier_list_list, null);
                        }

                        TextView textView = convertView.findViewById(R.id.textViewTier);
                        textView.setText(arrayListTierList.get(position).getName());

                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TierList selectedTierList = arrayListTierList.get(position);
                                Intent intent = new Intent(HomeActivity.this, TierListViewActivity.class);
                                intent.putExtra("chave_tier_list_id", selectedTierList.getId());
                                intent.putExtra("tier_list_name", selectedTierList.getName());
                                intent.putExtra("chave_usuario", selectedTierList.getUsername());
                                startActivity(intent);
                            }
                        });

                        Button editButton = convertView.findViewById(R.id.buttonEdit);
                        Button deleteButton = convertView.findViewById(R.id.buttonDelete);

                        editButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TierList selectedTierList = arrayListTierList.get(position);
                                Intent intent = new Intent(HomeActivity.this, RegistryTierListActivity.class);
                                intent.putExtra("chave_tier_list_id", selectedTierList.getId());
                                intent.putExtra("tier_list_name", selectedTierList.getName());
                                intent.putExtra("chave_usuario", selectedTierList.getUsername());
                                startActivity(intent);
                            }
                        });

                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TierList selectedTierList = arrayListTierList.get(position);
                                long rowsAffected = helper.excluirTierList(selectedTierList);
                                if (rowsAffected != -1) {
                                    arrayListTierList.remove(position);
                                    arrayAdapterTierList.notifyDataSetChanged();
                                    Toast.makeText(HomeActivity.this, "Tier list deleted successfully", Toast.LENGTH_SHORT).show();
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
        startActivity(intent);
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
            finish();
        }

        return super.onContextItemSelected(item);
    }

    private void alert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
