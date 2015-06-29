package com.tynarbek.CurrencyRatesInKyrgyzstan;

import android.content.Context;
import android.util.Xml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tynar on 4/13/2015.
 */
public class Banks {
    public static class NacBank extends Bank{
        private static NacBank sNacBank;
        private Context mAppContext;

        public NacBank(Context applicationContext) {
            mAppContext = applicationContext;
            setName("НБКР");
            setId(0);
        }

        public static NacBank get(Context c) {
            if (sNacBank == null){
                sNacBank = new NacBank(c.getApplicationContext());
            }
            return sNacBank;
        }

        @Override
        public void refreshRate() {
            Rate result = null;

            //JSoup
            try {
                result = new Rate();
                result.setDate(new Date());

                URL url = new URL("http://www.nbkr.kg/XML/daily.xml");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                InputStream stream = conn.getInputStream();
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(stream, null);

                int event = parser.getEventType();
                String currency = null;
                while (event != XmlPullParser.END_DOCUMENT) {
                    String name = null;
                    switch (event) {
                        case XmlPullParser.START_TAG:
                            name = parser.getName();
                            if (name.equals("CurrencyRates")) {
                                String dt = parser.getAttributeValue(null, "Date");
                                result.setDate(getSimpleDate(dt));
                            } else if (name.equals("Currency")) {
                                currency = parser.getAttributeValue(null, "ISOCode");
                            } else if (name.equals("Value")) {
                                String value = parser.nextText();
                                switch (currency) {
                                    case "USD":
                                        result.setUsdBuy(simpleParseDouble(value));
                                        result.setUsdSell(simpleParseDouble(value));
                                        break;
                                    case "EUR":
                                        result.setEurBuy(simpleParseDouble(value));
                                        result.setEurSell(simpleParseDouble(value));
                                        break;
                                    case "RUB":
                                        result.setRubBuy(simpleParseDouble(value));
                                        result.setRubSell(simpleParseDouble(value));
                                        break;
                                    case "KZT":
                                        result.setKztBuy(simpleParseDouble(value));
                                        result.setKztSell(simpleParseDouble(value));
                                        break;
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                    }
                    event = parser.next();
                }

                stream.close();

                setRate(result);
                saveToCache(mAppContext);
            }
            catch(Exception ignored){
                result = readFromCache(mAppContext);
                setRate(result);
            }
        }
    }

    private static double simpleParseDouble(String text) {
        String number = text.trim().replace(" ", "").replace(',', '.');

        DecimalFormat fmt = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        fmt.setDecimalFormatSymbols(symbols);

        double result = 0;
        try {
            result = fmt.parse(number).doubleValue();
        } catch (ParseException e) {
        }
        return result;
    }

    public static class OptimaBank extends Bank{
        private static OptimaBank sBank;
        private Context mAppContext;

        public OptimaBank(Context applicationContext) {
            mAppContext = applicationContext;
            setName("Оптима банк");
            setId(1);
        }

        public static OptimaBank get(Context c) {
            if (sBank == null){
                sBank = new OptimaBank(c.getApplicationContext());
            }
            return sBank;
        }
        @Override
        public void refreshRate() {
            Rate result = null;

            //JSoup
            try {
                result = new Rate();
                result.setDate(new Date());
                Document doc = Jsoup.connect("http://www.optimabank.kg/en/faq.html").get();
                Element mod_rates_table = doc.select("table.mod_rates_table").get(0);
                if (mod_rates_table != null){
                    Element mod_rates_date = mod_rates_table.select("span.mod_rates_date").get(0);
                    if (mod_rates_date != null) result.setDate(getSimpleDate(mod_rates_date.text()));

                    Elements rows = mod_rates_table.select("tbody>tr");
                    if (rows != null){
                        for(int i=0; i<rows.size(); i++){
                            Element row = rows.get(i);
                            if (row.className().startsWith("row")) {
                                Element th = row.select("th").get(0);
                                if (th!=null && th.text().equals("USD")){
                                    Elements rates = row.select("span");
                                    result.setUsdBuy(Double.parseDouble(rates.get(0).text()));
                                    result.setUsdSell(Double.parseDouble(rates.get(1).text()));
                                }

                                if (th!=null && th.text().equals("EUR")){
                                    Elements rates = row.select("span");
                                    result.setEurBuy(Double.parseDouble(rates.get(0).text()));
                                    result.setEurSell(Double.parseDouble(rates.get(1).text()));
                                }

                                if (th!=null && th.text().equals("RUB")){
                                    Elements rates = row.select("span");
                                    result.setRubBuy(Double.parseDouble(rates.get(0).text()));
                                    result.setRubSell(Double.parseDouble(rates.get(1).text()));
                                }

                                if (th!=null && th.text().equals("KZT")){
                                    Elements rates = row.select("span");
                                    result.setKztBuy(Double.parseDouble(rates.get(0).text()));
                                    result.setKztSell(Double.parseDouble(rates.get(1).text()));
                                }

                            }
                        }
                    }
                }
                setRate(result);
                saveToCache(mAppContext);
            }
            catch(Exception ignored){
                result = readFromCache(mAppContext);
                setRate(result);
            }
        }
    }

    public static class KicbBank extends Bank{
        private static KicbBank sNacBank;
        private Context mAppContext;

        public KicbBank(Context applicationContext) {
            mAppContext = applicationContext;
            setName("KICB");
            setId(2);
        }

        public static KicbBank get(Context c) {
            if (sNacBank == null){
                sNacBank = new KicbBank(c.getApplicationContext());
            }
            return sNacBank;
        }

        @Override
        public void refreshRate() {
            Rate result = null;

            //JSoup
            try {
                result = new Rate();
                result.setDate(new Date());
                Document doc = Jsoup.connect("http://kicb.net/curency/").get();
                Element span = doc.select("div#head>table>tbody>tr>td>span").get(0);
                if (span != null){
                    String dt = span.text().trim().substring(9);
                    Date dt2 = getSimpleDate(dt);
                    result.setDate(dt2);

                    Element td = span.parent().parent().parent().select("tr>td").get(1);
                    Elements spans = td.select("table>tbody>tr>td>span");

                    for(Element spanCurr : spans) {
                        if (spanCurr.text().equals("USD")){
                            Element usdBuy = spanCurr.parent().nextElementSibling().child(0);
                            result.setUsdBuy(Double.parseDouble(usdBuy.text()));
                            Element usdSell = spanCurr.parent().nextElementSibling().nextElementSibling().child(0);
                            result.setUsdSell(Double.parseDouble(usdSell.text()));
                            continue;
                        }
                        if (spanCurr.text().equals("EUR")){
                            Element buy = spanCurr.parent().nextElementSibling().child(0);
                            result.setEurBuy(Double.parseDouble(buy.text()));
                            Element sell = spanCurr.parent().nextElementSibling().nextElementSibling().child(0);
                            result.setEurSell(Double.parseDouble(sell.text()));
                            continue;
                        }
                        if (spanCurr.text().equals("RUB")){
                            Element buy = spanCurr.parent().nextElementSibling().child(0);
                            result.setRubBuy(Double.parseDouble(buy.text()));
                            Element sell = spanCurr.parent().nextElementSibling().nextElementSibling().child(0);
                            result.setRubSell(Double.parseDouble(sell.text()));
                            continue;
                        }
                        if (spanCurr.text().equals("KZT")){
                            Element buy = spanCurr.parent().nextElementSibling().child(0);
                            result.setKztBuy(Double.parseDouble(buy.text()));
                            Element sell = spanCurr.parent().nextElementSibling().nextElementSibling().child(0);
                            result.setKztSell(Double.parseDouble(sell.text()));
                            continue;
                        }
                    }

                }
                setRate(result);
                saveToCache(mAppContext);
            }
            catch(Exception ignored){
                result = readFromCache(mAppContext);
                setRate(result);
            }
        }
    }

    private static Date getSimpleDate(String dt) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
        return (fmt.parse(dt));
    }

