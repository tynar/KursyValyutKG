package com.tynarbek.CurrencyRatesInKyrgyzstan;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by tynar on 4/13/2015.
 */
public class BankPagerActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private ArrayList<Bank> mBanks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mBanks = BankRepository.get(this).getBanks();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentPagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                return BankFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return mBanks.size();
            }
        });

        int bankId = getIntent().getIntExtra(BankFragment.EXTRA_BANK_ID, 0);
        for (int i = 0; i < mBanks.size(); i++){
            Bank bank = mBanks.get(i);
            if (bank.getId() == bankId) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Bank bank = mBanks.get(position);
                if (!TextUtils.isEmpty(bank.getName())){
                    setTitle(bank.getName());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
