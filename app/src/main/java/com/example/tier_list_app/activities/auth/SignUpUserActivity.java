package com.example.tier_list_app.activities.auth;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tier_list_app.R;
import com.example.tier_list_app.database.DBHelper;
import com.example.tier_list_app.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class SignUpUserActivity extends AppCompatActivity {

    DBHelper helper = new DBHelper(this);
    private EditText edtNome;
    private EditText edtEmail;
    private EditText edtUsername;
    private EditText edtSenha;
    private EditText edtConfSenha;
    private Button btnSalvar;
    private User User;
    private User altUser;
    private FirebaseFirestore firebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sign_up);
        edtNome = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtUsername = findViewById(R.id.edtUsername);
        edtSenha = findViewById(R.id.edtSenha);
        edtConfSenha = findViewById(R.id.edtConfSenha);
        btnSalvar=findViewById(R.id.btnSalvar);
        Intent it=getIntent();

        altUser = (User) it.getSerializableExtra("chave_user");

        User = new User();
        if(altUser != null){
            btnSalvar.setText("ALTERAR");
            edtNome.setText(altUser.getName());
            edtEmail.setText(altUser.getEmail());
            edtUsername.setText(altUser.getUsername());
            edtSenha.setText(altUser.getPassword());
            edtConfSenha.setText(altUser.getPassword());
            User.setId(altUser.getId());
        }else{
            btnSalvar.setText("SALVAR");

            firebase = FirebaseFirestore.getInstance();

        }
    }
    public void cadastrar(View view) {
        String name = edtNome.getText().toString();
        String email = edtEmail.getText().toString();
        String usuario = edtUsername.getText().toString();
        String senha = edtSenha.getText().toString();
        String confSenha = edtConfSenha.getText().toString();

        if (!senha.equals(confSenha)) {
            Toast toast = Toast.makeText(SignUpUserActivity.this,
                    "Senha diferente da confirmação de senha!",
                    Toast.LENGTH_SHORT);
            toast.show();
            edtSenha.setText("");
            edtConfSenha.setText("");
        } else {
            User.setName(name);
            User.setEmail(email);
            User.setUsername(usuario);
            User.setPassword(senha);

            // Verificar se o username já está em uso
            firebase.collection("users")
                    .whereEqualTo("username", usuario)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // O username já está em uso
                                Toast.makeText(SignUpUserActivity.this, "Username já está em uso. Escolha outro.", Toast.LENGTH_SHORT).show();
                            } else {
                                // O username é único, adicionar o usuário
                                helper.insereUser(User);
                                firebase.collection("users").document(usuario).set(User)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(SignUpUserActivity.this, "Usuário adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                                            limpar();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(SignUpUserActivity.this, "Erro ao adicionar usuário.", Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            // Ocorreu um erro ao acessar o banco de dados
                            Toast.makeText(SignUpUserActivity.this, "Erro ao verificar username.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    public void limpar(){
        edtNome.setText(""); edtEmail.setText("");
        edtUsername.setText(""); edtSenha.setText("");
        edtConfSenha.setText("");
    }
    public void cancelar(View view) {
        finish();
    }
}