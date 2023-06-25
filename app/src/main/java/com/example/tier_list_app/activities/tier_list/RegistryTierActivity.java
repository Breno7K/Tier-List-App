package com.example.tier_list_app.activities.tier_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tier_list_app.R;
import com.example.tier_list_app.model.Tier;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class RegistryTierActivity extends AppCompatActivity {

    private EditText edtName;
    private Button btnSalvar;
    private Button btnCancel;
    private FirebaseFirestore firestore;
    private int selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tier_level_registry);

        edtName = findViewById(R.id.edtName);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnCancel = findViewById(R.id.btnCancelar);

        Button btnPickColor = findViewById(R.id.btnPickColor);
        btnPickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        firestore = FirebaseFirestore.getInstance();

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTier();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void openColorPicker() {
        AmbilWarnaDialog colorPickerDialog = new AmbilWarnaDialog(this, selectedColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                selectedColor = color;
                View colorPreview = findViewById(R.id.colorPreview);
                colorPreview.setBackgroundColor(color);
            }
        });
        colorPickerDialog.show();
    }

    public void saveTier() {
        Bundle args = getIntent().getExtras();
        String tierListId = args.getString("chave_tier_list_id");

        String name = edtName.getText().toString().trim();
        Tier tier = new Tier();
        tier.setName(name);
        tier.setColor(String.format("#%06X", (0xFFFFFF & selectedColor)));

        insereTier(tierListId, tier);

        String username = getIntent().getStringExtra("chave_usuario");
        Toast.makeText(this, "Tier created successfully", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("chave_tier_list_id", tierListId);
        resultIntent.putExtra("chave_usuario", username);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void insereTier(String tierListId, Tier tier) {
        CollectionReference tiersCollection = firestore.collection("tier");
        DocumentReference tierDocument = tiersCollection.document();
        tier.setId(tierDocument.getId());
        tier.setTierlistId(tierListId);

        Map<String, Object> tierData = new HashMap<>();
        tierData.put("name", tier.getName());
        tierData.put("color", tier.getColor());
        tierData.put("tierListId",tier.getTierlistId());

        tierDocument.set(tierData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegistryTierActivity.this, "Tier created successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistryTierActivity.this, "Failed to create tier", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
