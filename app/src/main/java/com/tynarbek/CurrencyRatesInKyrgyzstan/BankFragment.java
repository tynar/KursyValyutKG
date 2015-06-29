package com.tynarbek.CurrencyRatesInKyrgyzstan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;

/**
 * Created by tynar on 4/15/2015.
 */
public class BankFragment extends Fragment{
    public static final String EXTRA_BANK_ID = "com.tynarbek.kursyvalyutkg.bank_id";
    private Bank mBank;

    private TextView txtBankTitle;
    private TextView txtUsdBuy;
    private TextView txtUsdSell;
    private TextView txtEurBuy;
    private TextView txtEurSell;
    private TextView txtRubBuy;
    private TextView txtRubSell;
    private TextView txtKztBuy;
    private TextView txtKztSell;
    private TextView txtRefreshDate;
    private TextView txtRateDate;
    private Button btnRefresh;

    private boolean forceRefresh = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int bankId = getArguments().getInt(EXTRA_BANK_ID, 0);
        mBank = BankRepository.get(getActivity()).getBank(bankId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bank, container, false);

        txtBankTitle = (TextView)v.findViewById(R.id.txtBankTitle);
        txtRateDate = (TextView)v.findViewById(R.id.txtRateDate);
        txtUsdBuy = (TextView)v.findViewById(R.id.txtUsdBuy);
        txtUsdSell = (TextView)v.findViewById(R.id.txtUsdSell);
        txtEurBuy = (TextView)v.findViewById(R.id.txtEurBuy);
        txtEurSell = (TextView)v.findViewById(R.id.txtEurSell);
        txtRubBuy = (TextView)v.findViewById(R.id.txtRubBuy);
        txtRubSell = (TextView)v.findViewById(R.id.txtRubSell);
        txtKztBuy = (TextView)v.findViewById(R.id.txtKztBuy);
        txtKztSell = (TextView)v.findViewById(R.id.txtKztSell);
        txtRefreshDate = (TextView)v.findViewById(R.id.txtRefreshDate);
        btnRefresh = (Button)v.findViewById(R.id.btnRefresh);

        if (mBank != null) {
            txtBankTitle.setText(mBank.getName());

            new RetrieveRates().execute();
        }

        btnRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                forceRefresh = true;
                new RetrieveRates().execute();
            }
        });
        return v;
    }

    public static BankFragment newInstance(int id){
        Bundle args = new Bundle();
        args.putInt(EXTRA_BANK_ID, id);

        BankFragment fragment = new BankFragment();
        fragment.setArguments(args);
        return fragment;
    }

    class RetrieveRates extends AsyncTask<Void,Void,Rate>{

        @Override
        protected Rate doInBackground(Void... voids) {
            if (forceRefresh || mBank.rateNeedsRefresh()) {
                mBank.refreshRate();
                forceRefresh = false;
            }
            return mBank.getRate();
        }

        @Override
        protected void onPostExecute(Rate rate) {
            if (rate != null) {
                txtUsdBuy.setText(String.valueOf(rate.getUsdBuy()));
                txtUsdSell.setText(String.valueOf(rate.getUsdSell()));
                txtEurBuy.setText(String.valueOf(rate.getEurBuy()));
                txtEurSell.setText(String.valueOf(rate.getEurSell()));
                txtRubBuy.setText(String.valueOf(rate.getRubBuy()));
                txtRubSell.setText(String.valueOf(rate.getRubSell()));
                txtKztBuy.setText(String.valueOf(rate.getKztBuy()));
                txtKztSell.setText(String.valueOf(rate.getKztSell()));

                DateFormat fmt = DateFormat.getDateTimeInstance();
                txtRefreshDate.setText(fmt.format(rate.getLastRefreshDate()));

                DateFormat fmt2 = DateFormat.getDateInstance();
                String strRateDate = getResources().getString(R.string.RateDate) + fmt2.format(rate.getDate());
                txtRateDate.setText(strRateDate);
            }
        }
    }
}
