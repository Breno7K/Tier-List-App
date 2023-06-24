package com.example.tier_list_app.activities.tier_list;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tier_list_app.R;
import com.example.tier_list_app.activities.home.HomeActivity;
import com.example.tier_list_app.model.TierList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistryTierListActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_TIER_LIST = 1;
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

        String email = getIntent().getStringExtra("chave_usuario");

        String tier_list_name = getIntent().getStringExtra("tier_list_name");
        edtName = findViewById(R.id.edtName);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnCancel = findViewById(R.id.btnCancelar);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTierList(email);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String tierListId = getIntent().getStringExtra("chave_tier_list_id");
        if (tierListId != null) {
            btnSalvar.setText("ALTERAR");
            firestore.collection("tier_lists").document(String.valueOf(tierListId)).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            TierList existingTierList = documentSnapshot.toObject(TierList.class);
                            if (existingTierList != null) {
                                tierList.setId(existingTierList.getId());
                                tierList.setUserEmail(existingTierList.getUserEmail());
                                tierList.setName(existingTierList.getName());
                                edtName.setText(existingTierList.getName());
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RegistryTierListActivity.this, "Failed to fetch existing tier list", Toast.LENGTH_SHORT).show();
                    });
        } else {
            btnSalvar.setText("CADASTRAR");
            String tierListName = getIntent().getStringExtra("tier_list_name");
            if (tierListName != null) {
                edtName.setText(tierListName);
            }
        }
    }


    private void saveTierList(String userEmail) {
        String tierListNameInput = edtName.getText().toString();
        tierList.setUserEmail(userEmail);
        tierList.setName(tierListNameInput);

        String tierListId = getIntent().getStringExtra("chave_tier_list_id");
        if (tierListId != null) {
            firestore.collection("tier_lists")
                    .document(tierListId)
                    .update("name", tierList.getName())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegistryTierListActivity.this, "Tier List updated successfully", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistryTierListActivity.this, "Failed to update Tier List", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            firestore.collection("tier_lists")
                    .add(tierList)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String newTierListId = documentReference.getId();
                            tierList.setId(newTierListId);

                            documentReference.update("id", newTierListId)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegistryTierListActivity.this, "Tier List created successfully", Toast.LENGTH_SHORT).show();
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegistryTierListActivity.this, "Failed to create Tier List", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistryTierListActivity.this, "Failed to create Tier List", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TIER_LIST && resultCode == RESULT_OK) {
            fetchTierListData();
        }
    }

    private void fetchTierListData() {
        String tierListId = getIntent().getStringExtra("chave_tier_list_id");
        if (tierListId != null) {
            firestore.collection("tier_lists").document(tierListId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            TierList fetchedTierList = documentSnapshot.toObject(TierList.class);
                            if (fetchedTierList != null) {
                                tierList.setId(fetchedTierList.getId());
                                tierList.setUserEmail(fetchedTierList.getUserEmail());
                                tierList.setName(fetchedTierList.getName());
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RegistryTierListActivity.this, "Failed to fetch tier list data", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateTierListInHomeActivity(TierList updatedTierList) {
        Intent intent = new Intent();
        intent.putExtra("updated_tier_list", updatedTierList);
        setResult(RESULT_OK, intent);
        finish();
    }
}