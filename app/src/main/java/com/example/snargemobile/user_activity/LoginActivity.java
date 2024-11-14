package com.example.snargemobile.user_activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.snargemobile.MainActivity;
import com.example.snargemobile.R;
import com.example.snargemobile.dao.AppDatabase;
import com.example.snargemobile.dao.UserDao;
import com.example.snargemobile.entity.User;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signUpTextView, forgotPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpTextView = findViewById(R.id.signUpTextView);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView); // Initialize the Forgot Password TextView

        loginButton.setOnClickListener(view -> loginUser());

        // Set up the "Sign Up" clickable text
        setSignUpClickableText();

        // Set up the "Forgot Password" clickable text
        forgotPasswordTextView.setOnClickListener(view -> {
            // Navigate to ForgotPasswordActivity
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void setSignUpClickableText() {
        String text = "Don’t have an account? Sign Up";
        SpannableString spannableString = new SpannableString(text);

        // Define a ClickableSpan for the "Sign Up" part
        ClickableSpan signUpClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Navigate to RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        };

        // Apply the clickable span to the "Sign Up" portion of the text
        int signUpStartIndex = text.indexOf("Sign Up");
        int signUpEndIndex = signUpStartIndex + "Sign Up".length();
        spannableString.setSpan(signUpClickableSpan, signUpStartIndex, signUpEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the spannable string on the TextView and make links clickable
        signUpTextView.setText(spannableString);
        signUpTextView.setMovementMethod(LinkMovementMethod.getInstance());
        signUpTextView.setHighlightColor(getResources().getColor(android.R.color.transparent)); // Remove link highlight color
    }

    private void loginUser() {
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        // Validate email field
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return;
        }

        // Validate password field
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        // Initialize the Room database
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "user_database").build();

        // Authenticate the user
        new Thread(() -> {
            UserDao userDao = db.userDao();
            User user = userDao.getUserByEmail(email); // Query to find user by email

            if (user != null && BCrypt.checkpw(password, user.password)) {
                // Password matches
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    // Navigate to the next screen, e.g., HomeActivity
                    // save current user to prefs
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", user.email);
                    editor.putString("firstName", user.firstName);
                    editor.putString("lastName", user.lastName);
                    editor.putString("phone", user.phone);
                    editor.putString("role", user.role);
                    editor.apply();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class); // Replace with the target activity
                    startActivity(intent);
                    finish();
                });
            } else {
                // Email not found or password does not match
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
