package com.example.tier_list_app.activities.tier_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tier_list_app.R;
import com.example.tier_list_app.model.Tier;
import com.example.tier_list_app.model.TierList;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TierListViewActivity extends AppCompatActivity implements ItemListAdapter.OnAddItemClickListener {

    private RecyclerView tierListRecyclerView;
    private TierListAdapter tierListAdapter;
    private Button btnCreateTier;


    private TextView txtEmptyMessage;

    private FirebaseFirestore firestore;
    private String tierListId;
    private TierList tierlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tier_list_view);

        tierListRecyclerView = findViewById(R.id.tierListRecyclerView);
        btnCreateTier = findViewById(R.id.btnCadastrarTier);
        txtEmptyMessage = findViewById(R.id.txtEmptyMessage);

        firestore = FirebaseFirestore.getInstance(); // Initialize the firestore variable

        String username = getIntent().getStringExtra("chave_usuario");
        tierListId = getIntent().getStringExtra("chave_tier_list_id");

        btnCreateTier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TierListViewActivity.this, RegistryTierActivity.class);
                intent.putExtra("chave_tier_list_id", tierListId);
                intent.putExtra("chave_usuario", username);
                startActivity(intent);
            }
        });

        updateTierListUI();
    }


    private interface OnTierListLoadedListener {
        void onTierListLoaded(TierList tierList);
    }

    private void buscarTierList(String tierListId, OnTierListLoadedListener listener) {
        firestore.collection("tier_lists")
                .document(tierListId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            String username = document.getString("username");
                            ArrayList<Tier> tiers = (ArrayList<Tier>) document.get("tiers");

                            TierList tierList = new TierList();
                            tierList.setId(tierListId);
                            tierList.setName(name);
                            tierList.setUsername(username);
                            tierList.setTiers(tiers);

                            listener.onTierListLoaded(tierList);
                        } else {
                            listener.onTierListLoaded(null);
                            Toast.makeText(TierListViewActivity.this, "Tier list not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        listener.onTierListLoaded(null);
                        Toast.makeText(TierListViewActivity.this, "Error retrieving tier list", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void buscarTiers(TierList tierList, OnTiersLoadedListener listener) {
        String tierListId = tierList.getId();

        firestore.collection("tier")
                .whereEqualTo("tierListId", tierListId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Tier> tiers = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String tierId = document.getId();
                            String tierName = document.getString("name");
                            String tierColor = document.getString("color");

                            Tier tier = new Tier();
                            tier.setId(tierId);
                            tier.setName(tierName);
                            tier.setColor(tierColor);

                            tiers.add(tier);
                        }

                        listener.onTiersLoaded(tiers);
                    } else {
                        Toast.makeText(TierListViewActivity.this, "Error retrieving tiers", Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private interface OnTiersLoadedListener {
        void onTiersLoaded(List<Tier> tiers);
    }

    private void updateTierListUI() {
        if (tierlist != null) {
            buscarTiers(tierlist, new OnTiersLoadedListener() {
                @Override
                public void onTiersLoaded(List<Tier> tiers) {
                    if (!tiers.isEmpty()) {
                        tierListAdapter = new TierListAdapter(tiers, TierListViewActivity.this); // Add the missing onAddItemClickListener argument
                        tierListRecyclerView.setLayoutManager(new LinearLayoutManager(TierListViewActivity.this));
                        tierListRecyclerView.setAdapter(tierListAdapter);
                        txtEmptyMessage.setVisibility(View.GONE);
                    } else {
                        tierListRecyclerView.setVisibility(View.GONE);
                        txtEmptyMessage.setVisibility(View.VISIBLE);
                        txtEmptyMessage.setText("You still have no tiers here :C");
                    }
                }
            });
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tierListId = extras.getString("chave_tier_list_id"); // Update tierListId
            String username = extras.getString("chave_usuario"); // Update username

            buscarTierList(tierListId, new OnTierListLoadedListener() {
                @Override
                public void onTierListLoaded(TierList tierList) {
                    tierlist = tierList;
                    updateTierListUI();
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Update lista
            onResume();
        }
    }

    @Override
    public void onAddItemClick(Context context) {
        Intent intent = new Intent(TierListViewActivity.this, RegistryItemActivity.class);
        startActivity(intent);
    }
}
