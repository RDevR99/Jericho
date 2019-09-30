package com.example.mad_assignment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialized the navigation View.
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);

        //Lets assume that the below is the data we get from the server.
        ArrayList<Integer> minutes = new ArrayList<>();
        minutes.add(41);
        minutes.add(42);
        minutes.add(43);
        minutes.add(44);
        minutes.add(50);

        Calendar[] calendars = new Calendar[minutes.size()];

        AlarmManager[] alarmManagers = new AlarmManager[minutes.size()];

        ArrayList<PendingIntent> intentArray = new ArrayList<>();

        for(int i=0; i<minutes.size(); i++)
        {
            calendars[i] = Calendar.getInstance();
            calendars[i].set(Calendar.HOUR_OF_DAY, 23);
            calendars[i].set(Calendar.MINUTE, minutes.get(i));

            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.setAction("MY_NOTIFICATION_MESSAGE");

            intent.putExtra("requestCode", i);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManagers[i] = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarmManagers[i].setExact(AlarmManager.RTC_WAKEUP, calendars[i].getTimeInMillis(), pendingIntent);

            intentArray.add(pendingIntent);

        }

        loadFragment(new HomeFragment());
    }

    private boolean loadFragment(Fragment fragment){

        if(fragment != null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }

        return false;

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment fragment = null;

        switch(menuItem.getItemId()){

            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;
            case R.id.navigation_search:
                fragment = new SearchFragment();
                break;
            case R.id.navigation_settings:
                fragment = new SettingsFragment();
                break;
        }

        return loadFragment(fragment);
    }
}
