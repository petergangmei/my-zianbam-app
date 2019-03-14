package com.zianbam.yourcommunity.Fragment;

import com.zianbam.yourcommunity.Notifications.MyResponse;
import com.zianbam.yourcommunity.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
  @Headers(
          {
                  "Content-Type:application/json",
                  "Authorization:key=AAAAp0oJDyw:APA91bHpZZ8x16kM_BdMjQi16rc8UHuKWM7qBMd2_z3fNRN5m2nGAQZt-BRd9VVD_KT0JARbLDO0EmLCFS1yXFDBcsQgAvqW0VdMDCb7bXhCU5ET6Vg_R0N22rNUMXD4lQgDLYho27U8"
          }
  )
  @POST("fcm/send")
  Call<MyResponse> sendNotification(@Body Sender body);
}
