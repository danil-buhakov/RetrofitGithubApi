package com.training.dan.githubusersrepos;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.training.dan.githubusersrepos.Model.Repository;
import com.training.dan.githubusersrepos.Retrofit.GithubRetrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GithubActivity extends AppCompatActivity {
    private static final String TAG = "GithubActivity";
    RecyclerView mRecyclerView;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Wait please...");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_github,menu);
        MenuItem search = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG,"onQueryTextSubmit " + query);
                mProgressDialog.show();
                GithubRetrofit.getApi().getUsersRepo(query)
                        .enqueue(new Callback<List<Repository>>() {
                            @Override
                            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                                Log.i(TAG,"onResponse");
                                Toast.makeText(GithubActivity.this,"onResponse",Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<List<Repository>> call, Throwable t) {
                                Log.i(TAG,"onFailure");
                                Toast.makeText(GithubActivity.this,"onFailure",Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            }
                        });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
