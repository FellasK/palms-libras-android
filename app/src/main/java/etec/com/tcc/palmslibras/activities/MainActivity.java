package etec.com.tcc.palmslibras.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.fragments.CourseFragment;
import etec.com.tcc.palmslibras.fragments.HomeFragment;
import etec.com.tcc.palmslibras.fragments.ProfileFragment;
import etec.com.tcc.palmslibras.fragments.RankingFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // Inicia com o HomeFragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_activities) {
                    selectedFragment = new CourseFragment();
                } else if (itemId == R.id.nav_ranking) {
                    selectedFragment = new RankingFragment();
                } else if (itemId == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }
                return true;
            };
}