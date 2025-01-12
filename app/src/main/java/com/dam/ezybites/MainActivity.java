package com.dam.ezybites;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.dam.ezybites.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //TOOLBAR
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);


        NavigationUI.setupWithNavController(binding.navView, navController);

        updateIcons(navView, R.id.navigation_home);

        // Cambiar íconos dinámicamente según el fragmento seleccionado
        navView.setOnItemSelectedListener(item -> {
            int selectedItemId = item.getItemId();

            // Cambiar íconos para cada elemento basado en el patrón de nombres
            updateIcons(navView, selectedItemId);

            // Manejar la navegación predeterminada
            NavigationUI.onNavDestinationSelected(item, navController);

            return true;
        });
    }

    private void updateIcons(BottomNavigationView navView, int selectedItemId) {
        int[] menuItems = new int[]{
                R.id.navigation_home,
                R.id.navigation_search,
                R.id.navigation_discover,
                R.id.navigation_community,
                R.id.navigation_recipes
        };

        String[] iconNames = new String[]{
                "home",
                "search",
                "discover",
                "community",
                "recipes"
        };

        for (int i = 0; i < menuItems.length; i++) {
            int iconResId = getResources().getIdentifier(
                    selectedItemId == menuItems[i] ? "svg_" + iconNames[i] + "_selected" : "svg_" + iconNames[i],
                    "drawable",
                    getPackageName()
            );

            navView.getMenu().findItem(menuItems[i]).setIcon(iconResId);
        }
    }
}