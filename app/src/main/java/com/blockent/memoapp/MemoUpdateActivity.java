package com.blockent.memoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blockent.memoapp.api.MemoApi;
import com.blockent.memoapp.api.NetworkClient;
import com.blockent.memoapp.model.Memo;
import com.blockent.memoapp.model.MemoReq;
import com.blockent.memoapp.model.MemoRes;
import com.blockent.memoapp.utils.Utils;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MemoUpdateActivity extends AppCompatActivity {

    EditText editTitle;
    Button btnDate;
    Button btnTime;
    EditText editContent;

    Calendar myCalendar = Calendar.getInstance();

    String dateStr;
    String timeStr;

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            int newMonth = month + 1;

            // 버튼에 유저가 선택한 년월일로 표시
            dateStr = ""+year+"-"+newMonth+"-"+dayOfMonth;
            btnDate.setText(dateStr);

        }
    };

    TimePickerDialog.OnTimeSetListener myTimePicker = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            timeStr = ""+i+":"+i1;
            btnTime.setText(timeStr);
        }
    };
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_update);
        
        getSupportActionBar().setTitle("메모 수정");

        editTitle = findViewById(R.id.editTitle);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);
        editContent = findViewById(R.id.editContent);
        Button btnSave = findViewById(R.id.btnSave);

        // 메인액티비티로부터 넘겨받은 데이터를 가져온다.
        Memo memo = (Memo)getIntent().getSerializableExtra("memo");

        // 데이터를 가져왔으니, 화면에 표시!
        editTitle.setText( memo.getTitle() );

        // 2020-03-11 12:34
        String datetime = memo.getDate();
        String[] strArray = datetime.split(" ");

        dateStr = strArray[0];
        timeStr = strArray[1];

        btnDate.setText(  strArray[0] );
        btnTime.setText(  strArray[1] );

        editContent.setText( memo.getContent() );


        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MemoUpdateActivity.this,
                        myDatePicker,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                new TimePickerDialog(MemoUpdateActivity.this, myTimePicker,
                        hour, minute, false).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTitle.getText().toString().trim();
                String content = editContent.getText().toString().trim();
                String datetime = dateStr + " " + timeStr;

                if (title.isEmpty() || content.isEmpty() || datetime.isEmpty()){
                    Toast.makeText(MemoUpdateActivity.this,
                            "항목을 전부 작성해 주세요",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // 네트워크로 보낸다.

                Log.i("MyMemoApp", title + " " + datetime + " " +content);

                showProgress("메모 저장 중입니다...");

                Retrofit retrofit = NetworkClient.getRetrofitClient(MemoUpdateActivity.this);
                MemoApi api = retrofit.create(MemoApi.class);

                SharedPreferences sp = getSharedPreferences(Utils.PREFERENCES_NAME, MODE_PRIVATE);
                String accessToken = sp.getString("accessToken", "");

                MemoReq memoReq = new MemoReq(title, datetime, content);
                Call<MemoRes> call = api.updateMemo("Bearer "+accessToken,
                                                    memo.getId(),
                                                    memoReq);

                Log.i("MyMemoApp", "memo id : " + memo.getId());

                call.enqueue(new Callback<MemoRes>() {
                    @Override
                    public void onResponse(Call<MemoRes> call, Response<MemoRes> response) {
                        dismissProgress();
                        if(response.isSuccessful()){
                            setResult(4);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<MemoRes> call, Throwable t) {
                        dismissProgress();
                    }
                });


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