package com.training.dan.githubusersrepos;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.training.dan.githubusersrepos.Model.Repository;
import com.training.dan.githubusersrepos.Retrofit.GithubRetrofit;
import com.training.dan.githubusersrepos.Retrofit.IGithub;
import com.training.dan.githubusersrepos.view.RepositoryAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GithubActivity extends AppCompatActivity {
    public static final String TAG = "GithubActivity";
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private List<Repository> mRepositories;
    private CompositeDisposable compositeDisposable;
    private IGithub githubApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github);
        compositeDisposable = new CompositeDisposable();
        githubApi = DaggerGithubComponent.create().getGithubApi();
        init();
    }

    private void init() {
        mRepositories = new ArrayList<>();
        setupRecyclerView();
        setupProgressDialog();
    }

    private void setupProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Wait please...");
    }

    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RepositoryAdapter(mRepositories));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_github, menu);
        MenuItem search = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "onQueryTextSubmit " + query);
                mProgressDialog.show();
                compositeDisposable.add(
                        githubApi.getUsersRepo(query)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(list -> {
                                    Log.i(TAG, "onResponse " + list.size());
                                    Toast.makeText(GithubActivity.this, "onResponse", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                    mRepositories.clear();
                                    mRepositories.addAll(list);
                                    mRecyclerView.getAdapter().notifyDataSetChanged();
                                },
                                throwable -> {
                                    Log.i(TAG, "onFailure");
                                    Toast.makeText(GithubActivity.this, "onFail", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                })
                );
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2)
                    compositeDisposable.add(githubApi.getUsersRepo(newText)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(list -> {
                                        Log.i(TAG, "onResponse " + list.size());
                                        Toast.makeText(GithubActivity.this, "onResponse", Toast.LENGTH_SHORT).show();
                                        mProgressDialog.dismiss();
                                        mRepositories.clear();
                                        mRepositories.addAll(list);
                                        mRecyclerView.getAdapter().notifyDataSetChanged();
                                    },
                                    throwable -> {
                                        Log.i(TAG, "onFailure");
                                        Toast.makeText(GithubActivity.this, "onFail", Toast.LENGTH_SHORT).show();
                                        mProgressDialog.dismiss();
                                    })
                    );
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clicker:
                Toast.makeText(this, "clicker clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
