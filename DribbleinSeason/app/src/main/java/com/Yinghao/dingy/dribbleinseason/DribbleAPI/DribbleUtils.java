package com.Yinghao.dingy.dribbleinseason.DribbleAPI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.Yinghao.dingy.dribbleinseason.Model.Bucket;
import com.Yinghao.dingy.dribbleinseason.Model.Like;
import com.Yinghao.dingy.dribbleinseason.Model.Shot;
import com.Yinghao.dingy.dribbleinseason.views.AuthWebActivity;
import com.Yinghao.dingy.dribbleinseason.Model.ModelUtils;
import com.Yinghao.dingy.dribbleinseason.Model.User;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


public class DribbleUtils {
    public static final int REQ_CODE = 100;
    public static final int SHOTS_PER_PAGE = 4;


    private static String accessToken;
    private static User user;

    private static final String SP_AUTH = "SP_AUTH";

    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER = "user";

    private  static final String KEY_URL = "url";
    private  static final String KEY_CLIENT_ID = "client_id";
    private  static final String KEY_CLIENT_SECRET = "client_secret";
    private  static final String KEY_REDIRECT_URI = "rediect_url";
    private  static final String KEY_SCOPE = "scope";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_NAME = "name";
    private static final String KEY_SHOT_ID = "shot_id";


    public static final String KEY_CODE = "code";

    private static final String CLIENT_ID = "23470673c816ac070e7c6beb79530d9b234609bce6841ec955ccae8162f8ed30";
    private static final String CLIENT_SECRET = "08f037e74427b1acda126f07d69a0c5484d3a969bee3e4593e9d64f3074d24db";
    private static final String URI_AUTHORIZE = "https://dribbble.com/oauth/authorize";
    private static final String URI_TOKEN = "https://dribbble.com/oauth/token";
    public static final String REDIRECT_URI = "https://dev.twitter.com/apps/new";
    private static final String SCOPE = "public+write";

    private static OkHttpClient client = new OkHttpClient();
    private static final TypeToken<User> USER_TYPE = new TypeToken<User>(){};
    private static final String API_URL = "https://api.dribbble.com/v1/";
    private static final String USER_END_POINT = API_URL + "user";
    private static final String USERS_END_POINT = API_URL + "users";
    private static final String BUCKETS_END_POINT = API_URL + "buckets";


    private static final String SHOTS_END_POINT = API_URL + "shots";
    private static final TypeToken<List<Shot>> SHOT_LIST_TYPE = new TypeToken<List<Shot>>(){};
    private static final TypeToken<List<Like>> LIKE_LIST_TYPE = new TypeToken<List<Like>>(){};
    private static final TypeToken<List<Bucket>> BUCKET_LIST_TYPE = new TypeToken<List<Bucket>>(){};
    private static final TypeToken<Bucket> BUCKET_TYPE = new TypeToken<Bucket>(){};


    private static final TypeToken<Like> LIKE_TYPE = new TypeToken<Like>(){};


    public static void InitialToken(@NonNull Context context){
        accessToken = loadToken(context);
        if(accessToken != null){
            user = loadUser(context);
        }
    }

    public static Boolean isLoggedin(){
        return  accessToken != null;
    }

    public static String loadToken(@NonNull Context context){
        String tokenString;
        //get the sp that stores my token using sp name : SP_AUTH
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        //get the token if exists, using key : KEY_ACCESS_TOKEN, if no token tokenString = null
        tokenString = sp.getString(KEY_ACCESS_TOKEN,null);
        return tokenString;
    }

    public static User loadUser(@NonNull Context context) {
        return ModelUtils.read(context, KEY_USER, new TypeToken<User>(){});
    }

    public static Intent openAuthWeb(@NonNull Activity activity){
        Intent intent = new Intent(activity, AuthWebActivity.class);
        intent.putExtra(DribbleUtils.KEY_URL, buildUrlString());
        return  intent;
    }

