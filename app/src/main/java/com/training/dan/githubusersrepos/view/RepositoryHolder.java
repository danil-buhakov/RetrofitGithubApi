package com.training.dan.githubusersrepos.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.training.dan.githubusersrepos.Model.Repository;
import com.training.dan.githubusersrepos.R;

import static com.training.dan.githubusersrepos.GithubActivity.TAG;

public class RepositoryHolder extends RecyclerView.ViewHolder{
    private TextView mTextView;

    public RepositoryHolder(View itemView) {
        super(itemView);
        Log.i(TAG,"RepositoryHolder");
        mTextView = (TextView) itemView.findViewById(R.id.text_view);
    }

    public void bindRepository(final Repository repository){
        Log.i(TAG,"bindRepository");
        mTextView.setText(repository.getName());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(repository.getHtmlUrl()));
                itemView.getContext().startActivity(intent);
            }
        });
    }
}
