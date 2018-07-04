package com.jrs.medicare;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by SUHAIL on 8/2/2017.
 */

public class navFragmentPageAdapter extends FragmentPagerAdapter {
    private int pagecount=5;
    Context context;
    public navFragmentPageAdapter(FragmentManager fm) {
        super(fm);

    }
    @Override
    public Fragment getItem(int position) {
        Fragment fragment =null;
        switch (position)
        {
            // case 0:return new Menu1();
            case 1:fragment= new BookingDetails();
                break;
            case 2:fragment= new ConsultHistory();
                break;
            case 3:fragment= new Prescription();
                break;
            case 4:fragment= new Complaint();
                break;
            case 5:fragment= new SendTestResult();
                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return pagecount;
    }
}
