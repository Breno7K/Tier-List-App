package com.example.tier_list_app.activities.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tier_list_app.R;
import com.example.tier_list_app.activities.home.HomeActivity;
import com.example.tier_list_app.database.DBHelper;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DBHelper helper = new DBHelper(this);
    private EditText edtUsuario;
    private EditText edtSenha;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        edtUsuario=findViewById(R.id.edtUsuario);
        edtSenha=findViewById(R.id.edtSenha);
        firestore = FirebaseFirestore.getInstance();

    }
    public void conectar(View view) {
        String usr=edtUsuario.getText().toString();
        String senha = edtSenha.getText().toString();

        firestore.collection("users")
                .whereEqualTo("username", usr)
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
                                            String password = documentTask.getResult().getString("password");
                                            if (senha.equals(password)) {
                                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                                intent.putExtra("chave_usuario", usr);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(MainActivity.this, "Senha inválida", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(MainActivity.this, "Erro ao buscar usuário", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Erro ao buscar usuário", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void cadastrar(View view) {

        Intent it = new Intent(this, SignUpUserActivity.class);
        startActivity(it);

    }
}