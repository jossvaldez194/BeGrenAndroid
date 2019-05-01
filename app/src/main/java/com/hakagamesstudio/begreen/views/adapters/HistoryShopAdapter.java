package com.hakagamesstudio.begreen.views.adapters;

import android.content.Context;

import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.views.fragments.MonthHistoryShopFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HistoryShopAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private String mDate;

    public HistoryShopAdapter(@NonNull FragmentManager fm, Context context, String date) {
        super(fm);
        mContext = context;
        mDate = date;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MonthHistoryShopFragment.newInstance("ENE", mDate);
            case 1:
                return MonthHistoryShopFragment.newInstance("FEB", mDate);
            case 2:
                return MonthHistoryShopFragment.newInstance("MAR", mDate);
            case 3:
                return MonthHistoryShopFragment.newInstance("ABR", mDate);
            case 4:
                return MonthHistoryShopFragment.newInstance("MAY", mDate);
            case 5:
                return MonthHistoryShopFragment.newInstance("JUN", mDate);
            case 6:
                return MonthHistoryShopFragment.newInstance("JUL", mDate);
            case 7:
                return MonthHistoryShopFragment.newInstance("AGO", mDate);
            case 8:
                return MonthHistoryShopFragment.newInstance("SEP", mDate);
            case 9:
                return MonthHistoryShopFragment.newInstance("OCT", mDate);
            case 10:
                return MonthHistoryShopFragment.newInstance("NOV", mDate);
            case 11:
                return MonthHistoryShopFragment.newInstance("DIC", mDate);
            default:
                return MonthHistoryShopFragment.newInstance("ENE", mDate);

        }
    }

    @Override
    public int getCount() {
        return 12;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.january);
            case 1:
                return mContext.getString(R.string.february);
            case 2:
                return mContext.getString(R.string.march);
            case 3:
                return mContext.getString(R.string.april);
            case 4:
                return mContext.getString(R.string.may);
            case 5:
                return mContext.getString(R.string.june);
            case 6:
                return mContext.getString(R.string.july);
            case 7:
                return mContext.getString(R.string.august);
            case 8:
                return mContext.getString(R.string.september);
            case 9:
                return mContext.getString(R.string.october);
            case 10:
                return mContext.getString(R.string.november);
            case 11:
                return mContext.getString(R.string.december);
            default:
                return null;
        }
    }
}
