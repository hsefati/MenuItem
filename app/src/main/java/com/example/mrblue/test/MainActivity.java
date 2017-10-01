package com.example.mrblue.test;

import android.Manifest;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
        import android.content.pm.PackageManager;
        import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.TextView;
        import java.util.Collections;
        import java.util.List;

public class MainActivity extends ListActivity {
    AppAdapter adapter=null;

    Camera myCamera;
    FrameLayout myLayout;
    CameraSurfaceView CameraSurfaceView;

    private boolean mIsPreviewing;
    private static final int PREVIEW_PAUSE = 500;
    private static final int REQUEST_CAMERA = 0;

    private static final String TAG = "testProject";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager packetManager=getPackageManager();
        Intent mainIntent=new Intent(Intent.ACTION_MAIN, null);

        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> launchableApps = packetManager.queryIntentActivities(mainIntent, 0);

        Collections.sort(launchableApps,
                new ResolveInfo.DisplayNameComparator(packetManager));

        adapter=new AppAdapter(packetManager, launchableApps);
        setListAdapter(adapter);

        if(getCameraPermission()){
            myCamera = getCameraInstance();
        }

        if(myCamera != null)
            initAfterCameraGranted();
    }

    @Override
    protected void onPause() {

        super.onPause();
        // Release camera resources
        releaseCameraResources();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onListItemClick(ListView l, View v,
                                   int position, long id) {
        ResolveInfo launchableApps=adapter.getItem(position);
        ActivityInfo activity=launchableApps.activityInfo;
        ComponentName name=new ComponentName(activity.applicationInfo.packageName,
                activity.name);
        Intent i=new Intent(Intent.ACTION_MAIN);

        i.addCategory(Intent.CATEGORY_LAUNCHER);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        i.setComponent(name);

        startActivity(i);
    }

    private void initAfterCameraGranted(){

        myLayout = (FrameLayout) findViewById(R.id.camera_preview);

        CameraSurfaceView = new CameraSurfaceView(this, myCamera);
        myLayout.addView(CameraSurfaceView);
    }

    public boolean getCameraPermission(){
        // Check for the camera permission. If it is not granted to this app already, it will ask an user for the permission.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA}, REQUEST_CAMERA);

            return false;
        }
        else{
            return true;
        }
    }


    private Camera getCameraInstance(){
        Camera cam_obj = null;

        try {
            cam_obj = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.d(TAG, "Error getting a camera instance: " + e.getMessage());
        }

        Camera.Parameters parameters = cam_obj.getParameters();

        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {

            parameters.set("orientation", "potrait");
            cam_obj.setDisplayOrientation(90);
            parameters.setRotation(90);
        }
        else{
            parameters.set("orientation", "landscape");
            cam_obj.setDisplayOrientation(0);
            parameters.setRotation(0);
        }

        return cam_obj;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed

                myCamera = getCameraInstance();
                initAfterCameraGranted();

                Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
            }
            // END_INCLUDE(permission_result)
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Release camera so other applications can use it.
    private void releaseCameraResources() {
        if (null != myCamera) {
//          myCamera.release();
            myCamera = null;
        }
    }


    ///////////////////////////////////////////////////////////////

    class AppAdapter extends ArrayAdapter<ResolveInfo> {
        private PackageManager pm=null;

        AppAdapter(PackageManager pm, List<ResolveInfo> apps) {
            super(MainActivity.this.getApplicationContext(), R.layout.row, apps);
            this.pm=pm;
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {
            if (convertView==null) {
                convertView=newView(parent);
            }

            bindView(position, convertView);

            return(convertView);
        }

        private View newView(ViewGroup parent) {
            return(getLayoutInflater().inflate(R.layout.row, parent, false));
        }

        private void bindView(int position, View row) {
            TextView label=(TextView)row.findViewById(R.id.label);

            label.setText(getItem(position).loadLabel(pm));

            ImageView icon=(ImageView)row.findViewById(R.id.icon);

            icon.setImageDrawable(getItem(position).loadIcon(pm));
        }
    }
}