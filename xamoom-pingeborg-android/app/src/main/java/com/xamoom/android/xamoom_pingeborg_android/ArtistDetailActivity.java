package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.ContentBlocks.ContentBlock;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType0;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType3;
import com.xamoom.android.mapping.ContentById;
import com.xamoom.android.mapping.ContentByLocationIdentifier;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;

import java.util.List;

import retrofit.RetrofitError;


public class ArtistDetailActivity extends ActionBarActivity implements XamoomContentFragment.OnXamoomContentFragmentInteractionListener {

    private ProgressBar mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //actionbar
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(Global.getInstance().getCurrentSystemName());

        //get contentId or locationIdentifier from intent
        Intent myIntent = getIntent();
        String contentId = myIntent.getStringExtra(XamoomContentFragment.XAMOOM_CONTENT_ID);
        final String locationIdentifier= myIntent.getStringExtra(XamoomContentFragment.XAMOOM_LOCATION_IDENTIFIER);

        mProgressbar = (ProgressBar) findViewById(R.id.artistDetailLoadingIndicator);

        //load data
        if (contentId != null) {
            if(Global.getInstance().getSavedArtists().contains(contentId)) {
                Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with contentId: " + contentId);
                XamoomEndUserApi.getInstance(this.getApplicationContext()).getContentbyIdFull(contentId, false, false, null, true, new APICallback<ContentById>() { //TODO: Check if full o
                    @Override
                    public void finished(ContentById result) {
                        //create title and titleImage from content & add them to contentBlocks
                        ContentBlockType0 cb0 = new ContentBlockType0(result.getContent().getTitle(), true, 0, result.getContent().getDescriptionOfContent());
                        result.getContent().getContentBlocks().add(0, cb0);
                        ContentBlockType3 cb3 = new ContentBlockType3(null, true, 3, result.getContent().getImagePublicUrl());
                        result.getContent().getContentBlocks().add(1, cb3);

                        setupXamoomContentFrameLayout(result.getContent().getContentBlocks());
                    }

                    @Override
                    public void error(RetrofitError error) {
                        Log.e(Global.DEBUG_TAG, "Error:" + error);
                    }
                });
            } else {
                Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with contentId: " + contentId);
                XamoomEndUserApi.getInstance(this.getApplicationContext()).getContentbyIdFull(contentId, false, false, null, false, new APICallback<ContentById>() {
                    @Override
                    public void finished(ContentById result) {
                        //create title and titleImage from content & add them to contentBlocks
                        ContentBlockType0 cb0 = new ContentBlockType0(result.getContent().getTitle(), true, 0, result.getContent().getDescriptionOfContent());
                        result.getContent().getContentBlocks().add(0, cb0);
                        ContentBlockType3 cb3 = new ContentBlockType3(null, true, 3, result.getContent().getImagePublicUrl());
                        result.getContent().getContentBlocks().add(1, cb3);

                        setupXamoomContentFrameLayout(result.getContent().getContentBlocks());
                    }

                    @Override
                    public void error(RetrofitError error) {
                        Log.e(Global.DEBUG_TAG, "Error:" + error);
                    }
                });
            }
        } else if (locationIdentifier != null) {
            Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with locationIdentifier: " + locationIdentifier);
            XamoomEndUserApi.getInstance(this.getApplicationContext()).getContentByLocationIdentifier(locationIdentifier, false, false, null, new APICallback<ContentByLocationIdentifier>() {
                @Override
                public void finished(ContentByLocationIdentifier result) {

                    //create title and titleImage from content & add them to contentBlocks
                    ContentBlockType0 cb0 = new ContentBlockType0(result.getContent().getTitle(), true, 0, result.getContent().getDescriptionOfContent());
                    result.getContent().getContentBlocks().add(0, cb0);
                    ContentBlockType3 cb3 = new ContentBlockType3(null, true, 3, result.getContent().getImagePublicUrl());
                    result.getContent().getContentBlocks().add(1, cb3);

                    setupXamoomContentFrameLayout(result.getContent().getContentBlocks());
                }

                @Override
                public void error(RetrofitError error) {
                    Log.e(Global.DEBUG_TAG, "Error:" + error);
                }
            });
        } else {
            Log.w(Global.DEBUG_TAG, "There is no contentId or locationIdentifier");
            finish();
        }
    }

    private void setupXamoomContentFrameLayout(List<ContentBlock> contentBlocks) {
        //hide loading indicator
        mProgressbar.setVisibility(View.GONE);

        XamoomContentFragment fragment = XamoomContentFragment.newInstance(Global.YOUTUBE_API_KEY, Integer.toHexString(getResources().getColor(R.color.pingeborg_green)).substring(2));
        fragment.setContentBlocks(contentBlocks);

        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.XamoomContentFrameLayout, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artist_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void clickedContentBlock(String contentId) {
        //also discover this artist
        Global.getInstance().saveArtist(contentId);

        Intent intent = new Intent(ArtistDetailActivity.this, ArtistDetailActivity.class);
        intent.putExtra(XamoomContentFragment.XAMOOM_CONTENT_ID,contentId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
