package com.blockent.memoapp.api;

import com.blockent.memoapp.model.UserReq;
import com.blockent.memoapp.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {
    // 로그인 API
    @POST("/dev/v1/user/login")
    Call<UserRes> userLogin(@Body UserReq userReq);

    // 회원가입 API
    @POST("/dev/v1/user/register")
    Call<UserRes> userSignUp(@Body UserReq userReq);
}