    public static String obtainTokenFromDribble(String auth_code)throws IOException{
        OkHttpClient client = new OkHttpClient();
        RequestBody postBody = new FormBody.Builder()
                .add(KEY_CLIENT_ID,CLIENT_ID)
                .add(KEY_CLIENT_SECRET, CLIENT_SECRET)
                .add(KEY_CODE, auth_code)
                .add(KEY_REDIRECT_URI, REDIRECT_URI)
                .build();
        Request request = new Request.Builder()
                .url(URI_TOKEN)
                .post(postBody)
                .build();
        //build a request and send it to dribble through http
        Response response = client.newCall(request).execute();
        String responseString = response.body().string();
        try {
            JSONObject obj = new JSONObject(responseString);
            return obj.getString(KEY_ACCESS_TOKEN);
        }catch (JSONException e){
            e.printStackTrace();
            return "";
        }
    }

    public static void login(@NonNull  Context context,
                             @NonNull  String token)throws DribbleException{
        DribbleUtils.accessToken = token;
        storeToken(context, token);

        DribbleUtils.user = getUser();
        storeUser(context, user);
    }

    public static void logout(@NonNull Context context) {
        storeToken(context, null);
        storeUser(context, null);

        CookieSyncManager.createInstance(context);
        CookieManager.getInstance().removeAllCookie();
        CookieSyncManager.getInstance().sync();

        accessToken = null;
        user = null;
    }

    public static User getCurrentUser() {
        User u = user;
        return u;
    }

    private static void storeToken(@NonNull Context context, @NonNull String token){
        ModelUtils.save(context,KEY_ACCESS_TOKEN,token);
    }

    private static void storeUser(@NonNull Context context, @NonNull User user){
        ModelUtils.save(context, KEY_USER, user);
    }

    private static User getUser() throws DribbleException{
        User user = reponseToObj(makeGetRequest(USER_END_POINT),USER_TYPE);
        return user;
    }

    private static <T> T  reponseToObj(Response response, TypeToken<T> typeToken) throws DribbleException{
        String responseString;
        try {
            responseString = response.body().string();
        } catch (IOException e) {
            throw new DribbleException(e.getMessage());
        }

        Log.d(TAG, responseString);

        try {
            return ModelUtils.toObject(responseString, typeToken);
        } catch (JsonSyntaxException e) {
            throw new DribbleException(responseString);
        }

    }

    private static Response makeGetRequest(String url) throws DribbleException {
        Request request = requestBuilder(url).build();
        return executeRequest(request);
    }

    private static Response makePostRequest(String url, RequestBody body) throws DribbleException {
        Request request = requestBuilder(url)
                .post(body)
                .build();
        return executeRequest(request);
    }

    private static Response makePutRequest(String url,
                                           RequestBody requestBody) throws DribbleException {
        Request request = requestBuilder(url)
                .put(requestBody)
                .build();
        return executeRequest(request);
    }

    private static Response makeDeleteRequest(String url) throws DribbleException {
        Request request = requestBuilder(url)
                .delete()
                .build();
        return executeRequest(request);
    }

    private static Response executeRequest(Request request) throws DribbleException {
        try {
            Response response = client.newCall(request).execute();
            Log.d(TAG, response.header("X-RateLimit-Remaining"));
            return response;
        } catch (IOException e) {
            throw new DribbleException(e.getMessage());
        }
    }


