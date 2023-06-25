package com.example.tier_list_app.activities.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tier_list_app.R;
import com.example.tier_list_app.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpUserActivity extends AppCompatActivity {

    private EditText edtNome;
    private EditText edtEmail;
    private EditText edtUsername;
    private EditText edtSenha;
    private EditText edtConfSenha;
    private Button btnSalvar;
    private User user;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        edtNome = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtUsername = findViewById(R.id.edtUsername);
        edtSenha = findViewById(R.id.edtSenha);
        edtConfSenha = findViewById(R.id.edtConfSenha);
        btnSalvar = findViewById(R.id.btnSalvar);

        Intent intent = getIntent();
        User altUser = (User) intent.getSerializableExtra("chave_user");

        user = new User();
        if (altUser != null) {
            btnSalvar.setText("ALTERAR");
            populateUserData(altUser);
            user.setId(altUser.getId());
        } else {
            btnSalvar.setText("SALVAR");
        }

        firestore = FirebaseFirestore.getInstance();
    }

    private void populateUserData(User user) {
        edtNome.setText(user.getName());
        edtEmail.setText(user.getEmail());
        edtUsername.setText(user.getUsername());
        edtSenha.setText(user.getPassword());
        edtConfSenha.setText(user.getPassword());
    }

    public void cadastrar(View view) {
        String name = edtNome.getText().toString();
        String email = edtEmail.getText().toString();
        String username = edtUsername.getText().toString();
        String password = edtSenha.getText().toString();
        String confirmPassword = edtConfSenha.getText().toString();

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Senha diferente da confirmação de senha!", Toast.LENGTH_SHORT).show();
            edtSenha.setText("");
            edtConfSenha.setText("");
            return;
        }

        user.setName(name);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        FirebaseFirestore.setLoggingEnabled(true);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (firebaseUser != null) {
                                user.setId(firebaseUser.getUid());
                                firestore.collection("users")
                                        .document(username)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SignUpUserActivity.this, "Usuário adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                                                limpar();
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignUpUserActivity.this, "Erro ao adicionar usuário.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SignUpUserActivity.this, "Erro ao criar usuário.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void limpar() {
        edtNome.setText("");
        edtEmail.setText("");
        edtUsername.setText("");
        edtSenha.setText("");
        edtConfSenha.setText("");
    }

    public void cancelar(View view) {
        finish();
    }
}
