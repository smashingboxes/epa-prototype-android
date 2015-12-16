package com.smashingboxes.epa_prototype_android.fitbit;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.smashingboxes.epa_prototype_android.fitbit.activity.FitbitActivityApi;
import com.smashingboxes.epa_prototype_android.fitbit.activity.Period;
import com.smashingboxes.epa_prototype_android.fitbit.activity.TimeSeriesResourcePath;
import com.smashingboxes.epa_prototype_android.fitbit.auth.FitbitLoginCache;
import com.smashingboxes.epa_prototype_android.models.ActivityData;
import com.smashingboxes.epa_prototype_android.models.FitbitAuthModel;
import com.smashingboxes.epa_prototype_android.models.FitbitProfile;
import com.smashingboxes.epa_prototype_android.network.BaseRequest;
import com.smashingboxes.epa_prototype_android.network.NetworkRequestManager;
import com.smashingboxes.epa_prototype_android.network.RequestHandler;
import com.smashingboxes.epa_prototype_android.network.UrlGenerator;
import com.smashingboxes.epa_prototype_android.network.parsing.ClassParseStrategy;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public class FitbitRequestManager implements FitbitApi, RequestHandler {

    private static NetworkRequestManager delegate;

    private FitbitAuthModel authencationModel;
    private Object cancelTag;

    public FitbitRequestManager(Context context, FitbitAuthModel authencationModel){
        if(delegate == null){
            delegate = NetworkRequestManager.getInstance(context);
        }
        this.authencationModel = authencationModel;
    }

    public FitbitRequestManager(Context context, FitbitAuthModel authencationModel, Object cancelTag){
        this(context, authencationModel);
        setCancelTag(cancelTag);
    }

    @Override
    public void addRequest(BaseRequest<?> request, Object tag) {
        if(authencationModel == null || authencationModel.isExpired()){
            FitbitLoginCache.logout(delegate.getContext());
            return;
        }
        request.setHeaders(authencationModel.getAuthHeaders());
        delegate.addRequest(request, tag);
    }

    @Override
    public void cancelAllForTag(Object tag) {
        delegate.cancelAllForTag(tag);
    }

    public void setCancelTag(Object tag){
        this.cancelTag = tag;
    }

    @Override
    public void getUserProfile(String userId, Response.Listener<FitbitProfile> fitbitProfileListener, Response.ErrorListener errorListener) {
        String url = UrlGenerator.getFitbitUserProfileUrl(userId);
        BaseRequest<FitbitProfile> getProfile = new BaseRequest<>(Request.Method.GET, url,
                fitbitProfileListener, errorListener, new ClassParseStrategy<>(FitbitProfile.class));
        addRequest(getProfile, cancelTag);
    }

    @Override
    public void getCurrentUserProfile(Response.Listener<FitbitProfile> fitbitProfileListener, Response.ErrorListener errorListener) {
        getUserProfile(CURRENT_USER_ID, fitbitProfileListener, errorListener);
    }

    @Override
    public void getUserDailySummaryActivityData(String userId, String date, Response.Listener<ActivityData> fitbitActivityListener, Response.ErrorListener errorListener) {
        String url = UrlGenerator.getFitbitUserActivitiesUrl(userId, date);
        BaseRequest<ActivityData> getProfile = new BaseRequest<>(Request.Method.GET, url,
                fitbitActivityListener, errorListener, new ClassParseStrategy<>(ActivityData.class));
        addRequest(getProfile, cancelTag);
    }

    @Override
    public void getCurrentUserDailySummaryActivityData(String date, Response.Listener<ActivityData> fitbitActivityListener, Response.ErrorListener errorListener) {
        getUserDailySummaryActivityData(CURRENT_USER_ID, date, fitbitActivityListener, errorListener);
    }

    @Override
    public void getUserTimeSeriesData(String userId, TimeSeriesResourcePath resourcePath, String date, Period period, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        String url = UrlGenerator.getActivityTimeSeriesUrl(userId, resourcePath.getFullPath(), date, period.durationKey);
        BaseRequest<String> getTimeSeriesData = new BaseRequest<>(Request.Method.GET, url, responseListener,
                errorListener, BaseRequest.NO_PARSE_STRAT);
        addRequest(getTimeSeriesData, cancelTag);
    }

    @Override
    public void getCurrentUserTimeSeriesData(TimeSeriesResourcePath resourcePath, String date, Period period, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        getUserTimeSeriesData(CURRENT_USER_ID, resourcePath, date, period, responseListener, errorListener);
    }
}