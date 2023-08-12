package com.example.socialapps.bottomMenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialapps.LoginAndRegister.LoginRegisterActivity;
import com.example.socialapps.LoginAndRegister.users;
import com.example.socialapps.MainActivity;
import com.example.socialapps.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
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

public class ProfileFragment extends Fragment {
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final TextView email = view.findViewById(R.id.emailPro);
        final TextView name = view.findViewById(R.id.namePro);
        final ImageButton settingButton=view.findViewById(R.id.settingButton);
        final ImageView imageView = view.findViewById(R.id.imageMainPro);

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
                    Toast.makeText(getContext(),"Something wrong !",Toast.LENGTH_LONG).show();
                }
            });
        }

        //打開編輯設定
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
            }
        });
        return view;
    }

    private void showBottomDialog() {

        final Dialog dialog;
        Context context = getContext();
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_setting_sheet);

        LinearLayout cUsername = dialog.findViewById(R.id.renameLayout);
        LinearLayout cPicture = dialog.findViewById(R.id.changePicture);
        LinearLayout cContent = dialog.findViewById(R.id.changeContentLayout);
        LinearLayout logoutLayout = dialog.findViewById(R.id.logout);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        cUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

       cPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        cContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //Firebase sign out
                FirebaseAuth.getInstance().signOut();
                //Google
                //讀取目前登陸的google帳號
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getContext(),googleSignInOptions);
                googleSignInClient.signOut();
                Toast.makeText(getContext(), "Successfully logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), LoginRegisterActivity.class));
                getActivity().finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}