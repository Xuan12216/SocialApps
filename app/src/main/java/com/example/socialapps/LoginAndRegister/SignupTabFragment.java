package com.example.socialapps.LoginAndRegister;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.socialapps.MainActivity;
import com.example.socialapps.R;
import com.example.socialapps.bottomMenu.bottom_menu;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignupTabFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private TextInputLayout layoutPassword_up,layoutConfirm_up,layoutEmail_up;
    private TextInputEditText up_password,up_confirm,up_email,up_name;
    private Button up_button;
    private ProgressBar progressBar;
    private boolean isPasswordMatch = false;
    private boolean isPasswordTrue = false;
    private boolean isEmailTrue = false;
    private boolean isNameTrue = false;
    private String picture = "https://cdn0.iconfinder.com/data/icons/users-android-l-lollipop-icon-pack/24/user-128.png";
    private FirebaseAuth myAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup_tab, container, false);

        final ImageView autoSignIn_in = view.findViewById(R.id.autoSignIn_up);
        layoutPassword_up = view.findViewById(R.id.layoutPassword_sign_up);
        up_password = view.findViewById(R.id.signup_password);
        layoutConfirm_up = view.findViewById(R.id.layoutConfirm_sign_up);
        up_confirm = view.findViewById(R.id.signup_confirm);
        layoutEmail_up = view.findViewById(R.id.layoutEmail_sign_up);
        up_email = view.findViewById(R.id.signup_email);
        up_name = view.findViewById(R.id.signup_name);
        up_button = view.findViewById(R.id.signup_button);
        progressBar = view.findViewById(R.id.progressBar_up);
        myAuth = FirebaseAuth.getInstance();

        //checking if user already signed in
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(getContext(), bottom_menu.class));
            getActivity().finish();
        }

        //=====================================Below is Button Code=========================================
        up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if (!up_name.getText().toString().isEmpty()){
                    isNameTrue = true;
                }
                else {
                    isNameTrue = false;
                }

                if (!up_email.getText().toString().isEmpty() && up_password.getText().toString().matches(up_confirm.getText().toString())
                        && isNameTrue && isEmailTrue && isPasswordTrue && isPasswordMatch){
                    String email = up_email.getText().toString();
                    String password = up_password.getText().toString();
                    String name = up_name.getText().toString();

                    myAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task ->
                    {
                        if (task.isSuccessful())
                        {
                            users user = new users(FirebaseAuth.getInstance().getCurrentUser().getUid(),name,picture,"",email);

                            FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(task1 ->
                            {
                                if (task1.isSuccessful())
                                {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(),"Sign Up successfully",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getContext(), bottom_menu.class));
                                    getActivity().finish();
                                }
                                else
                                {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(),"Failed Try again",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(),"Failed email exists or format wrong",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(),"Format Wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //=====================================Above is Button Code=========================================
        //=====================================Below is Check Email Code=========================================
        up_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = charSequence.toString();
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    layoutEmail_up.setHelperText("Format True");
                    layoutEmail_up.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.green,null)));
                    isEmailTrue=true;
                }
                else {
                    layoutEmail_up.setHelperText("Format False");
                    layoutEmail_up.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red,null)));

                    isEmailTrue=false;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        //=====================================Above is Check Email Code=========================================
        //=====================================Below is Check Password Code=========================================
        up_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = charSequence.toString();
                layoutPassword_up.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.green,null)));
                if (password.length() >= 8){
                    if (password.length()>20){
                        layoutPassword_up.setHelperText("");
                        layoutPassword_up.setError("Maximum 20 char");
                        isPasswordTrue=false;
                    }
                    else {
                        layoutPassword_up.setHelperText("Strong Password");
                        layoutPassword_up.setError("");
                        isPasswordTrue=true;
                        if (!up_confirm.getText().toString().isEmpty() && up_confirm.getText().toString().matches(password)){
                            layoutConfirm_up.setHelperText("Password Match");
                            layoutConfirm_up.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.green,null)));
                            layoutConfirm_up.setError("");
                            isPasswordMatch=true;
                        }
                        else {
                            layoutConfirm_up.setHelperText("Password did not match");
                            layoutConfirm_up.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red,null)));
                            layoutConfirm_up.setError("");
                            isPasswordMatch=false;
                        }
                    }
                }
                else {
                    layoutPassword_up.setHelperText("Enter Minimum 8 char");
                    layoutPassword_up.setError("");
                    isPasswordTrue=false;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        up_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String confirm_pass = charSequence.toString();

                if (isPasswordTrue){
                    if (confirm_pass.matches(up_password.getText().toString())){
                        layoutConfirm_up.setHelperText("Password Match");
                        layoutConfirm_up.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.green,null)));
                        layoutConfirm_up.setError("");
                        isPasswordMatch=true;
                    }
                    else {
                        layoutConfirm_up.setHelperText("Password did not match");
                        layoutConfirm_up.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red,null)));
                        layoutConfirm_up.setError("");
                        isPasswordMatch=false;
                    }
                }
                else {
                    layoutConfirm_up.setHelperText("");
                    layoutConfirm_up.setError("Wrong Password Format");
                    isPasswordMatch=false;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        //=====================================Above is Check Password Code=========================================
        //=====================================Below is GoogleLogin And Firebase Code=========================================
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getContext(),googleSignInOptions);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                //getting signed in account after user selected an account from google account dialog
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());

                try {
                    progressBar.setVisibility(View.VISIBLE);
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                    auth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if (task.isSuccessful()){
                                FirebaseUser user = auth.getCurrentUser();
                                assert user != null;
                                //user不為空就執行以下程式，把Name，UID，ProfilePic和Email儲存進user1的物件中
                                users users1 = new users(user.getUid(),user.getDisplayName(),user.getPhotoUrl().toString(),"","GoogleSignIn");
                                database.getReference().child("users").child(user.getUid()).setValue(users1);

                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(getContext(), bottom_menu.class));
                                getActivity().finish();
                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                catch (ApiException e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed or Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        autoSignIn_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(signInIntent);
            }
        });
        //=====================================Above is GoogleLogin And Firebase Code=========================================
        return view;
    }
}