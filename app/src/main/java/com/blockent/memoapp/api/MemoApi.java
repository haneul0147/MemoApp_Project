package com.blockent.memoapp.api;

import com.blockent.memoapp.model.MemoList;
import com.blockent.memoapp.model.MemoReq;
import com.blockent.memoapp.model.MemoRes;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MemoApi {

    // 메모 생성 API
    @Multipart
    @POST("/dev/v1/memo")
    Call<MemoRes> addMemo(@Header("Authorization") String accessToken,
                          @Part MultipartBody.Part photo,
                          @PartMap Map<String, RequestBody> params);

    // 내 메모 리스트 가져오는 API
    @GET("/dev/v1/memo")
    Call<MemoList> getMemoList(@Header("Authorization") String accessToken,
                               @Query("offset") int offset,
                               @Query("limit") int limit);

    // 메모 업데이트 API
    @PUT("/dev/v1/memo/{memoId}")
    Call<MemoRes> updateMemo(@Header("Authorization") String accessToken,
                             @Path("memoId") int memoId,
                             @Body MemoReq memoReq);

    // 메모 삭제 API
    @DELETE("/dev/v1/memo/{memoId}")
    Call<MemoRes> deleteMemo(@Header("Authorization") String accessToken,
                             @Path("memoId") int memoId);


}
