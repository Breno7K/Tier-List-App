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
        String password = helper.buscarSenha(usr);


        if(senha.equals(password)){
            Intent intent= new Intent(this, HomeActivity.class);
            intent.putExtra("chave_usuario", usr);
            startActivity(intent);
        }
        else{
            Toast toast = Toast.makeText(MainActivity.this,
                    "Usuário ou senha inválido",Toast.LENGTH_LONG);
            toast.show();
        }
    }
    public void cadastrar(View view) {

        Intent it = new Intent(this, SignUpUserActivity.class);
        startActivity(it);

    }
}