package com.example.daniel.manoge3;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import helper.DatabaseHelper;
import model.Exercise;
import model.Rep;
import model.Routine;
import model.RoutineExercise;
import model.Weight;

public class DatabaseActivity extends AppCompatActivity {
    private static final String TAG = "DatabaseActivity";
    private static final int REQUEST_WRITE_STORAGE = 112;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    DatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        dbh = DatabaseHelper.getInstance(this);

        // backup database
        FloatingActionButton backupDatabaseFab = findViewById(R.id.backupDatabaseFab);
        backupDatabaseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermission();

                Log.d(TAG, "onClick: " + isExternalStorageWritable());
                Log.d(TAG, "onClick: " + isExternalStorageReadable());
                //copyDatabase("manogeDatabase");

                printDatabaseString(getDatabaseAsString());
                
            }
        });
    }

    public void askPermission() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "askPermission: PERMISSION DENIED");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Permission to access the SD-CARD is required for this app").setTitle("Permission required");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: Clicked");
                        makeRequest();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                makeRequest();
            }
        }
    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Permission denied by user");
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Permission granted by user");
                }
                return;
            }
        }
    }

    public void printDatabaseString(String data){
        try {
            Log.d(TAG, "copyDatabase: start copy");
            File sd = Environment.getExternalStorageDirectory();
            Log.d(TAG, "copyDatabase: sd: " + sd);
            Log.d(TAG, "copyDatabase: sd exists: " + sd.exists());
            Log.d(TAG, "copyDatabase: mountable: " + Environment.getExternalStorageState());
            Log.d(TAG, "copyDatabase: write: " + sd.canWrite());
            Log.d(TAG, "copyDatabase: read: " + sd.canRead());

            if(sd.canWrite()) {
                Log.d(TAG, "copyDatabase: create file");
                String backupDBPath = "manoge3DatabaseBackup.txt";
                File backupDB = new File(sd, backupDBPath);
                Log.d(TAG, "copyDatabase: backupDB: " + backupDB);

                Log.d(TAG, "copyDatabase: starting copying");

                backupDB.createNewFile();
                FileOutputStream fOut = new FileOutputStream(backupDB);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(data);

                myOutWriter.close();

                fOut.flush();
                fOut.close();

                Log.d(TAG, "copyDatabase: done copying");

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(backupDB)));
                Log.d(TAG, "copyDatabase: media scanned");

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // this doesn't work for some reason so used getDatabaseAsString to just print database instead
    public void copyDatabase(String databaseName){
        try {
            Log.d(TAG, "copyDatabase: start copy");
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            Log.d(TAG, "copyDatabase: sd: " + sd);
            Log.d(TAG, "copyDatabase: sd exists: " + sd.exists());
            Log.d(TAG, "copyDatabase: mountable: " + Environment.getExternalStorageState());
            Log.d(TAG, "copyDatabase: write: " + sd.canWrite());
            Log.d(TAG, "copyDatabase: read: " + sd.canRead());

            if(sd.canWrite()) {
                Log.d(TAG, "copyDatabase: create files");
                String currentDBPath = "//data//" + getPackageName() +"//databases//" + databaseName + "";
                String backupDBPath = "manoge3DatabaseBackup.txt";
                File currentDB = new File(data, currentDBPath);
                Log.d(TAG, "copyDatabase: currentDB: " + currentDB);
                File backupDB = new File(sd, backupDBPath);
                Log.d(TAG, "copyDatabase: backupDB: " + backupDB);

                if (currentDB.exists()){
                    Log.d(TAG, "copyDatabase: starting copying");
                    InputStream in = new FileInputStream(currentDB);
                    OutputStream out = new FileOutputStream(backupDB);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }

                    out.close();
                    in.close();
                    Log.d(TAG, "copyDatabase: done copying");

                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(backupDB)));
                    Log.d(TAG, "copyDatabase: media scanned");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // this is improper but copyDatabase isn't working and i've already spent too much time on this
    public String getDatabaseAsString() {
        Log.d(TAG, "printDatabaseToFile: begin print");
        String weightString = String.format("Table WEIGHTS:\n");
        String exerciseString = "Table EXERCISE:\n";
        String repString = "Table REP:\n";
        String routineString = "Table ROUTINE:\n";
        String routineRelString = "Table ROUTINE REL:\n";

        List<Weight> allWeights = dbh.getAllWeights();
        for (Weight weight: allWeights){
            weightString += weight.toString() + "\n";
        }

        List<Exercise> allExercise = dbh.getAllExercises();
        for (Exercise exercise: allExercise){
            exerciseString += exercise.toString() + "\n";
        }

        List<Rep> allReps = dbh.getAllReps();
        for (Rep reps: allReps){
            repString += reps.toString() + "\n";
        }

        List<Routine> allRoutines = dbh.getAllRoutines();
        for (Routine routines : allRoutines){
            routineString += routines.toString() + "\n";
        }

        List<RoutineExercise> allRoutineRels = dbh.getAllPairs();
        for (RoutineExercise pairs : allRoutineRels){
            routineRelString += pairs.toString() + "\n";
        }

        return weightString +
                "\n----------\n" +
                exerciseString +
                "\n----------\n" +
                repString +
                "\n----------\n" +
                routineString +
                "\n----------\n" +
                routineRelString;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DatabaseTab1Weights(), "Weights");
        adapter.addFragment(new DatabaseTab2Exercise(), "Exercises");
        adapter.addFragment(new DatabaseTab3Routine(), "Routines");
        adapter.addFragment(new DatabaseTab4Rep(), "Reps");
        adapter.addFragment(new DatabaseTab5RoutinePair(), "Pairs");

        viewPager.setAdapter(adapter);
    }

    // ----------------
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
