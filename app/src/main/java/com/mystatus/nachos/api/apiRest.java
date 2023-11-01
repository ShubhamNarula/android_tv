package com.mystatus.nachos.api;

import com.mystatus.nachos.config.Global;
import com.mystatus.nachos.entity.Actor;
import com.mystatus.nachos.entity.ApiResponse;
import com.mystatus.nachos.entity.Category;
import com.mystatus.nachos.entity.Channel;
import com.mystatus.nachos.entity.CheckIsSubscribe;
import com.mystatus.nachos.entity.Comment;
import com.mystatus.nachos.entity.Country;
import com.mystatus.nachos.entity.Data;
import com.mystatus.nachos.entity.Genre;
import com.mystatus.nachos.entity.Language;
import com.mystatus.nachos.entity.Poster;
import com.mystatus.nachos.entity.Season;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by Tamim on 28/09/2017.
 */

public interface apiRest {


    @GET("version/check/{code}/{user}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> check(@Path("code") Integer code,@Path("user") Integer user);

    @GET("user/checkpaid/{email}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> checkpaid(@Path("email") String email);

    @GET("install/add/{id}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> addInstall(@Path("id") String id);

    @GET("device/checkdevice/{tkn}/{user_id}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse>checkdevice(@Path("tkn")  String tkn,@Path("user_id") String email) ;

    @GET("device/{tkn}/{user_id}/"+ Global.SECURE_KEY+ "/"+ Global.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> addDevice(@Path("tkn")  String tkn,@Path("user_id")  String user_id);

//    @GET("/check/{email}/"+ Global.SECURE_KEY+ "/"+ Global.ITEM_PURCHASE_CODE + "/")
//    Call<ApiResponse> checkDevice(@Path("email")  String email);

    @GET("search/{query}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<Data> searchData(@Path("query") String query);

    @GET("role/by/poster/{id}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Actor>> getRolesByPoster(@Path("id") Integer id);

    @GET("actor/all/{page}/{search}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Actor>> getActorsList(@Path("page") Integer page,@Path("search") String search);

    @GET("movie/by/{id}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<Poster> getPosterById(@Path("id") Integer id);

    @GET("channel/by/{id}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<Channel> geChannelById(@Path("id") Integer id);


    @GET("movie/random/{genres}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Poster>> getRandomMoivies(@Path("genres") String genres);

    @GET("channel/random/{categories}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Channel>> getRandomChannel(@Path("categories") String categories);

    @FormUrlEncoded
    @POST("user/register/"+ Global.SECURE_KEY+"/"+ Global.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> register(@Field("name") String name, @Field("username") String username, @Field("password") String password, @Field("type") String type, @Field("image") String image);

    @FormUrlEncoded
    @POST("user/login/"+ Global.SECURE_KEY+"/"+ Global.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> login(@Field("username") String email, @Field("password") String password, @Field("type") String type);

    @GET("user/logout/{tkn}/{username}/"+ Global.SECURE_KEY+"/"+ Global.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> logout(@Path("tkn") String tkn,@Path("username") String username);

    @FormUrlEncoded
    @POST("user/token/"+ Global.SECURE_KEY+"/"+ Global.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> editToken(@Field("user") Integer user, @Field("key") String key, @Field("token_f") String token_f, @Field("name") String name);

    @FormUrlEncoded
    @POST("watch/"+ Global.SECURE_KEY+"/"+ Global.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> updateWatchTime(@Field("email") String email, @Field("poster_id") Integer poster_id, @Field("episode_id") Integer episode_id, @Field("mili_second") String mili_second, @Field("percentage") Integer percentage, @Field("last_watch") String last_watch, @Field("deleted") Integer deleted);


    @FormUrlEncoded
    @POST("comment/channel/add/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> addChannelComment(@Field("user") String user,@Field("key")  String key, @Field("id") Integer id, @Field("comment") String comment);


    @GET("comments/by/channel/{id}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Comment>> getCommentsByChannel(@Path("id") Integer id);


    @FormUrlEncoded
    @POST("comment/poster/add/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> addPosterComment(@Field("user") String user,@Field("key")  String key, @Field("id") Integer id, @Field("comment") String comment);


    @GET("comments/by/poster/{id}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Comment>> getCommentsByPoster(@Path("id") Integer id);

    @GET("subtitles/by/movie/{id}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Language>> getSubtitlesByPoster(@Path("id") Integer id);

    @GET("subtitles/by/episode/{id}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Language>> getSubtitlesByEpisode(@Path("id") Integer id);


    @FormUrlEncoded
    @POST("rate/poster/add/"+Global.SECURE_KEY+"/"+Global.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> addPosterRate(@Field("user")  String user,@Field("key")  String key,@Field("poster") Integer poster,@Field("value") float value);


    @FormUrlEncoded
    @POST("rate/channel/add/"+Global.SECURE_KEY+"/"+Global.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> addChannelRate(@Field("user")  String user,@Field("key")  String key,@Field("channel") Integer channel,@Field("value") float value);

    @FormUrlEncoded
    @POST("poster/add/share/"+ Global.SECURE_KEY+"/"+ Global.ITEM_PURCHASE_CODE+"/")
    Call<Integer> addPosterShare(@Field("id")  Integer id);

    @FormUrlEncoded
    @POST("first/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<Data> homeData(@Field("email")  String email);

    @FormUrlEncoded
    @POST("channel/add/share/"+ Global.SECURE_KEY+"/"+ Global.ITEM_PURCHASE_CODE+"/")
    Call<Integer> addChannelShare(@Field("id")  Integer id);

    @GET("movie/by/actor/{id}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Poster>> getPosterByActor(@Path("id") Integer id);

    @FormUrlEncoded
    @POST("support/add/"+ Global.SECURE_KEY+"/"+ Global.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> addSupport(@Field("email") String email, @Field("name") String name , @Field("message") String message);


    @GET("movie/by/filtres/{genre}/{order}/{page}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Poster>> getMoviesByFiltres(@Path("genre") Integer genre,@Path("order") String order,@Path("page") Integer page);


    @GET("poster/by/filtres/{genre}/{order}/{page}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Poster>> getPostersByFiltres(@Path("genre") Integer genre,@Path("order") String order,@Path("page") Integer page);


    @GET("genre/all/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Genre>> getGenreList();

    @GET("serie/by/filtres/{genre}/{order}/{page}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Poster>> getSeriesByFiltres(@Path("genre") Integer genre,@Path("order") String order,@Path("page") Integer page);

    @GET("season/by/serie/{id}/{email}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Season>> getSeasonsBySerie(@Path("id") Integer id,@Path("email") String email);

    @Multipart
    @POST("user/edit/"+ Global.SECURE_KEY+"/"+ Global.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> editProfile(@Part MultipartBody.Part file, @Part("id") Integer id, @Part("key") String key, @Part("name") String name);

    @GET("country/all/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Country>> getCountiesList();

    @GET("category/all/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Category>> getCategoriesList();

    @GET("channel/by/filtres/{category}/{country}/{page}/" + Global.SECURE_KEY + "/" + Global.ITEM_PURCHASE_CODE + "/")
    Call<List<Channel>> getChannelsByFiltres(@Path("category") Integer category,@Path("country") Integer country,@Path("page") Integer page);

    @FormUrlEncoded
    @POST("movie/add/download/"+ Global.SECURE_KEY+"/"+ Global.ITEM_PURCHASE_CODE+"/")
    Call<Integer> addMovieDownload(@Field("id")  Integer id);

    @FormUrlEncoded
    @POST("episode/add/download/"+ Global.SECURE_KEY+"/"+ Global.ITEM_PURCHASE_CODE+"/")
    Call<Integer> addEpisodeDownload(@Field("id")  Integer id);

    @FormUrlEncoded
    @POST("movie/add/view/"+ Global.SECURE_KEY+"/"+ Global.ITEM_PURCHASE_CODE+"/")
    Call<Integer> addMovieView(@Field("id")  Integer id);

    @FormUrlEncoded
    @POST("episode/add/view/"+ Global.SECURE_KEY+"/"+ Global.ITEM_PURCHASE_CODE+"/")
    Call<Integer> addEpisodeView(@Field("id")  Integer id);

    @FormUrlEncoded
    @POST("channel/add/view/"+ Global.SECURE_KEY+"/"+ Global.ITEM_PURCHASE_CODE+"/")
    Call<Integer> addChannelView(@Field("id")  Integer id);

    @GET
    Call<CheckIsSubscribe> checkSubscribe(@Url String url);

    @GET
    Call<CheckIsSubscribe> cancelSubscription(@Url String url);

}

