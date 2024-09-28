
import android.widget.Toast;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;



public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private AccessControl accessControl;


import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.detections.DetectionsActivity;
import com.example.smishingdetectionapp.notifications.NotificationPermissionDialogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;


    public MainActivity() {
        super();
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (!areNotificationsEnabled()) {
            showNotificationPermissionDialog();
        }


        BottomNavigationView nav = findViewById(R.id.bottom_navigation); //variable assignment
        nav.setSelectedItemId(R.id.nav_home); //home page selected by default
        nav.setOnItemSelectedListener(menuItem -> { //selected item listener


                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            }
            return false;
        });


        Button debug_btn = findViewById(R.id.debug_btn);
        debug_btn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DebugActivity.class)));


        Button detections_btn = findViewById(R.id.detections_btn);
        detections_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, DetectionsActivity.class));
            finish();
        });


        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private void showNotificationPermissionDialog() {
        NotificationPermissionDialogFragment dialogFragment = new NotificationPermissionDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "notificationPermission");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

