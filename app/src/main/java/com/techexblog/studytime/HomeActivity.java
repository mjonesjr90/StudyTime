package com.techexblog.studytime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_INVITE = 1;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    //private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;
    private ProgressBar mProgressBar;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAnalytics mFirebaseAnalytics;
    private EditText mMessageEditText;
    //private AdView mAdView;
    //private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private GoogleApiClient mGoogleApiClient;

    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private RecyclerView mRecyclerView;
    private RecyclerView uRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter uAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.LayoutManager uLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = ANONYMOUS;

        //Setup app bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Initiatlize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        //If not signed in, launch the loginActivity
        if(mFirebaseUser == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        else{
            mUsername = mFirebaseUser.getDisplayName();
            if(mFirebaseUser.getPhotoUrl() != null){
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        int numUpcoming = 2;
        int numRecent = 1;
        //Create upcoming cards
        //If the user has scheduled groups show them
        uRecyclerView = (RecyclerView) findViewById(R.id.upcoming_recycler_view);
        uRecyclerView.setHasFixedSize(true);
        uLayoutManager = new LinearLayoutManager(this);
        uRecyclerView.setLayoutManager(uLayoutManager);
        uAdapter = new MyRecyclerViewAdapter(getDataSet(numUpcoming));
        uRecyclerView.setAdapter(uAdapter);

        //Create recent cards
        mRecyclerView = (RecyclerView) findViewById(R.id.group_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet(numRecent));
        mRecyclerView.setAdapter(mAdapter);

        // Code to Add an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);
    }

    @Override
    protected  void onResume(){
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(TAG, " Clicked on Item " + position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                //Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.settings_menu:
                Log.i(TAG, "Refresh menu item selected");;
                mAdapter.notifyDataSetChanged();
                uAdapter.notifyDataSetChanged();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //This should show last XX groups created
    private ArrayList<GroupDataObject> getDataSet(int num) {
        ArrayList results = new ArrayList<GroupDataObject>();
        for (int index = 0; index < num; index++) {
            GroupDataObject obj = new GroupDataObject("Group Name" + index,
                    "Topic" + index, "Date");
            results.add(index, obj);
        }
        return results;
    }

    public void createNewGroup(View v){
        Intent newGroupIntent = new Intent(getApplicationContext(), NewGroupActivity.class);
        startActivity(newGroupIntent);
    }
}