    private static Request.Builder requestBuilder(String url) throws DribbleException {
        Request.Builder request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url);
        return request;
    }

    private static String buildUrlString(){
        String url = Uri.parse(URI_AUTHORIZE)
                .buildUpon()
                .appendQueryParameter(KEY_CLIENT_ID, CLIENT_ID)
                .build()
                .toString();
        // fix encode issue
        url += "&" + KEY_REDIRECT_URI + "=" + REDIRECT_URI;
        url += "&" + KEY_SCOPE + "=" + SCOPE;
        // the return value looks like:
        // https://dribbble.com/oauth/authorize?client_id=16b5398857f25ebbcf6e8efe9c27f6379998d0de45a16f24a042d3f3670a6a71&redirect_uri=http://www.dribbbo.com&scope=public+write
        // which is exactly what Dribbble API doc requires us to do
        // check out "1. Redirect users to request Dribbble access." at http://developer.dribbble.com/v1/oauth/
        return url;

    }

    private static void checkStatusCode(Response response,
                                        int statusCode) throws DribbleException {
        if (response.code() != statusCode) {
            throw new DribbleException(response.message());
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////

    public static List<Shot> getShots(int page) throws DribbleException {
        String url = SHOTS_END_POINT + "?page=" + page;
        String responseString;
        try {
            responseString = makeGetRequest(url).body().string();
        }catch (IOException e){
            throw new DribbleException(e.getMessage());
        }
        Log.d(TAG, responseString);
        List<Shot> tempShotList = ModelUtils.toObject(responseString, SHOT_LIST_TYPE);

        return tempShotList;
    }

    public static List<Like> getLikes(int page) throws DribbleException {
        String url = USER_END_POINT + "/likes?page=" + page;
        return reponseToObj(makeGetRequest(url), LIKE_LIST_TYPE);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<Shot> getLikedShots(int page) throws DribbleException {
        List<Like> tempLikeList = getLikes(page);
        List<Shot> tempShotList = new ArrayList<Shot>();

        for(Like like:tempLikeList){
            like.shot.liked = true;
            tempShotList.add(like.shot);
        }
        return tempShotList;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static void likeShot(String shotId) throws DribbleException{
        String url = SHOTS_END_POINT + "/" + shotId + "/like";
        Response response = makePostRequest(url,new FormBody.Builder().build());
        checkStatusCode(response, HttpURLConnection.HTTP_CREATED);
       // return reponseToObj(response, LIKE_TYPE);
    }

    public static void unlikeShot(String shotId) throws DribbleException{
        String url = SHOTS_END_POINT + "/" + shotId + "/like";
        Response response = makeDeleteRequest(url);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
        // return reponseToObj(response, LIKE_TYPE);
    }

    public static boolean isLikingShot(@NonNull String id) throws DribbleException {
        String url = SHOTS_END_POINT + "/" + id + "/like";
        Response response = makeGetRequest(url);
        switch (response.code()) {
            case HttpURLConnection.HTTP_OK:
                return true;
            case HttpURLConnection.HTTP_NOT_FOUND:
                return false;
            default:
                throw new DribbleException(response.message());
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////

    public static List<Bucket> getUserBuckets(int page) throws DribbleException {
        String url = USER_END_POINT + "/" + "buckets?page=" + page;
        return reponseToObj(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    /**
     * Will return all the buckets for the logged in user
     * @return
     * @throws DribbleException
     */
    public static List<Bucket> getUserBuckets() throws DribbleException {
        String url = USER_END_POINT + "/" + "buckets?per_page=" + Integer.MAX_VALUE;
        return reponseToObj(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static List<Bucket> getUserBuckets(@NonNull String userId,
                                              int page) throws DribbleException {
        String url = USERS_END_POINT + "/" + userId + "/buckets?page=" + page;
        return reponseToObj(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static Bucket newBucket(@NonNull String name,
                                   @NonNull String description) throws DribbleException {
        FormBody formBody = new FormBody.Builder()
                .add(KEY_NAME, name)
                .add(KEY_DESCRIPTION, description)
                .build();
        return reponseToObj(makePostRequest(BUCKETS_END_POINT, formBody), BUCKET_TYPE);
    }

    /**
     * Will return all the buckets for a certain shot
     * @param shotId
     * @return
     * @throws DribbleException
     */
    public static List<Bucket> getShotBuckets(@NonNull String shotId) throws DribbleException {
        String url = SHOTS_END_POINT + "/" + shotId + "/buckets?per_page=" + Integer.MAX_VALUE;
        return reponseToObj(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static void addBucketShot(@NonNull String bucketId,
                                     @NonNull String shotId) throws DribbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotId)
                .build();

        Response response = makePutRequest(url, formBody);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    public static void removeBucketShot(@NonNull String bucketId,
                                        @NonNull String shotId) throws DribbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotId)
                .build();

        Response response = makeDeleteRequest(url, formBody);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    private static Response makeDeleteRequest(String url,
                                              RequestBody requestBody) throws DribbleException {
        Request request = requestBuilder(url)
                .delete(requestBody)
                .build();
        return executeRequest(request);
    }

    public static List<Shot> getBucketShots(@NonNull String bucketId,
                                            int page) throws DribbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        return reponseToObj(makeGetRequest(url), SHOT_LIST_TYPE);
    }
}
