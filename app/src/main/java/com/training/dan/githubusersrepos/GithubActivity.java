package com.training.dan.githubusersrepos;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.training.dan.githubusersrepos.Model.Repository;
import com.training.dan.githubusersrepos.Retrofit.GithubRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GithubActivity extends AppCompatActivity {
    private static final String TAG = "GithubActivity";
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private List<Repository> mRepositories;

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
        mRepositories = new ArrayList<>();
        mRecyclerView.setAdapter(new RepositoryAdapter(mRepositories));
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
                                Log.i(TAG,"onResponse "+response.body().size());
                                Toast.makeText(GithubActivity.this,"onResponse",Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                                mRepositories.clear();
                                mRepositories.addAll(response.body());
                                mRecyclerView.getAdapter().notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Call<List<Repository>> call, Throwable t) {
                                Log.i(TAG,"onFailure");
                                Toast.makeText(GithubActivity.this,"onResponse",Toast.LENGTH_SHORT).show();
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

    private class RepositoryHolder extends RecyclerView.ViewHolder{
        private TextView mTextView;

        public RepositoryHolder(View itemView) {
            super(itemView);
            Log.i(TAG,"RepositoryHolder");
            mTextView = (TextView) itemView.findViewById(R.id.text_view);
        }

        public void bindRepository(Repository repository){
            Log.i(TAG,"bindRepository");
            mTextView.setText(repository.getName());
        }
    }

    private class RepositoryAdapter extends RecyclerView.Adapter<RepositoryHolder>{
        private List<Repository> mRepositoryList;

        public RepositoryAdapter(List<Repository> repositories){
            mRepositoryList = repositories;
            Log.i(TAG,"RepositoryAdapter " + mRepositoryList.size());
        }

        @Override
        public RepositoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i(TAG,"onCreateViewHolder");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.repo_item,parent,false);
            return new RepositoryHolder(v);
        }

        @Override
        public void onBindViewHolder(RepositoryHolder holder, int position) {
            Log.i(TAG,"onBindViewHolder");
            holder.bindRepository(mRepositoryList.get(position));
        }

        @Override
        public int getItemCount() {
            return mRepositoryList.size();
        }
    }
}
