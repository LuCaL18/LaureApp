package it.uniba.dib.sms222327.laureapp.ui.settings;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import it.uniba.dib.sms222327.laureapp.MainActivity;
import it.uniba.dib.sms222327.laureapp.R;

import java.util.Locale;

/**
 * Fragment che si occupa della visualizzazione e della gestione della schermata delle Impostazioni
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private ListPreference themePreference;
    private ListPreference languagePreference;
    private BottomNavigationView navBar;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        themePreference = findPreference("theme");
        if (themePreference != null) {
            themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue instanceof String) {
                    String themeValue = (String) newValue;

                    // Save the new value to SharedPreferences
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("theme_enabled", themeValue);
                    editor.apply();

                    applyTheme(themeValue);
                }
                return true;
            });
        }

        languagePreference = findPreference("language");
        languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            String language = (String) newValue;

            // Save the new value to SharedPreferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("language", language);
            editor.apply();

            // Apply the new language
            applyLanguage(language);
            return true;
        });


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView rv = getListView(); // This holds the PreferenceScreen's items
        // Set the top margin (adjust the value as needed)

        int actionBarHeight = 0;
        // Get the height of the action bar
        TypedValue typedValue = new TypedValue();
        if (getContext().getTheme().resolveAttribute(com.google.android.material.R.attr.actionBarSize, typedValue, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
        }

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) rv.getLayoutParams();
        layoutParams.topMargin = actionBarHeight;
        rv.setLayoutParams(layoutParams);

        String systemLanguage = getResources().getConfiguration().getLocales().get(0).getLanguage();
        int systemTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        String themeDefault = "light";
        if (systemTheme == Configuration.UI_MODE_NIGHT_YES) {
            themeDefault = "night";
        }

        // Get the saved values from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String theme = sharedPreferences.getString("theme_enabled", themeDefault);
        String language = sharedPreferences.getString("language", systemLanguage);

        themePreference.setValue(theme);
        themePreference.setSummary(themeDefault);
        languagePreference.setValue(language);
        languagePreference.setSummary(language);
    }

    private void applyTheme(String theme) {
        if (theme.equals("night")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void applyLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);

        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, displayMetrics);

        // Restart the activity to apply the language change
        requireActivity().recreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        navBar.setVisibility(View.VISIBLE);
        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(MainActivity.SETTINGS).setChecked(false);
        navBar = null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }
}
