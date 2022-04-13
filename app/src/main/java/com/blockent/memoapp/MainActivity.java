package com.blockent.memoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.blockent.memoapp.adapter.MemoAdapter;
import com.blockent.memoapp.api.MemoApi;
import com.blockent.memoapp.api.NetworkClient;
import com.blockent.memoapp.model.Memo;
import com.blockent.memoapp.model.MemoList;
import com.blockent.memoapp.model.MemoRes;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    // 리스트를 페이징 처리하는데 필요한 변수들
    RecyclerView recyclerView;
    int offset = 0;
    int limit = 25;
    int cnt;
    List<Memo> memoList = new ArrayList<>();
    MemoAdapter adapter;

    ProgressBar progressBar;

    String accessToken;
    private ProgressDialog progressDialog;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        getSupportActionBar().setTitle("메모 리스트");

        Log.i("MyMemoApp", "MainActivity");

        // 억세스 토큰이 있는지 확인, (쉐어드 프리퍼런스에)
        SharedPreferences sp = getSharedPreferences("MyMemoApp", MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");

        if(accessToken.isEmpty()){
            // 로그인 액티비티를 띄운다.
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            // 메인액티비티에서 네트워크를 통해서, 내 메모 리스트를 서버로부터 가져온다.

            progressBar = findViewById(R.id.progressBar);

            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    // 리스트의 끝부분에 오면, 네트워크통해서 데이터를 불러와야 한다.
                    // 데이터를 불러와서, 리스트 끝에다가 추가로 붙여준다.
                    int lastPosition = ((LinearLayoutManager)recyclerView.
                            getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    int totalCount = recyclerView.getAdapter().getItemCount();

                    if( (lastPosition + 1) == totalCount){
                        if(cnt == limit){
                            // todo 네트워크를 통해서, 추가 데이터를 요청한다.
                            addNetworkData();
                        }
                    }

                }
            });
            // 네트워크를 통해서, 메모 데이터를 가져온다.
            getNetworkData();
        }

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MemoAdd.class);
                startActivityForResult(intent, 1);
            }
        });


    }

    private void addNetworkData() {
        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
        MemoApi api = retrofit.create(MemoApi.class);

        Call<MemoList> call = api.getMemoList("Bearer "+accessToken,
                offset, limit);

        call.enqueue(new Callback<MemoList>() {
            @Override
            public void onResponse(Call<MemoList> call, Response<MemoList> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()){

                    memoList.addAll( response.body().getList() );
                    adapter.notifyDataSetChanged();

                    cnt = response.body().getCount();
                    offset = offset + cnt;

                }else{
                    if(response.code() == 400){

                    }else if(response.code() == 401){

                    }
                }
            }

            @Override
            public void onFailure(Call<MemoList> call, Throwable t) {

            }
        });

    }

    // 처음에만 실행할 함수.
    private void getNetworkData() {
        offset = 0;
        cnt = 0;
        memoList.clear();

        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
        MemoApi api = retrofit.create(MemoApi.class);

        Call<MemoList> call = api.getMemoList("Bearer "+accessToken,
                offset, limit);

        call.enqueue(new Callback<MemoList>() {
            @Override
            public void onResponse(Call<MemoList> call, Response<MemoList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){
                    // 어댑터 만들어서, 리사이클러뷰에 붙여준다.
                    // 그러면 화면에, 리스트가 표시된다.

                    memoList = response.body().getList();
                    Log.i("MyMemoApp", ""+memoList.size());
                    Log.i("MyMeoApp", response.body().toString());
                    adapter = new MemoAdapter(MainActivity.this, memoList);
                    // 어댑터를 새로 만드는 코드 바로 아래에, 클릭 이벤트를 여기에 작성
                    adapter.setOnItemClickListener(new MemoAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int index) {
                            // 자바 코드에서, 메모리에 들어있는 메모 정보를 가져온다.
                            Memo memo = memoList.get(index);
                            // 메모 업데이트 액티비티로
                            // 이 이 메모 정보가 들어있는 클래스를 통으로
                            // 넘겨주면된다.
                            Intent i = new Intent(MainActivity.this, MemoUpdateActivity.class);
                            i.putExtra("memo", memo);
                            startActivityForResult(i, 2);
                        }

                        @Override
                        public void onDeleteClick(int index) {
                            
                            position = index;
                            
                            // x 이미지를 눌렀을때, 실행하는 코드
                            showAlertDialog();
                        }
                    });

                    recyclerView.setAdapter(adapter);

                    // 스크롤 처리를 위해 필요한 변수들 값 셋팅
                    cnt = response.body().getCount();
                    offset = offset + cnt;

                }else{

                    // 로그인이 풀린 상태이므로, 억세스토큰이 유효하지 않다.
                    // 따라서 로그인 화면을 띄운다.
                    if(response.code() == 500){
                        Intent intent = new Intent(MainActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<MemoList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("MyMemoApp", ""+requestCode+" "+resultCode);
        if(requestCode == 1 && resultCode == 3) {
            // 메모가 생성되어서, 다시 메인으로 돌아온경우.
            getNetworkData();
        } else if (requestCode == 2 && resultCode == 4){
            // 메모가 업데이트 된 경우, 다시 메인으로 돌아왔으므로,
            getNetworkData();
        }
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


    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("정말 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 네트워크를 통해서, api 호출하여 삭제를 수행한다.
                        deleteMemo();
                    }
                }).setNegativeButton("아니오", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteMemo(){
        // 1. 어떤 행의 x 이미지인지 파악해서 데이터를 가져온다.
        Memo memo = memoList.get(position);
        // 2. 삭제 API를 호출해서, 삭제한다.
        showProgress("메모를 삭제중입니다...");

        Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
        MemoApi api = retrofit.create(MemoApi.class);

        Call<MemoRes> call = api.deleteMemo("Bearer "+accessToken,
                memo.getId());
        call.enqueue(new Callback<MemoRes>() {
            @Override
            public void onResponse(Call<MemoRes> call, Response<MemoRes> response) {
                dismissProgress();
                if(response.isSuccessful()){
                    // 200 OK 인 경우이므로
                    memoList.remove(position);
                    adapter.notifyDataSetChanged();

                }else{

                }

            }

            @Override
            public void onFailure(Call<MemoRes> call, Throwable t) {
                dismissProgress();
            }
        });
    }

}