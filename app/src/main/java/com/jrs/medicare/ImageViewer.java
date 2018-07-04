package com.jrs.medicare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.Calendar;

/**
 * Created by SUHAIL on 11/9/2017.
 */

public class ImageViewer extends Fragment {
    private PhotoView img;
    private String url,sbjct;
    private String tstRslt;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View v = inflater.inflate(R.layout.image_view, container, false);
        if (container != null) {
            container.removeAllViews();
        }
        Bundle b = this.getArguments();
        if (getArguments() != null) {
            url = b.getString("url");
            sbjct = b.getString("sbjct");
            tstRslt=b.getString("tstRslt");
        }
        if (tstRslt=="yes") {
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            v.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            TestResults tR = new TestResults();
                            ft.replace(R.id.test_results, tR);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                            ft.commit();

                        }
                    }
                    return false;
                }
            });
        }
        img = (PhotoView) v.findViewById(R.id.photoViewer);
        img.setMaximumScale(5);
        Glide.with(getActivity()).load(url).into(img);
        setHasOptionsMenu(true);
        Calendar c=Calendar.getInstance();
        return v;
    }
}