    public static class DemirBank extends Bank{
        private static DemirBank sNacBank;
        private Context mAppContext;

        public DemirBank(Context applicationContext) {
            mAppContext = applicationContext;
            setName("Демир банк");
            setId(3);
        }

        public static DemirBank get(Context c) {
            if (sNacBank == null){
                sNacBank = new DemirBank(c.getApplicationContext());
            }
            return sNacBank;
        }

        @Override
        public void refreshRate() {
            Rate result = null;

            //JSoup
            try {
                result = new Rate();
                result.setDate(new Date());
                Document doc = Jsoup.connect("http://www.demirbank.kg/").get();

                Elements currencies = doc.select("#moneytable").select("td.redlabel").select("strong");
                for(Element currency : currencies) {
                    if (currency.text().equals("USD")) {
                        Element usdBuy = currency.parent().nextElementSibling();
                        if (usdBuy != null) {
                            result.setUsdBuy(Double.parseDouble(usdBuy.text()));
                            Element usdSell = usdBuy.nextElementSibling();
                            if (usdSell != null)
                                result.setUsdSell(Double.parseDouble(usdSell.text()));
                        }
                    }

                    if (currency.text().equals("EUR")) {
                        Element eurBuy = currency.parent().nextElementSibling();
                        if (eurBuy != null) {
                            result.setEurBuy(Double.parseDouble(eurBuy.text()));
                            Element EurSell = eurBuy.nextElementSibling();
                            if (EurSell != null) {
                                result.setEurSell(Double.parseDouble(EurSell.text()));
                            }
                        }
                    }

                    if (currency.text().equals("RUB")) {
                        Element rubBuy = currency.parent().nextElementSibling();
                        if (rubBuy != null) {
                            result.setRubBuy(Double.parseDouble(rubBuy.text()));
                            Element RubSell = rubBuy.nextElementSibling();
                            if (RubSell != null) {
                                result.setRubSell(Double.parseDouble(RubSell.text()));
                            }
                        }
                    }

                    if (currency.text().equals("KZT")) {
                        Element kztBuy = currency.parent().nextElementSibling();
                        if (kztBuy != null) {
                            result.setKztBuy(Double.parseDouble(kztBuy.text()));
                            Element KztSell = kztBuy.nextElementSibling();
                            if (KztSell != null) {
                                result.setKztSell(Double.parseDouble(KztSell.text()));
                            }
                        }
                    }
                }
                setRate(result);
                saveToCache(mAppContext);
            }
            catch(Exception ignored){
                result = readFromCache(mAppContext);
                setRate(result);
            }
        }
    }

