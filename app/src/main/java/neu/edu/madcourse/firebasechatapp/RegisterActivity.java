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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    // widgets
    EditText username;
    EditText password;
    EditText email;
    Button registerButton;

    // firebase
    FirebaseAuth auth;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username_edit_text_register);
        password = findViewById(R.id.password_edit_text_register);
        email = findViewById(R.id.register_email);

        auth = FirebaseAuth.getInstance();

        // register button
        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username_str = username.getText().toString();
                String email_str = email.getText().toString().trim();
                String password_str = password.getText().toString();

                if (username_str.length() == 0 || email_str.length() == 0 || password_str.length() == 0) {
                    Toast.makeText(RegisterActivity.this,
                            "Please fill your information", Toast.LENGTH_LONG);
                } else {
                    RegisterNow(username_str, email_str, password_str);
                }
            }
        });
    }

    private void RegisterNow(final String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            myRef = FirebaseDatabase.getInstance()
                                    .getReference("MyUsers").child(userId);

                            HashMap<String, String> userIdMap = new HashMap<>();
                            userIdMap.put("id", userId);
                            userIdMap.put("username", username);

                            // switch to main activity after registering successfully
                            myRef.setValue(userIdMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Invalid Password or Email", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}