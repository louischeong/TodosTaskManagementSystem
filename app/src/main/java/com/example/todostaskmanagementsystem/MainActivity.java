package com.example.todostaskmanagementsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        DrawerLayout drawer_layout = findViewById(R.id.drawer_layout);
        NavigationView nav_view = findViewById(R.id.nav_view);

        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).setOpenableLayout(drawer_layout).build();
        this.setSupportActionBar(myToolbar);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(nav_view, navController);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setNavigationViewListener();

        //getFCMToken();

//        SharedPreferences prefs = getSharedPreferences("user_details", Context.MODE_PRIVATE);
//        String token = prefs.getString("pref_token", null);
//        if (token != null) {
//            String email = prefs.getString("pref_email", null);
//            db.collection("Users").document(email).update("token", token).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void unused) {
//                    Log.d("MYDEBUG", "Firestore: Updated Token");
//                }
//            });
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_app_bar, menu);

        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_myTodolists:
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToMyTodolistsFragment());
                break;
            case R.id.nav_myInbox:
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToMyInboxFragment());
                break;
            case R.id.nav_myProfile:
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment());
                break;
            default:
                return true;
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:

                SharedPreferences prefs = getSharedPreferences("user_details", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                db.collection("Users").document(prefs.getString("pref_email", null)).update("token", null);
                editor.clear();
                editor.commit();
                SharedPreferences prefsRecentAccess = getSharedPreferences("recent_accessed", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorRecentAccess = prefsRecentAccess.edit();
                editorRecentAccess.clear();
                editorRecentAccess.commit();

                Intent myIntent = new Intent(this, LoginActivity.class);
                startActivity(myIntent);
                this.finish();
            default:
        }
        return super.onOptionsItemSelected(item);
    }

}