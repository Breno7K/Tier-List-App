package com.example.tier_list_app;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tier_list_app.database.DBHelper;
import com.example.tier_list_app.model.User;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);
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
        }
    }
    public void cadastrar(View view) {
        String name = edtNome.getText().toString();
        String email = edtEmail.getText().toString();
        String usuario = edtUsername.getText().toString();
        String senha = edtSenha.getText().toString();
        String confSenha = edtConfSenha.getText().toString();
        if(!senha.equals(confSenha)){
            Toast toast = Toast.makeText(SignUpUserActivity.this,
                    "Senha diferente da confirmação de senha!",
                    Toast.LENGTH_SHORT);
            toast.show();
            edtSenha.setText("");
            edtConfSenha.setText("");
        }
        else{
            User.setName(name);
            User.setEmail(email);
            User.setUsername(usuario);
            User.setPassword(senha);
            if(btnSalvar.getText().toString().equals("SALVAR")) {
                helper.insereUser(User);
                Toast toast = Toast.makeText(SignUpUserActivity.this,
                        "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT);
                toast.show();
            }else{
                helper.atualizarUser(User);
                helper.close();
            }
            limpar();
            finish();
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