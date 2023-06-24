package com.example.tier_list_app.activities.tier_list;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tier_list_app.R;
import com.example.tier_list_app.activities.home.HomeActivity;
import com.example.tier_list_app.database.DBHelper;
import com.example.tier_list_app.model.TierList;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistryTierListActivity extends AppCompatActivity {

    private EditText edtName;
    private Button btnSalvar;
    private Button btnCancel;

    private TierList tierList;


    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tier_list_registry);

        tierList = new TierList();

        firestore = FirebaseFirestore.getInstance();

        String username = getIntent().getStringExtra("chave_usuario");

        String tier_list_name = getIntent().getStringExtra("tier_list_name");
        edtName = findViewById(R.id.edtName);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnCancel = findViewById(R.id.btnCancelar);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTierList(username);
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

    private void saveTierList(String userName){
        String tierListNameInput = edtName.getText().toString();
        Log.d("Tag", "tierListName = "+ tierListNameInput+ " Linha 53");
        tierList.setUsername(userName);
        tierList.setName(tierListNameInput);
        String id;
        id = userName+tierListNameInput;
        firestore.collection("tier_list")
                .whereEqualTo("name", tierListNameInput)
                .whereEqualTo("user", userName)
                .get()
                .addOnCompleteListener(task->{
                    if(task.getResult().isEmpty()){

                        firestore.collection("tier_lists").document(id).set(tierList)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegistryTierListActivity.this, "Tier List adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                                    updateTierListInHomeActivity(tierList);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegistryTierListActivity.this, "Erro ao adicionar tier list.", Toast.LENGTH_SHORT).show();
                                });
                        Intent intent = new Intent(RegistryTierListActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(RegistryTierListActivity.this, "Esse nome já está em uso.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateTierListInHomeActivity(TierList updatedTierList) {
        Intent intent = new Intent();
        intent.putExtra("updated_tier_list", updatedTierList);
        setResult(RESULT_OK, intent);
        finish();
    }
}
