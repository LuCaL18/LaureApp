package com.uniba.mobile.cddgl.laureapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    public static final String LOGGED_USER = "logged_user";
    public static final int CHAT = R.id.nav_chat;
    public static final int TICKET = R.id.nav_ticket;
    public static final int LOGOUT = R.id.logout;

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private NavigationView navigationView;
    private MainViewModel mainViewModel;
    private FirebaseAuth auth;

    @Nullable
    private LoggedInUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {

            auth = FirebaseAuth.getInstance();

            Intent intent = getIntent();
            user = (LoggedInUser) intent.getSerializableExtra(LOGGED_USER);

            if (auth.getCurrentUser() == null && user == null) {
                goToLoginActivity();
            }


            setSupportActionBar(binding.appBarMain.topAppBar);
            initViewModel();

            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
            FirebaseMessaging.getInstance().subscribeToTopic("notifications");

            DrawerLayout drawer = binding.drawerLayout;
            BottomNavigationView navView = findViewById(R.id.nav_view);
            navigationView = binding.navViewMenu;

            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home,
                    R.id.navigation_dashboard)
                    .setOpenableLayout(drawer)
                    .build();

            navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    boolean isSelected;
                    switch (item.getItemId()) {
                        case R.id.nav_gallery:
                            navController.navigate(R.id.nav_gallery);
                            isSelected = true;
                            break;
                        case CHAT:
                            navController.navigate(R.id.nav_chat_list_fragment);
                            isSelected = true;
                            break;
                        case TICKET:
                            navController.navigate(R.id.nav_ticket);
                            isSelected = true;
                            break;
                        case LOGOUT:
                            logout();
                            isSelected = true;
                            break;
                        default:
                            isSelected = false;
                    }
                    drawer.closeDrawer(GravityCompat.START);
                    return isSelected;
                }
            });

            if (user != null) {

                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.display_name_text_view)).setText(user.getDisplayName());
                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email_text_view)).setText(user.getEmail());

            } else {
                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.display_name_text_view)).setText(auth.getCurrentUser().getDisplayName());
                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email_text_view)).setText(auth.getCurrentUser().getEmail());
            }

            mainViewModel.getUser().observe(this, loggedInUser -> {
                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.display_name_text_view)).setText(loggedInUser.getDisplayName());
                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email_text_view)).setText(loggedInUser.getEmail());

                try {
                    if (loggedInUser.getPhotoUrl() != null) {
                        Glide.with(this).load(loggedInUser.getPhotoUrl()).transform(new CircleCrop()).into((ImageView) navigationView.findViewById(R.id.navigation_header_image_view));
                    }
                } catch (RuntimeException e) {
                    Log.e("Main Activity", "Unable set image profile user");
                }
            });

        } catch (NullPointerException e) {
            Log.w("Main Activity", e.getMessage());
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logout() {
        auth.signOut();
        FirebaseMessaging.getInstance().deleteToken();
        goToLoginActivity();
    }

    @Nullable
    public LoggedInUser getUser() {
        return user;
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void initViewModel() {
        try {
            ViewModelProvider modelProvider = new ViewModelProvider(this);
            mainViewModel = modelProvider.get(MainViewModel.class);

            if (user != null) {
                mainViewModel.init(user);
                return;
            }

            mainViewModel.init(new LoggedInUser(auth.getCurrentUser().getUid()));
        } catch (Exception e) {
            Log.e("Main Activity", e.getMessage());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}