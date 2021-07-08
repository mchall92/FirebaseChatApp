package neu.edu.madcourse.firebasechatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    // widgets
    private EditText emailLogin;
    private EditText passwordLogin;
    private Button loginButton;
    private Button toRegisterButton;

    // Firebase authentication
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin = findViewById(R.id.email_edit_text_login);
        passwordLogin = findViewById(R.id.password_edit_text_login);

        // firebase user
        auth = FirebaseAuth.getInstance();

        // login button
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailLoginStr = emailLogin.getText().toString();
                String passwordLoginStr= passwordLogin.getText().toString();

                if (emailLoginStr.length() == 0 || passwordLoginStr.length() == 0) {
                    Toast.makeText(LoginActivity.this,
                            "Please fill your information", Toast.LENGTH_LONG).show();
                } else {
                    auth.signInWithEmailAndPassword(emailLoginStr, passwordLoginStr)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this,
                                                "Login Failed", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        // to register button
        toRegisterButton = findViewById(R.id.to_register_activity_button);
        toRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRegisterIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(toRegisterIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // check user existence by using current saved user
        if (firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}