    public static class AiylBank extends Bank{
        private static AiylBank sNacBank;
        private Context mAppContext;

        public AiylBank(Context applicationContext) {
            mAppContext = applicationContext;
            setName("Айыл банк");
            setId(4);
        }

        public static AiylBank get(Context c) {
            if (sNacBank == null){
                sNacBank = new AiylBank(c.getApplicationContext());
            }
            return sNacBank;
        }

        @Override
        public void refreshRate() {
            Rate result = null;

            //JSoup
            try {
                result = new Rate();
                result.setDate(new Date());
                Document doc = Jsoup.connect("http://www.ab.kg/").get();

                Element date =doc.select("p#v_date>strong").get(0);
                if (date != null) {
                    result.setDate(getSimpleDate(date.text()));

                    Element table = date.parent().nextElementSibling();
                    result.setUsdBuy(Double.parseDouble(table.select("td#usd_buy").text()));
                    result.setUsdSell(Double.parseDouble(table.select("td#usd_sell").text()));

                    result.setEurBuy(Double.parseDouble(table.select("td#eur_buy").text()));
                    result.setEurSell(Double.parseDouble(table.select("td#eur_sell").text()));

                    result.setRubBuy(Double.parseDouble(table.select("td#rub_buy").text()));
                    result.setRubSell(Double.parseDouble(table.select("td#rub_sell").text()));

                    result.setKztBuy(Double.parseDouble(table.select("td#kzt_buy").text()));
                    result.setKztSell(Double.parseDouble(table.select("td#kzt_sell").text()));
                }

                setRate(result);
                saveToCache(mAppContext);
            }
            catch(Exception ignored){
                result = readFromCache(mAppContext);
                setRate(result);
            }
        }
    }
}
