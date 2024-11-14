package com.example.snargemobile.user_activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.snargemobile.R;
import com.example.snargemobile.dao.AppDatabase;
import com.example.snargemobile.dao.UserDao;
import com.example.snargemobile.entity.User;


public class ProfileActivity extends AppCompatActivity {
    private TextView profileName, profileSurname, profilePhone;
    private EditText editProfileName, editProfileSurname, editProfilePhone;
    private Button editButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        profileName = findViewById(R.id.profileName);
        profileSurname = findViewById(R.id.profileSurname);
        profilePhone = findViewById(R.id.profilePhone);

        editProfileName = findViewById(R.id.editProfileName);
        editProfileSurname = findViewById(R.id.editProfileSurname);
        editProfilePhone = findViewById(R.id.editProfilePhone);

        editButton = findViewById(R.id.editButton);
        saveButton = findViewById(R.id.saveButton);
        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("firstName", "");
        String lastName = sharedPreferences.getString("lastName", "");
        String phone = sharedPreferences.getString("phone", "");
        String email = sharedPreferences.getString("email", "");

        // Set the retrieved data to the TextViews
        profileName.setText("Name: " + firstName);
        profileSurname.setText("Surname: " + lastName);
        profilePhone.setText("Phone: " + phone);

        editButton.setOnClickListener(v -> toggleEditMode(true));

        saveButton.setOnClickListener(v -> {
            String updatedFirstName = editProfileName.getText().toString();
            String updatedLastName = editProfileSurname.getText().toString();
            String updatedPhone = editProfilePhone.getText().toString();

            profileName.setText("Name: " + updatedFirstName);
            profileSurname.setText("Surname: " + updatedLastName);
            profilePhone.setText("Phone: " + updatedPhone);

            // Save updated data to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("firstName", updatedFirstName);
            editor.putString("lastName", updatedLastName);
            editor.putString("phone", updatedPhone);
            editor.apply();

            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "user_database").build();
            new Thread(() -> {
                UserDao userDao = db.userDao();
                User user = userDao.getUserByEmail(email);
                if (user != null) {
                    userDao.updateUser(user.id,updatedFirstName,updatedLastName,updatedPhone);
                }
            }).start();

            toggleEditMode(false);
        });
    }

    private void toggleEditMode(boolean isEditing) {
        if (isEditing) {
            // Set the current values to the EditText fields
            editProfileName.setText(profileName.getText().toString().replace("Name: ", ""));
            editProfileSurname.setText(profileSurname.getText().toString().replace("Surname: ", ""));
            editProfilePhone.setText(profilePhone.getText().toString().replace("Phone: ", ""));
        }
        profileName.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        profileSurname.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        profilePhone.setVisibility(isEditing ? View.GONE : View.VISIBLE);

        editProfileName.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        editProfileSurname.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        editProfilePhone.setVisibility(isEditing ? View.VISIBLE : View.GONE);

        editButton.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(isEditing ? View.VISIBLE : View.GONE);
    }
}

