package com.example.tier_list_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tier_list_app.database.DBHelper;
import com.example.tier_list_app.model.TierList;
import com.example.tier_list_app.model.User;

public class RegistryTierListActivity extends AppCompatActivity {

    private EditText edtName;
    private Button btnSalvar;
    private Button btnCancel;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registy_tier_list);

        dbHelper = new DBHelper(this);

        edtName = findViewById(R.id.edtName);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnCancel = findViewById(R.id.btnCancelar);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTierList();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int tierListId = getIntent().getIntExtra("chave_tier_list_id", -1);
        if (tierListId != -1) {
            btnSalvar.setText("ALTERAR");
        } else {
            btnSalvar.setText("CADASTRAR");
        }
    }

    private void saveTierList() {
        String name = edtName.getText().toString().trim();
        String username = getIntent().getStringExtra("chave_usuario");
        int tierListId = getIntent().getIntExtra("chave_tier_list_id", -1);

        User user = dbHelper.buscarUser(username);
        if (user != null) {
            if (tierListId != -1 && btnSalvar.getText().toString().equals("ALTERAR")) {
                TierList tierList = new TierList();
                tierList.setId(tierListId);
                tierList.setName(name);
                tierList.setUsername(username);

                long result = dbHelper.atualizarTierList(tierList);

                if (result != -1) {
                   Toast.makeText(this, "Tier list updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update tier list", Toast.LENGTH_SHORT).show();
                }
            } else {
                TierList tierList = new TierList();
                tierList.setName(name);
                dbHelper.insereTierList(user, tierList);

                Toast.makeText(this, "Tier list saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Failed to save tier list. User not found: " + username, Toast.LENGTH_SHORT).show();
        }
    }

}
