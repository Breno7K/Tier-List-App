package com.example.tier_list_app.activities.tier_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tier_list_app.R;
import com.example.tier_list_app.database.DBHelper;
import com.example.tier_list_app.model.Tier;
import com.example.tier_list_app.model.TierList;

import yuku.ambilwarna.AmbilWarnaDialog;

public class RegistryTierActivity extends AppCompatActivity {

    private EditText edtName;
    private Button btnSalvar;
    private Button btnCancel;

    private DBHelper dbHelper;

    private int selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tier_level_registry);

        dbHelper = new DBHelper(this);
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



        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTier();
            }
        });

        Button btnCancel = findViewById(R.id.btnCancelar);
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
                // Handle color picker cancellation
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                selectedColor = color;
                // Do something with the selected color, such as setting it to a view
                // For example:
                View colorPreview = findViewById(R.id.colorPreview);
                colorPreview.setBackgroundColor(color);
            }
        });
        colorPickerDialog.show();
    }

    public void saveTier() {
        Bundle args = getIntent().getExtras();
        int tierListId = args.getInt("chave_tier_list_id", -1);
        TierList tierList = dbHelper.buscarTierList(tierListId);
        String name = edtName.getText().toString().trim();
        Tier tier = new Tier();
        tier.setName(name);
        dbHelper.insereTier(tierList, tier);
        String username = getIntent().getStringExtra("chave_usuario");
        Toast.makeText(this, "Tier created successfully", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("chave_tier_list_id", tierListId);
        resultIntent.putExtra("chave_usuario", username);
        setResult(RESULT_OK, resultIntent);
        finish();
    }



}
