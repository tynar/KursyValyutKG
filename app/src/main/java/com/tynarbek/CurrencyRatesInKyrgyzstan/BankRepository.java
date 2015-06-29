package com.tynarbek.CurrencyRatesInKyrgyzstan;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by tynar on 4/15/2015.
 */
public class BankRepository {
    private ArrayList<Bank> mBanks;

    private static BankRepository sBankRepository;
    private Context mAppContext;

    public BankRepository(Context context) {
        mAppContext = context;
        mBanks = new ArrayList<>();
        mBanks.add(Banks.NacBank.get(context));
        mBanks.add(Banks.DemirBank.get(context));
        mBanks.add(Banks.OptimaBank.get(context));
        mBanks.add(Banks.KicbBank.get(context));
        mBanks.add(Banks.AiylBank.get(context));
    }

    public static BankRepository get(Context c){
        if (sBankRepository == null){
            sBankRepository = new BankRepository(c.getApplicationContext());
        }
        return sBankRepository;
    }

    public ArrayList<Bank> getBanks() {
        return mBanks;
    }

    public Bank getBank(int bankId) {
        for(Bank bank : mBanks){
            if (bank.getId() == bankId) return bank;
        }
        return null;
    }
}
