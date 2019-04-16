package dev.marshall.visualsearch.visualSearch;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import dev.marshall.visualsearch.R;


public class VisualSearchFragment extends Fragment {
    private CameraKitView cameraKitView;

    private long captureStartTime;
    private boolean capturingVideo;



    public VisualSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_visual_search, container, false);
        cameraKitView = view.findViewById(R.id.camera);
        ImageView photo_library = view.findViewById(R.id.photo_library);
        ImageView flashButton = view.findViewById(R.id.flashButton);
        ImageView captureImageButton = view.findViewById(R.id.captureImageButton);

        flashButton.setOnTouchListener(onTouchFlash);
        photo_library.setOnTouchListener(onTouchLibrary);
        captureImageButton.setOnTouchListener(onTouchCaptureImage);

        showResultDialog();

        return view;
    }

    private void showResultDialog() {


    }

    @Override
    public void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    public void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private View.OnTouchListener onTouchCaptureImage = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    handleViewTouchFeedback(view, motionEvent);
                    captureStartTime = System.currentTimeMillis();
                    cameraKitView.captureImage((cameraKitView, photo) -> {

                        // TODO:crop image and send it to the network

                        BottomSheetResult bottomSheetResult=new BottomSheetResult();
                        bottomSheetResult.show(getChildFragmentManager(),bottomSheetResult.getTag());


                    });
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    handleViewTouchFeedback(view, motionEvent);
                    break;
                }
            }
            return true;
        }
    };

    private View.OnTouchListener onTouchLibrary =new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP: {
                    handleViewTouchFeedback(v,event);
                    //get image from gallery

                    break;
                }
            }
            return true;
        }
    };
    private View.OnTouchListener onTouchFlash = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_UP: {
                    handleViewTouchFeedback(view,motionEvent);
                    if (cameraKitView.getFlash() == CameraKit.FLASH_OFF) {
                        cameraKitView.setFlash(CameraKit.FLASH_ON);
                        changeViewImageResource((ImageView) view, R.drawable.ic_flash_on);
                    } else {
                        cameraKitView.setFlash(CameraKit.FLASH_OFF);
                        changeViewImageResource((ImageView) view, R.drawable.ic_flash_off);
                    }

                    break;
                }
            }
            return true;
        }
    };

    private boolean handleViewTouchFeedback(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchDownAnimation(view);
                return true;
            }

            case MotionEvent.ACTION_UP: {
                touchUpAnimation(view);
                return true;
            }

            default: {
                return true;
            }
        }
    }

    private void touchDownAnimation(View view) {
        view.animate()
                .scaleX(0.88f)
                .scaleY(0.88f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    private void touchUpAnimation(View view) {
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    private void changeViewImageResource(final ImageView imageView, @DrawableRes final int resId) {
        imageView.setRotation(0);
        imageView.animate()
                .rotationBy(360)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator())
                .start();

        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(resId);
            }
        }, 120);
    }
}
