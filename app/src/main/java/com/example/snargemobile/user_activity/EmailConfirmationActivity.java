package com.example.snargemobile.user_activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.snargemobile.R;
import com.example.snargemobile.dao.AppDatabase;
import com.example.snargemobile.dao.UserDao;


public class EmailConfirmationActivity extends AppCompatActivity {
    private EditText confirmationCodeEditText;
    private Button confirmButton;
    private String email;
    private String expectedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_confirmation);

        email = getIntent().getStringExtra("email");
        expectedCode = getIntent().getStringExtra("confirmationCode");

        confirmationCodeEditText = findViewById(R.id.confirmationCodeEditText);
        confirmButton = findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(v -> verifyCode());
    }

    private void verifyCode() {
        String enteredCode = confirmationCodeEditText.getText().toString().trim();

        if (enteredCode.equals(expectedCode)) {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "user_database").build();

            new Thread(() -> {
                UserDao userDao = db.userDao();
                userDao.confirmUser(email);

                runOnUiThread(() -> {
                    Toast.makeText(EmailConfirmationActivity.this, "Email confirmed successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EmailConfirmationActivity.this, LoginActivity.class));
                    finish();
                });
            }).start();
        } else {
            //1
            Toast.makeText(this, "Invalid confirmation code", Toast.LENGTH_SHORT).show();
        }
    }
}

