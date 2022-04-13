package com.blockent.memoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blockent.memoapp.api.NetworkClient;
import com.blockent.memoapp.api.UserApi;
import com.blockent.memoapp.model.UserReq;
import com.blockent.memoapp.model.UserRes;
import com.blockent.memoapp.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignUpActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPasswd;
    EditText editNickname;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editEmail = findViewById(R.id.editEmail);
        editPasswd = findViewById(R.id.editPasswd);
        editNickname = findViewById(R.id.editNickname);

        Button btnDone = findViewById(R.id.btnDone);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이메일, 비번, 닉네임이 정상인지 체크
                String email = editEmail.getText().toString().trim();
                String password = editPasswd.getText().toString().trim();
                String nickname = editNickname.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || nickname.isEmpty() ){
                    Toast.makeText(SignUpActivity.this,
                            "비어있는 항목이 있으면 안됩니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 4 || password.length() > 12){
                    Toast.makeText(SignUpActivity.this,
                            "비밀번호는 4자리에서 12자리까지 입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 정상이면, 네트워크 통해서 보낸다.
                showProgress("회원 가입 중입니다. 잠시만 기다리세요...");

                Retrofit retrofit = NetworkClient.getRetrofitClient(SignUpActivity.this);
                UserApi api = retrofit.create(UserApi.class);

                UserReq userReq = new UserReq(email, password, nickname);
                Call<UserRes> call = api.userSignUp(userReq);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        // 화면에서 똥글뱅이를 없애준다.
                        dismissProgress();
                        if(response.isSuccessful()){
                            // http status code 가 200일때
                            SharedPreferences sp = getSharedPreferences(Utils.PREFERENCES_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("accessToken", response.body().getAccess_token());
                            editor.apply();

                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                            
                        }else{
                            // http status code 가 200이 아닌경우
                            if(response.code() == 400){
                                Toast.makeText(SignUpActivity.this,
                                        "이미 회원가입 되어있습니다. 로그인하세요",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        // 화면에서 똥글뱅이를 없애준다.
                        dismissProgress();

                        Toast.makeText(SignUpActivity.this,
                                "네트워크에 문제가 있습니다.",
                                Toast.LENGTH_LONG).show();
                        return;

                    }
                });


                // 네트워크에서 데이터받으면, 억세스토큰을 저장한다. 이 액티비티는 종료하고
                // 메인 액티비티로 이동.
            }
        });


    }

    // 우리가 만든 함수. 화면에 네트워크 처리중이라고 표시할 것.
    private void showProgress(String message){
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.show();
    }
    private void dismissProgress(){
        progressDialog.dismiss();
    }

}