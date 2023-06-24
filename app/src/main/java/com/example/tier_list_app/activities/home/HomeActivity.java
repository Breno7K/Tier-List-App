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
import com.example.tier_list_app.model.Tier;
import com.example.tier_list_app.model.TierList;
import com.example.tier_list_app.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_TIER_LIST = 1;
    private static final int REQUEST_CODE_EDIT_TIER_LIST = 1;


    private TextView txtNome;
    private ListView listTierLists;
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

        if (requestCode == REQUEST_CODE_TIER_LIST || requestCode == REQUEST_CODE_EDIT_TIER_LIST) {
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
        if (args != null) {
            String name = args.getString("chave_usuario");
            fillList(name);
        } else {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void buscarTierLists(String username, OnTierListsLoadedListener listener) {
        firestore.collection("tier_lists")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<TierList> tierLists = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String name = document.getString("name");
                            ArrayList<Tier> tiers = (ArrayList<Tier>) document.get("tiers");

                            TierList tierList = new TierList();
                            tierList.setId(id);
                            tierList.setUsername(username);
                            tierList.setName(name);
                            tierList.setTiers(tiers);

                            tierLists.add(tierList);
                        }
                        listener.onTierListsLoaded(tierLists);
                    } else {
                        Toast.makeText(HomeActivity.this, "Failed to fetch tier lists.", Toast.LENGTH_SHORT).show();
                        listener.onTierListsLoaded(null);
                    }
                });
    }


    interface OnTierListsLoadedListener {
        void onTierListsLoaded(ArrayList<TierList> tierLists);
    }

    private void buscarUser(String username, OnUserLoadedListener listener) {
        firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean userExists = !task.getResult().isEmpty();
                        if (userExists) {
                            String documentId = task.getResult().getDocuments().get(0).getId();
                            firestore.collection("users")
                                    .document(documentId)
                                    .get()
                                    .addOnCompleteListener(documentTask -> {
                                        if (documentTask.isSuccessful()) {
                                            String name = documentTask.getResult().getString("name");
                                            String email = documentTask.getResult().getString("email");
                                            String password = documentTask.getResult().getString("password");
                                            User user = new User();
                                            user.setUsername(username);
                                            user.setName(name);
                                            user.setEmail(email);
                                            user.setPassword(password);
                                            listener.onUserLoaded(user);
                                        } else {
                                            Toast.makeText(HomeActivity.this, "Error fetching user", Toast.LENGTH_SHORT).show();
                                            listener.onUserLoaded(null);
                                        }
                                    });
                        } else {
                            Toast.makeText(HomeActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            listener.onUserLoaded(null);
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Error fetching user", Toast.LENGTH_SHORT).show();
                        listener.onUserLoaded(null);
                    }
                });
    }

    interface OnUserLoadedListener {
        void onUserLoaded(User user);
    }

    public void fillList(String name) {
        buscarUser(name, new OnUserLoadedListener() {
            @Override
            public void onUserLoaded(User user) {
                if (user != null) {
                    buscarTierLists(user.getUsername(), new OnTierListsLoadedListener() {
                        @Override
                        public void onTierListsLoaded(ArrayList<TierList> tierLists) {
                            if (tierLists != null) {
                                arrayListTierList = tierLists;
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
                                                    startActivityForResult(intent, REQUEST_CODE_TIER_LIST); // Use startActivityForResult instead of startActivity
                                                }
                                            });

                                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    TierList selectedTierList = arrayListTierList.get(position);
                                                    excluirTierList(selectedTierList);
                                                }
                                            });

                                            return convertView;
                                        }
                                    };

                                    listTierLists.setAdapter(arrayAdapterTierList);
                                }
                            }
                        }
                    });
                } else {
                    System.out.println("Erro");
                }
            }
        });
    }

    public void excluirTierList(TierList tierList) {
        String tierListId = tierList.getId();

        firestore.collection("tier_lists")
                .document(String.valueOf(tierListId))
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayListTierList.remove(tierList);
                        arrayAdapterTierList.notifyDataSetChanged();

                        Toast.makeText(HomeActivity.this, "Tier list deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HomeActivity.this, "Failed to delete tier list", Toast.LENGTH_SHORT).show();
                    }
                });
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
            excluirTierList(selectedTierList);
        } else if (item.getTitle().equals("Cancel")) {
            finish();
        }

        return super.onContextItemSelected(item);
    }

    private void alert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
