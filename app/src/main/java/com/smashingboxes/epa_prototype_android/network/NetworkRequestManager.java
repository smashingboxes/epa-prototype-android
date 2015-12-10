package com.smashingboxes.epa_prototype_android.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.smashingboxes.epa_prototype_android.fitbit.FitbitApi;
import com.smashingboxes.epa_prototype_android.models.FitbitProfile;
import com.smashingboxes.epa_prototype_android.network.parsing.ClassParseStrategy;

/**
 * Created by Austin Lanier on 12/8/15.
 * Updated by
 */
public class NetworkRequestManager implements RequestHandler {

    public static class NetworkExecutionException extends Exception {
        public NetworkExecutionException(String e) {
            super(e);
        }
    }

    private static final NetworkRequestManager mManager = new NetworkRequestManager();

    private static final String TAG = NetworkRequestManager.class.getName();

    private static RequestQueue requestQueue;

    private static Context mContext;

    private NetworkRequestManager() {}

    /**
     * Returns the NetworkRequestManager singleton
     */
    public static NetworkRequestManager getInstance() {
        return mManager;
    }

    /**
     * Initializes the static request queue singleton
     *
     * @param context
     */
    public static void initQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
            mContext = context.getApplicationContext();
        }
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            throw new NullPointerException("The request queue is null. You must call " +
                    "NetworkRequestManager.initQueue before accessing the queue.");
        }

        return requestQueue;
    }

    public Context getContext(){
        return mContext;
    }

    /**
     * Cancels all requests for the given object tag
     *
     * @param tag
     */
    @Override
    public void cancelAllForTag(Object tag) {
        getRequestQueue().cancelAll(tag);
    }

    /**
     *
     * A convience method for adding requests to the queue.  This is useful if we want to go back
     * and add different retry policies, check app state before dispatching, etc, for each request
     * that we add
     *
     * @param request
     * @param tag
     */
    @Override
    public void addRequest(BaseRequest<?> request, Object tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

}