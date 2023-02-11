package com.uniba.mobile.cddgl.laureapp;

import android.app.MediaRouteButton;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.databinding.ActivityMainBinding;
import com.uniba.mobile.cddgl.laureapp.ui.chat.ChatViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.chat.ChatViewModelFactory;
import com.uniba.mobile.cddgl.laureapp.ui.login.LoginViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.login.LoginViewModelFactory;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    public static final String LOGGED_USER = "logged_user";
    public static final int CHAT = R.id.nav_chat;
    public static final int LOGOUT =  R.id.logout;
    public static final int VISUALIZZA_TASK = R.id.nav_lista_task;
    public static final int NEW_TASK = R.id.nav_new_task;
    public static final int CLASSIFICA_TESI = R.id.nav_classifica_tesi;
    public static final int LISTA_TESI = R.id.nav_lista_tesi;

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private NavigationView navigationView;

    @Nullable
    private LoggedInUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        user = (LoggedInUser) intent.getSerializableExtra(LOGGED_USER);

        if (FirebaseAuth.getInstance().getCurrentUser() == null && user == null) {
            goToLoginActivity();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.topAppBar);

        DrawerLayout drawer = binding.drawerLayout;
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navigationView = binding.navViewMenu;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home,
                R.id.navigation_dashboard, R.id.navigation_notifications)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean isSelected;
                switch(item.getItemId()) {
                    case R.id.nav_gallery:
                        navController.navigate(R.id.nav_gallery);
                        isSelected = true;
                        break;
                    case NEW_TASK:
                        navController.navigate(R.id.nav_new_task);
                        isSelected = true;
                        break;
                    case VISUALIZZA_TASK:
                        navController.navigate(R.id.nav_lista_task);
                        isSelected = true;
                        break;
                    case CLASSIFICA_TESI:
                        navController.navigate(R.id.nav_classifica_tesi);
                        isSelected = true;
                        break;
                    case LISTA_TESI:
                        navController.navigate(R.id.nav_lista_tesi);
                        isSelected = true;
                        break;
                    case CHAT:
                        navController.navigate(R.id.chatListFragment);
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

    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser == null) {
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.display_name_text_view)).setText(user.getDisplayName());
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email_text_view)).setText(user.getEmail());

            return;
        }

        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.display_name_text_view)).setText(firebaseUser.getDisplayName());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email_text_view)).setText(firebaseUser.getEmail());
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        goToLoginActivity();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}