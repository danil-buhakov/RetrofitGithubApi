package com.training.dan.githubusersrepos.Retrofit;

import com.training.dan.githubusersrepos.Model.Repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IGithub {

    @GET("/users/{username}/repos")
    Call<List<Repository>> getUsersRepo(@Path("username") String username);
}
