package com.training.dan.githubusersrepos;

import com.training.dan.githubusersrepos.Retrofit.GithubRetrofit;
import com.training.dan.githubusersrepos.Retrofit.IGithub;

import dagger.Component;
import retrofit2.Retrofit;

@Component(modules = GithubRetrofit.class)
public interface GithubComponent {
    IGithub getGithubApi();
}
