package com.example.socialapps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialapps.StartupPage.NavigationActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView email = findViewById(R.id.email);
        final TextView name = findViewById(R.id.name);
        final Button button = findViewById(R.id.logout);
        final ImageView imageView = findViewById(R.id.imageMain);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        //get details for sign in user
        final String getName = googleSignInAccount.getDisplayName();
        final String getEmail = googleSignInAccount.getEmail();
        final Uri getImage = googleSignInAccount.getPhotoUrl();

        email.setText("email: "+getEmail);
        name.setText("name: "+getName);
        imageView.setImageURI(getImage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //sign out
                googleSignInClient.signOut();
                startActivity(new Intent(MainActivity.this, NavigationActivity.class));
                finish();
            }
        });
    }
}