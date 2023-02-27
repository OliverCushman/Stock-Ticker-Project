import yahoofinance.*;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.util.Calendar;
import java.util.List;
import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;


/**
 * Retrieve historical stock prices
 */
public class StockPriceHistory {

    private final String TICKER = "GOOG";
    private final Interval DAILY = Interval.DAILY;
    private final Calendar from = Calendar.getInstance();



    
    /**
     * Retrieve the stock price data
     */
    public void run() {
        try {
            from.roll(Calendar.YEAR, -1);
            Stock stock = YahooFinance.get(TICKER, true);
            List<HistoricalQuote> quoteList = stock.getHistory(from, DAILY);
            HashMap<String, String> quoteMap = new HashMap<String, String>(); 
            // map is not necessary, data can be written from list
            String quote;
            String date;
            String price;
            System.out.println("Writing " + TICKER + " stock history to 'StockQuotes.csv'...");
            File csvFile = new File("StockQuotes.csv");
            FileWriter writer = new FileWriter(csvFile);
            writer.write("Date,Closing Price\n");
            for (int i = 0; i < quoteList.size(); i++) {
                quote = quoteList.get(i).toString();
                date = quote.substring(5, 15);
                price = Float.toString(roundCents(quote.substring(getPriceIndex(quote), quote.length() - 1)));
                if (decimalCount(price) < 2) {
                    price += "0";
                }
                quoteMap.put(date, price);
                writer.write(date + "," + quoteMap.get(date) + "\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.out.println("Error in stock call");    
        }
    }

    private int getPriceIndex(String data) {
        int index = 0;
        for (int i = data.length() - 2; i >= 0; i--) {
            if (data.substring(i, i + 1).equals("(")) {
                index = i + 1;
            }
        }
        return index;
    }

    private int decimalCount(String data) {
        int count = 0;
        for (int i = data.length() - 1; i >= 0; i--) {
            if (!data.substring(i, i + 1).equals(".")) {
                count++;
            } else {
                i = -1;
            }
        }
        return count;
    }
    
    /**
     * Rounds a stock price in the form of a string to cents
     * @param data
     * @return rounded price
     */
    private float roundCents(String data) {
        float price = Float.parseFloat(data);
        return (float) Math.round(price * 100) / 100;
    }
    
    // formatDate removed
    
    /**
     * Main method to run the program
     */
    public static void main (String[] args) {
        StockPriceHistory sph = new StockPriceHistory();
        sph.run();
    }
}