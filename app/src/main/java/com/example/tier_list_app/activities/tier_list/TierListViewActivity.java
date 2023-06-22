package com.example.tier_list_app.activities.tier_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tier_list_app.R;
import com.example.tier_list_app.database.DBHelper;
import com.example.tier_list_app.model.Tier;
import com.example.tier_list_app.model.TierList;

import java.util.List;

public class TierListViewActivity extends AppCompatActivity {

    private RecyclerView tierListRecyclerView;
    private TierListAdapter tierListAdapter;
    private Button btnCreateTier;

    private DBHelper dbHelper;

    private TextView txtEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tier_list);
        dbHelper = new DBHelper(this);

        tierListRecyclerView = findViewById(R.id.tierListRecyclerView);
        btnCreateTier = findViewById(R.id.btnCadastrarTier);
        txtEmptyMessage = findViewById(R.id.txtEmptyMessage);

        String username = getIntent().getStringExtra("chave_usuario");
        int tierListId = getIntent().getIntExtra("chave_tier_list_id", -1);
        TierList tierlist = dbHelper.buscarTierList(tierListId);

            List<Tier> listOfTiers = dbHelper.buscarTiers(tierlist);

            if (!listOfTiers.isEmpty()) {
                tierListAdapter = new TierListAdapter(listOfTiers);
                tierListRecyclerView.setAdapter(tierListAdapter);
                txtEmptyMessage.setVisibility(View.GONE);
            } else {
                tierListRecyclerView.setVisibility(View.GONE);
                txtEmptyMessage.setVisibility(View.VISIBLE);
                txtEmptyMessage.setText("You still have no tiers here :C");
            }

        btnCreateTier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TierListViewActivity.this, RegistryTierActivity.class);
                intent.putExtra("chave_tier_list_id", tierListId);
                intent.putExtra("chave_usuario", username);
                startActivity(intent);
            }
        });



        btnCreateTier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TierListViewActivity.this, RegistryTierActivity.class);
                intent.putExtra("chave_tier_list_id", tierListId);
                intent.putExtra("chave_usuario", username);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int tierListId = extras.getInt("chave_tier_list_id");
            String username = extras.getString("chave_usuario");
            TierList tierList = dbHelper.buscarTierList(tierListId);
            List<Tier> updatedTierList = dbHelper.buscarTiers(tierList);

            if (!updatedTierList.isEmpty()) {
                tierListAdapter = new TierListAdapter(updatedTierList);
                tierListRecyclerView.setAdapter(tierListAdapter);
                txtEmptyMessage.setVisibility(View.GONE);
            } else {
                tierListRecyclerView.setVisibility(View.GONE);
                txtEmptyMessage.setVisibility(View.VISIBLE);
                txtEmptyMessage.setText("You still have no tiers here :C");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Refresh the data here
            onResume();
        }
    }




}
