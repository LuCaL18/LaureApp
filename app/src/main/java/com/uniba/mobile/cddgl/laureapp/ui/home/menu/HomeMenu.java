package com.uniba.mobile.cddgl.laureapp.ui.home.menu;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.ui.home.HomeFragment;


public class HomeMenu implements MenuProvider {

    private final MutableLiveData<Menu> menu = new MutableLiveData<>();
    private final NavController navController;

    public HomeMenu(NavController navController) {
        this.navController = navController;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.app_bar_home, menu);
        this.menu.setValue(menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case HomeFragment.NOTIFICATION_APP_BAR: {
                navController.navigate(R.id.action_navigation_home_to_navigation_notifications);
                return true;
            }
            case R.id.favorite: {
                navController.navigate(R.id.visualizeTesiFragment);
                return true;
            }
            case R.id.crea: {
                navController.navigate(R.id.action_navigation_home_to_tesiFragmant);
            }
            default:
                return false;
        }
    }

    public void setBadgeIcon(Integer count) {

        Menu menu = this.menu.getValue();
        try {

            MenuItem item = menu.findItem(HomeFragment.NOTIFICATION_APP_BAR);
            View actionView = item.getActionView();

            actionView.setOnClickListener(v -> onMenuItemSelected(item));


            TextView badge = actionView.findViewById(R.id.notification_badge);

            if(count > 0 && count < 100) {
                badge.setVisibility(View.VISIBLE);
                badge.setText(String.valueOf(count));
                return;
            } else if(count > 100) {

                badge.setVisibility(View.VISIBLE);
                badge.setText("99+");
                return;
            }

            badge.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e("HomeMenu", "Error in set badge icon menu");
        }
    }

    public MutableLiveData<Menu> getMenu() {
        return menu;
    }
}
