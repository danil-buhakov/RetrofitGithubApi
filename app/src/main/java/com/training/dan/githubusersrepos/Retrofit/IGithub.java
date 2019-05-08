package com.training.dan.githubusersrepos.Retrofit;

import com.training.dan.githubusersrepos.Model.Repository;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IGithub {

    @GET("/users/{username}/repos")
    Single<List<Repository>> getUsersRepo(@Path("username") String username);
}
