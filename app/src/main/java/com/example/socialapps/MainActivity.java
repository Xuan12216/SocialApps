package com.example.socialapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialapps.LoginAndRegister.LoginRegisterActivity;
import com.example.socialapps.LoginAndRegister.users;
import com.example.socialapps.StartupPage.NavigationActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView email = findViewById(R.id.email);
        final TextView name = findViewById(R.id.name);
        final Button button = findViewById(R.id.logout);
        final ImageView imageView = findViewById(R.id.imageMain);

        //取得目前的Firebase帳號
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //取得Database的根目錄
            reference = FirebaseDatabase.getInstance().getReference("users");
            //取得User的UID
            userID = user.getUid();

            //讀取Firebase Database的資料
            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    users userDetail = snapshot.getValue(users.class);
                    email.setText("email: "+userDetail.getUserEmail());
                    name.setText("name: "+userDetail.getUserName());
                    Picasso.get().load(userDetail.getProfilePic()).into(imageView);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(),"Something wrong !",Toast.LENGTH_LONG).show();
                }
            });
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Firebase sign out
                FirebaseAuth.getInstance().signOut();
                //Google
                //讀取目前登陸的google帳號
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getApplicationContext(),googleSignInOptions);
                googleSignInClient.signOut();
                Toast.makeText(MainActivity.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginRegisterActivity.class));
                finish();
            }
        });
    }
}