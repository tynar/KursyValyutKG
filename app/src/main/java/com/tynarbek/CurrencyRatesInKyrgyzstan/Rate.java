package com.tynarbek.CurrencyRatesInKyrgyzstan;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by tynar on 4/13/2015.
 */
public class Rate implements Serializable {
    private double mUsdBuy, mUsdSell, mEurBuy, mEurSell, mRubBuy, mRubSell, mKztBuy, mKztSell;
    private Date mDate;

    public double getUsdBuy() {
        return mUsdBuy;
    }

    public void setUsdBuy(double usdBuy) {
        mUsdBuy = usdBuy;
    }

    public double getUsdSell() {
        return mUsdSell;
    }

    public void setUsdSell(double usdSell) {
        mUsdSell = usdSell;
    }

    public double getEurBuy() {
        return mEurBuy;
    }

    public void setEurBuy(double eurBuy) {
        mEurBuy = eurBuy;
    }

    public double getEurSell() {
        return mEurSell;
    }

    public void setEurSell(double eurSell) {
        mEurSell = eurSell;
    }

    public double getRubBuy() {
        return mRubBuy;
    }

    public void setRubBuy(double rubBuy) {
        mRubBuy = rubBuy;
    }

    public double getRubSell() {
        return mRubSell;
    }

    public void setRubSell(double rubSell) {
        mRubSell = rubSell;
    }

    public double getKztBuy() {
        return mKztBuy;
    }

    public void setKztBuy(double kztBuy) {
        mKztBuy = kztBuy;
    }

    public double getKztSell() {
        return mKztSell;
    }

    public void setKztSell(double kztSell) {
        mKztSell = kztSell;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    private Date lastRefreshDate;
    private boolean refreshed = false;

    public Date getLastRefreshDate() {
        return lastRefreshDate;
    }

    public void setLastRefreshDate(Date lastRefreshDate) {
        this.lastRefreshDate = lastRefreshDate;
    }

    public boolean isRefreshed() {
        return refreshed;
    }

    public void setRefreshed(boolean refreshed) {
        this.refreshed = refreshed;
    }
}

