package com.canatme.zpirit.Service;

import com.canatme.zpirit.Dataclasses.OrderCreatedResponseDto;
import com.canatme.zpirit.Dataclasses.RazorPayCreateOrderDtoBody;
import com.canatme.zpirit.Utils.Constants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST(Constants.RP_ORDERS)
    Call<OrderCreatedResponseDto> sendFeedback(@Body RazorPayCreateOrderDtoBody razorPayCreateOrderDtoBody);
}
