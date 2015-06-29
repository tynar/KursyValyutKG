package com.tynarbek.CurrencyRatesInKyrgyzstan;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * Created by tynar on 4/13/2015.
 */
public abstract class Bank {
    private Rate mRate;
    private String mName;
    private int mId;

    public Rate getRate() {
        return mRate;
    }

    public void setRate(Rate rate) {
        mRate = rate;
    }


    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public abstract void refreshRate();

    public boolean rateNeedsRefresh(){
        if (mRate == null || !mRate.isRefreshed()){
            return true;
        }
        else {
            long diff = new Date().getTime() - mRate.getLastRefreshDate().getTime();
            int passedMinutes = (int)(diff/(60*1000));

            if (passedMinutes >= Constants.REFRESH_INTERVAL_MIN) {
                return true;
            }
        }
        return false;
    }

    public boolean saveToCache(Context context) {
        mRate.setRefreshed(true);
        mRate.setLastRefreshDate(new Date());

        File fileToWrite = new File(context.getCacheDir(), String.valueOf(getId()));
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        boolean keep = true;

        try{
            fos = new FileOutputStream(fileToWrite);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(getRate());
        }
        catch(Exception e){
            keep = false;
        }
        finally{
            try{
                if (oos != null) oos.close();
                if (fos != null) fos.close();
                if (keep == false) fileToWrite.delete();
            }
            catch(Exception ignored){

            }
        }
        return keep;
    }

    public Rate readFromCache(Context context) {
        File fileToRead = new File(context.getCacheDir(), String.valueOf(getId()));

        Rate rate = null;
        FileInputStream fis = null;
        ObjectInputStream is = null;

        try{
            fis = new FileInputStream(fileToRead);
            is = new ObjectInputStream(fis);
            rate = (Rate)is.readObject();
        }
        catch(Exception ignored){
            if (fileToRead.exists()){
                try{
                    fileToRead.delete();
                }
                catch(Exception e){
                }
            }
        }
        finally{
            try{
                if (fis != null) fis.close();
                if (is != null) is.close();
            }
            catch(Exception ignored){

            }
        }
        return rate;
    }
}
