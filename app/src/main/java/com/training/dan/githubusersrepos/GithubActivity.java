package com.training.dan.githubusersrepos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;
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
    private Toolbar mToolbar;
    private Drawer mDrawer;
    private ProfileDrawerItem mProfileDrawerItem;
    private AccountHeader mAccountHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github);

        initUI();
    }

    private void initUI() {
        findViews();
        setupRecyclerView();
        setupProgressDialog();
        setupToolbar();
        navigationDrawerCreation();
    }

    private void findViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRepositories = new ArrayList<>();
        mRecyclerView.setAdapter(new RepositoryAdapter(mRepositories));
    }

    private void setupProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Wait please...");
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void navigationDrawerCreation() {
        DrawerImagesInit();
        makeNavigationDrawerAccountHeader();
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(mAccountHeader)
                .withHeader(R.layout.drawer_header)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        InputMethodManager inputMethodManager = (InputMethodManager) GithubActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(GithubActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .build();
    }

    private void makeNavigationDrawerAccountHeader() {
        mAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        mProfileDrawerItem = new ProfileDrawerItem().withName("Danil Buhakov").withEmail("t.com")
                                .withIcon("https://lh3.googleusercontent.com/gN6iBKP1b2GTXZZoCxhyXiYIAh8QJ_8xzlhEK6csyDadA4GdkEdIEy9Bc8s5jozt1g=w300")
                                .withIdentifier(1)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();
    }

    private void DrawerImagesInit(){
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });
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
                GithubRetrofit.getApi().getUsersRepo(query)
                        .enqueue(new Callback<List<Repository>>() {
                            @Override
                            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                                onRetrofitResponse(response);
                            }

                            @Override
                            public void onFailure(Call<List<Repository>> call, Throwable t) {
                                Log.i(TAG, "onFailure");
                                Toast.makeText(GithubActivity.this, "onResponse", Toast.LENGTH_SHORT).show();
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

    private void onRetrofitResponse(Response<List<Repository>> response) {
        Log.i(TAG, "onResponse " + response.body().size());
        Toast.makeText(GithubActivity.this, "onResponse", Toast.LENGTH_SHORT).show();
        mProgressDialog.dismiss();
        mRepositories.clear();
        mRepositories.addAll(response.body());
        mRecyclerView.getAdapter().notifyDataSetChanged();
        IProfile profile = mAccountHeader.getActiveProfile();
        profile.withIcon(response.body().get(0).getOwner().getAvatarUrl());
        mAccountHeader.updateProfile(profile);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private class RepositoryHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private View mItemView;

        public RepositoryHolder(View itemView) {
            super(itemView);
            Log.i(TAG, "RepositoryHolder");
            mItemView = itemView;
            mTextView = (TextView) itemView.findViewById(R.id.text_view);
        }

        public void bindRepository(final Repository repository) {
            Log.i(TAG, "bindRepository");
            mTextView.setText(repository.getName());
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(repository.getHtmlUrl()));
                    startActivity(intent);
                }
            });
        }
    }

    private class RepositoryAdapter extends RecyclerView.Adapter<RepositoryHolder> {
        private List<Repository> mRepositoryList;

        public RepositoryAdapter(List<Repository> repositories) {
            mRepositoryList = repositories;
            Log.i(TAG, "RepositoryAdapter " + mRepositoryList.size());
        }

        @Override
        public RepositoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i(TAG, "onCreateViewHolder");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.repo_item, parent, false);
            return new RepositoryHolder(v);
        }

        @Override
        public void onBindViewHolder(RepositoryHolder holder, int position) {
            Log.i(TAG, "onBindViewHolder");
            holder.bindRepository(mRepositoryList.get(position));
        }

        @Override
        public int getItemCount() {
            return mRepositoryList.size();
        }
    }
}
