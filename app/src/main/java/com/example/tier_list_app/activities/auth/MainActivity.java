package com.example.tier_list_app.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tier_list_app.R;
import com.example.tier_list_app.activities.auth.SignUpUserActivity;
import com.example.tier_list_app.activities.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private EditText edtUsuario;
    private EditText edtSenha;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        edtUsuario = findViewById(R.id.edtUsuario);
        edtSenha = findViewById(R.id.edtSenha);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void conectar(View view) {
        String usr = edtUsuario.getText().toString();
        String senha = edtSenha.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(usr, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra("chave_usuario", user.getEmail());
                            startActivity(intent);
                        }
                    } else {
            String errorMessage = task.getException().getMessage();
            Toast.makeText(MainActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
        }

    });
    }

    public void cadastrar(View view) {
        Intent it = new Intent(this, SignUpUserActivity.class);
        startActivity(it);
    }
}
