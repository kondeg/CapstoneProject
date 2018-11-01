package edu.udacity.project.dividendpayout.util;

public class Endpoints {

    public static final String combinedQuoteUrl="https://api.iextrading.com/1.0/stock/market/batch?symbols=";
    public static final String getCombinedQuoteKey="&types=delayed-quote,dividends,stats&range=2y";

    public static final String priceQuoteUrl="https://api.iextrading.com/1.0/stock/";
    public static final String priceQuoteUrlKey="/price";

    public static final String marketEndpoint = "https://www.bloomberg.com/markets/chart/data/1D/";
    public static final String dowSymbol="INDU:IND";
    public static final String spSymbol="SPX:IND";
    public static final String nasdaqSymbol="CCMP:IND";
}
