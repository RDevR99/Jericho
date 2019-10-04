package com.example.mad_assignment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navView;
    ViewPager viewPager;

    public static void setupFm(FragmentManager fragmentManager, ViewPager viewPager){
        FragmentAdapter Adapter = new FragmentAdapter(fragmentManager);
        //Add All Fragment To List
        Adapter.add(new HomeFragment(), "Home Page");
        Adapter.add(new SearchFragment(), "Search Page");
        Adapter.add(new SettingsFragment(), "Settings Page");
        viewPager.setAdapter(Adapter);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_search:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_settings:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialized the navigation View.
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewPager = findViewById(R.id.viewPager);
        setupFm(getSupportFragmentManager(), viewPager);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new PageChange());



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

    }




    public class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    navView.setSelectedItemId(R.id.navigation_home);
                    break;
                case 1:
                    navView.setSelectedItemId(R.id.navigation_search);
                    break;
                case 2:
                    navView.setSelectedItemId(R.id.navigation_settings);
                    break;
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
