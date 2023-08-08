package com.example.socialapps.LoginAndRegister;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.socialapps.MainActivity;
import com.example.socialapps.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SigninTabFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signin_tab, container, false);

        //google登陸Button
        final ImageView autoSignIn_in = view.findViewById(R.id.autoSignIn_in);

        //Firebase Authentication 和 Firebase Realtime Database 功能的初始化設定。
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //用來配置Google登入選項（GoogleSignInOptions）的，用於實現使用 Google 帳號登入你的應用程式。
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //用來建立一個 GoogleSignInClient 物件，該物件允許你在你的應用程式中實現 Google 登入功能。
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getContext(),googleSignInOptions);

        //checking if user already signed in
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        }

        //使用 AndroidX Activity Result API 建立一個 ActivityResultLauncher 物件，用於處理 Google 登入的結果
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                //getting signed in account after user selected an account from google account dialog
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                handleSignInTask(task);
            }
        });

        autoSignIn_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(signInIntent);
            }
        });

        return view;
    }

    private void handleSignInTask(Task<GoogleSignInAccount> task) {
        try {
            //從 Task 中取得 Google 登入成功後的帳號資訊
            GoogleSignInAccount account = task.getResult(ApiException.class);
            //使用 GoogleSignInAccount 中的 ID Token 創建 Firebase 身分驗證的憑證，用於後續的 Firebase 登入驗證。
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
            //使用 Firebase 憑證進行身分驗證後的成功處理程式碼區塊
            auth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if (task.isSuccessful()){
                        //獲取當前已登入的 Firebase 使用者物件
                        FirebaseUser user = auth.getCurrentUser();
                        users users1 = new users();
                        //確保user不為空，如果user為空，則會觸發斷言失敗，並可能引發異常
                        assert user != null;
                        //user不為空就執行以下程式，把Name，UID，ProfilePic和Email儲存進user1的物件中
                        users1.setUserName(user.getDisplayName());
                        users1.setUserId(user.getUid());
                        users1.setProfilePic(user.getPhotoUrl().toString());
                        users1.setUserEmail(user.getEmail());
                        //把資料儲存進Firebase 的RealTime Database
                        database.getReference().child("users").child(user.getUid()).setValue(users1);

                        //startActivity到MainActivity
                        startActivity(new Intent(getContext(), MainActivity.class));
                        //結束當前頁面，避免返回來造成錯誤
                        getActivity().finish();
                    }
                    else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (ApiException e) {
            Toast.makeText(getContext(), "Failed or Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}