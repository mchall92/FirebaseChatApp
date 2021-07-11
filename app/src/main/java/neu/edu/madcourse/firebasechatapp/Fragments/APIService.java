package neu.edu.madcourse.firebasechatapp.Fragments;

import neu.edu.madcourse.firebasechatapp.Notification.MyResponse;
import neu.edu.madcourse.firebasechatapp.Notification.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAp3HAFug:APA91bEA4fpGf8rRbc_cEth5voSqBjcAA7Fm9BvmOZ00mBxdt9TfZ01jbsP-407XX9D6b4HYfhO5IIhKOjnv18JKI24itxUeqNDhRFQL-4V9RkSTeF6x519wfagQLSDyXwO4_dvifJOQ"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotifications(@Body Sender body);
}