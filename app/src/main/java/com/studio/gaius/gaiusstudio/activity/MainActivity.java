package com.studio.gaius.gaiusstudio.activity;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.studio.gaius.gaiusstudio.R;
import com.studio.gaius.gaiusstudio.TextImageView;
import com.studio.gaius.gaiusstudio.utils.CameraTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.iv_pic)
    ImageView ivPic;

    @BindView(R.id.tv_spinner)
    TextImageView tvSpinner;

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d("yang", "onCreate: this is a test github");
    }

    private void takePhoto() {
        CameraTool cameraTool = new CameraTool();
        Camera camera = cameraTool.getCameraInstance();
        cameraTool.generateDefaultParams(camera);
        cameraTool.tackPicture(camera);
    }

    @OnClick({R.id.btn_pic, R.id.tv_spinner})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_pic:
                takePhoto();
                break;
            case R.id.tv_spinner:
                animateArrow();
                break;
        }
    }

    private void animateArrow() {
        tvSpinner.setAnimationRight(getApplicationContext(), tvSpinner, true);
        //tvSpinner.setArrowRotate(tvSpinner);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
