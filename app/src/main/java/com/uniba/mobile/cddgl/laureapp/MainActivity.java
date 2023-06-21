package com.uniba.mobile.cddgl.laureapp;

import static android.os.Environment.getExternalStoragePublicDirectory;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.databinding.ActivityMainBinding;
import com.uniba.mobile.cddgl.laureapp.util.ShareContent;

import java.io.File;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    public static final String LOGGED_USER = "logged_user";
    public static final int CHAT = R.id.nav_chat;
    public static final int TICKET = R.id.nav_ticket;
    public static final int BOOKING = R.id.nav_booking;
    public static final int LOGOUT = R.id.logout;
    public static final int MEETING = R.id.nav_meeting;
    public static final int LISTA_TASK = R.id.nav_lista_task;
    public static final int CLASSIFICA_TESI = R.id.nav_classifica_tesi;
    public static final int LISTA_TESI = R.id.navigation_lista_tesi;
    public static final int SETTINGS = R.id.nav_settings;

    public static final int REQUEST_WRITE_STORAGE_PERMISSION = 1;
    public static final int REQUEST_INTERNET_PERMISSION = 2;
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 3;
    public static final int REQUEST_RECEIVE_PERMISSION = 4;

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private NavigationView navigationView;
    private MainViewModel mainViewModel;
    private FirebaseAuth auth;
    private BroadcastReceiver downloadReceiver;
    private ShareContent shareContent;
    private BottomNavigationView navView;

    @Nullable
    private LoggedInUser user;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        applySettings();
    }

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
            navView = findViewById(R.id.nav_view);
            navigationView = binding.navViewMenu;

            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home,
                    LISTA_TESI, CLASSIFICA_TESI, R.id.navigation_profile)
                    .setOpenableLayout(drawer)
                    .build();

            navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);

            //Menu di navigazione con tutte le destinazioni
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    boolean isSelected = true;
                    switch (item.getItemId()) {
                        case LISTA_TASK:
                            navController.navigate(R.id.nav_lista_task);
                            break;
                        case CLASSIFICA_TESI:
                            navController.navigate(R.id.nav_classifica_tesi);
                            break;
                        case CHAT:
                            navController.navigate(R.id.nav_chat_list_fragment);
                            break;
                        case TICKET:
                            navController.navigate(R.id.nav_ticket);
                            break;
                        case BOOKING:
                            navController.navigate(R.id.nav_bookingListFragment);
                            break;
                        case LOGOUT:
                            logout();
                            break;
                        case MEETING:
                            navController.navigate(R.id.calendario);
                            break;
                        case SETTINGS:
                            navController.navigate(R.id.fragment_settings);
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

        } catch (NullPointerException e) {
            Log.w("Main Activity", e.getMessage());
        }

        shareContent = new ShareContent(getApplicationContext());

        try {
            downloadReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                    if (mainViewModel.getDownloadReference() != null &&
                            mainViewModel.getDownloadReference() == referenceId &&
                            mainViewModel.getFileToOpen() != null) {

                        Intent visualizeFile = shareContent.viewFileDownloaded(mainViewModel.getFileToOpen());
                        startActivity(visualizeFile);

                        mainViewModel.setFileToOpen(null);
                        mainViewModel.setDownloadReference(null);
                    }

                }
            };
        } catch (Exception e) {
            Log.e("Main Activity", e.getMessage());
        }

        mainViewModel.getUser().observe(this, loggedInUser -> {

            if(loggedInUser.getRole().equals(RoleUser.PROFESSOR)) {
                navView.getMenu().findItem(CLASSIFICA_TESI).setVisible(false);
            } else if(RoleUser.GUEST.equals(loggedInUser.getRole())) {
                navigationView.getMenu().findItem(LISTA_TASK).setVisible(false);
                navigationView.getMenu().findItem(CHAT).setVisible(false);
                navigationView.getMenu().findItem(TICKET).setVisible(false);
                navigationView.getMenu().findItem(BOOKING).setVisible(false);
                navigationView.getMenu().findItem(MEETING).setVisible(false);
            }

            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.display_name_text_view)).setText(loggedInUser.getDisplayName());
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email_text_view)).setText(loggedInUser.getEmail());

            try {
                if (loggedInUser.getPhotoUrl() != null) {
                    Glide.with(this).load(loggedInUser.getPhotoUrl())
                            .apply(new RequestOptions()
                                    .placeholder(R.mipmap.ic_user_round)
                                    .error(R.mipmap.ic_user_round)
                                    .transform(new CircleCrop())
                                    .skipMemoryCache(true))
                            .into((ImageView) navigationView.getHeaderView(0).findViewById(R.id.navigation_header_image_view));
                }
            } catch (RuntimeException e) {
                Log.e("Main Activity", e.getMessage());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = super.onCreateView(name, context, attrs);

        //only for the first time
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean permissionRequestedBefore = sharedPref.getBoolean("permission_requested_before", false);
        // Permission is not granted and has not been requested before
        // Request the permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (!permissionRequestedBefore && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_RECEIVE_PERMISSION);
            }

            // Store the flag indicating the permission has been requested
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("permission_requested_before", true);
            editor.apply();

        } else {
            if (!permissionRequestedBefore && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY},
                        REQUEST_RECEIVE_PERMISSION);
            }

            // Store the flag indicating the permission has been requested
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("permission_requested_before", true);
            editor.apply();
        }

        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadReceiver);
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

    public void goToLoginActivity() {
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

    public static File getExternalStorageDirectory(String directoryPictures) {
        return getExternalStoragePublicDirectory(directoryPictures);
    }

    private void applySettings() {

        String systemLanguage = getResources().getConfiguration().getLocales().get(0).getLanguage();
        int systemTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        String themeDefault = "light";
        if(systemTheme == Configuration.UI_MODE_NIGHT_YES) {
            themeDefault = "night";
        }

        // Get the saved values from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String defaultTheme = sharedPreferences.getString("theme_enabled", themeDefault);
        String defaultLanguage = sharedPreferences.getString("language", systemLanguage);

        // Apply the saved settings to your app
        applyLanguage(defaultLanguage);
        applyTheme(defaultTheme);
    }

    private void applyTheme(String theme) {
        if (theme.equals("night")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (theme.equals("light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void applyLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}