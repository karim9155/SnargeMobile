package com.example.snargemobile.user_activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;


import com.example.snargemobile.R;
import com.example.snargemobile.dao.AppDatabase;
import com.example.snargemobile.dao.UserDao;
import com.example.snargemobile.entity.User;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText, firstNameEditText, lastNameEditText, phoneEditText;
    private Button registerButton;
    private TextView signInTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        signInTextView = findViewById(R.id.signInTextView);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        // Set up the "Sign In" clickable text
        setSignInClickableText();
    }

    private void setSignInClickableText() {
        String text = "Already have an account? Sign In";
        SpannableString spannableString = new SpannableString(text);

        // Define a ClickableSpan for the "Sign In" part
        ClickableSpan signInClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Navigate to LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        };

        // Apply the clickable span to the "Sign In" portion of the text
        int signInStartIndex = text.indexOf("Sign In");
        int signInEndIndex = signInStartIndex + "Sign In".length();
        spannableString.setSpan(signInClickableSpan, signInStartIndex, signInEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the spannable string on the TextView and make links clickable
        signInTextView.setText(spannableString);
        signInTextView.setMovementMethod(LinkMovementMethod.getInstance());
        signInTextView.setHighlightColor(getResources().getColor(android.R.color.transparent)); // Remove link highlight color
    }

    private void registerUser() {
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        final String firstName = firstNameEditText.getText().toString().trim();
        final String lastName = lastNameEditText.getText().toString().trim();
        final String phone = phoneEditText.getText().toString().trim();



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

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        // Validate first name field
        if (firstName.isEmpty()) {
            firstNameEditText.setError("First name is required");
            firstNameEditText.requestFocus();
            return;
        }

        // Validate last name field
        if (lastName.isEmpty()) {
            lastNameEditText.setError("Last name is required");
            lastNameEditText.requestFocus();
            return;
        }

        // Validate phone field
        if (phone.isEmpty()) {
            phoneEditText.setError("Phone number is required");
            phoneEditText.requestFocus();
            return;
        }

        if (phone.length() != 8) {
            phoneEditText.setError("Phone number must be 8 digits");
            phoneEditText.requestFocus();
            return;
        }

        // Hash the password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Generate a confirmation code
        String confirmationCode = generateConfirmationCode();

        // Initialize the Room database
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "user_database").build();

        // Check if the user already exists and register if not
        new Thread(() -> {
            UserDao userDao = db.userDao();
            int userExists = userDao.checkUserExists(email);

            if (userExists > 0) {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "User already exists", Toast.LENGTH_SHORT).show());
            } else {
                // Insert the user with the hashed password and confirmation code
                User user = new User(email, hashedPassword, confirmationCode, firstName, lastName, phone, "user");
                userDao.insert(user);

                // Send the confirmation email
                sendEmail(email, "Email Confirmation", "Please use the following confirmation code: " + confirmationCode);

                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Registration successful. Check your email for the confirmation code.", Toast.LENGTH_SHORT).show();

                    // Navigate to EmailConfirmationActivity
                    Intent confirmIntent = new Intent(RegisterActivity.this, EmailConfirmationActivity.class);
                    confirmIntent.putExtra("email", email);
                    confirmIntent.putExtra("confirmationCode", confirmationCode);
                    startActivity(confirmIntent);

                    finish();
                });
            }
        }).start();
    }

    private void sendEmail(String recipient, String subject, String body) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("firas.jery@gmail.com", "ysjo xjdt qmhw sqvp");
                }
            });

            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress("salimaloui406@gmail.com"));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);
                Log.i("RegisterActivity", "Email sent successfully to: " + recipient);
            } catch (MessagingException e) {
                Log.e("RegisterActivity", "Failed to send email to: " + recipient, e);
            } finally {
                executor.shutdown();
            }
        });
    }

    public String generateConfirmationCode() {
        // genertae a random 6 characters and numbers code
        String code = "";
        for (int i = 0; i < 6; i++) {
            int random = (int) (Math.random() * 10);
            code += random;
        }
        return code;
    }








}
