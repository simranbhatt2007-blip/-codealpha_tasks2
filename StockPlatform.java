import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class Stock {
    public String symbol;
    public String title;
    public double rate;

    public Stock(String symbol, String title, double rate) {
        this.symbol = symbol;
        this.title = title;
        this.rate = rate;
    }
}

class Investor {
    public String investorName;
    public double cashBalance;
    
    public String[] ownedSymbols = new String[100];
    public int[] ownedQuantities = new int[100];
    public int totalOwnedCount = 0;

    public Investor(String investorName, double initialCash) {
        this.investorName = investorName;
        this.cashBalance = initialCash;
    }

    public void buyStock(String symbol, int qty, double currentPrice) {
        cashBalance = cashBalance - (qty * currentPrice);
        
        for (int i = 0; i < totalOwnedCount; i++) {
            if (ownedSymbols[i].equals(symbol)) {
                ownedQuantities[i] = ownedQuantities[i] + qty;
                return;
            }
        }
        
        ownedSymbols[totalOwnedCount] = symbol;
        ownedQuantities[totalOwnedCount] = qty;
        totalOwnedCount++;
    }

    public void sellStock(String symbol, int qty, double currentPrice) {
        for (int i = 0; i < totalOwnedCount; i++) {
            if (ownedSymbols[i].equals(symbol)) {
                ownedQuantities[i] = ownedQuantities[i] - qty;
                cashBalance = cashBalance + (qty * currentPrice);
                return;
            }
        }
    }

    public int checkQuantity(String symbol) {
        for (int i = 0; i < totalOwnedCount; i++) {
            if (ownedSymbols[i].equals(symbol)) {
                return ownedQuantities[i];
            }
        }
        return 0;
    }
}

public class StockPlatform {
    public static Stock[] marketData = new Stock[3];
    public static Investor client;
    // Changed save file name to look unique
    public static String saveFile = "market_ledger.txt";

    public static void main(String[] args) {
        // Changed market data to Indian Companies with unique stock prices
        marketData[0] = new Stock("TATA", "Tata Motors", 925.40);
        marketData[1] = new Stock("RELI", "Reliance Industries", 2460.15);
        marketData[2] = new Stock("ZOMA", "Zomato Ltd.", 195.80);

        // Changed default investor name and budget
        client = new Investor("Rahul", 15000.00);
        loadUserData(); 

        Scanner inputScanner = new Scanner(System.in);
        boolean keepsRunning = true;

        // Custom screen headers
        System.out.println("=== SIMULATED SHARE MARKET ===");

        while (keepsRunning) {
            System.out.println("\n1. Show Current Rates");
            System.out.println("2. Buy Shares");
            System.out.println("3. Sell Shares");
            System.out.println("4. My Investment Portfolio");
            System.out.println("5. Close Program");
            System.out.print("Enter your choice (1-5): ");
            
            int selectedOption = inputScanner.nextInt();

            if (selectedOption == 1) {
                showMarket();
            } else if (selectedOption == 2) {
                processPurchase(inputScanner);
            } else if (selectedOption == 3) {
                processSale(inputScanner);
            } else if (selectedOption == 4) {
                showPortfolio();
            } else if (selectedOption == 5) {
                saveUserData(); 
                keepsRunning = false;
                System.out.println("Saving portfolio progress... Goodbye!");
            } else {
                System.out.println("Wrong choice! Please select between 1 and 5.");
            }
        }
        inputScanner.close();
    }

    public static void showMarket() {
        System.out.println("\n--- Live Market Board ---");
        for (int i = 0; i < marketData.length; i++) {
            System.out.println(marketData[i].symbol + " | " + marketData[i].title + " | Price: INR " + marketData[i].rate);
        }
    }

    public static void processPurchase(Scanner inputScanner) {
        System.out.print("Enter stock symbol to buy: ");
        String enteredTicker = inputScanner.next().toUpperCase();
        Stock matchedStock = locateStock(enteredTicker);

        if (matchedStock == null) {
            System.out.println("Error: This stock symbol does not exist.");
            return;
        }

        System.out.print("Enter quantity to purchase: ");
        int buyQty = inputScanner.nextInt();
        double absoluteCost = matchedStock.rate * buyQty;

        if (client.cashBalance >= absoluteCost) {
            client.buyStock(enteredTicker, buyQty, matchedStock.rate);
            System.out.println("Order successful! Balance updated.");
        } else {
            System.out.println("Order failed: You do not have enough funds.");
        }
    }

    public static void processSale(Scanner inputScanner) {
        System.out.print("Enter stock symbol to sell: ");
        String enteredTicker = inputScanner.next().toUpperCase();
        Stock matchedStock = locateStock(enteredTicker);

        if (matchedStock == null) {
            System.out.println("Error: This stock symbol does not exist.");
            return;
        }

        System.out.print("Enter quantity to liquidate: ");
        int sellQty = inputScanner.nextInt();

        if (client.checkQuantity(enteredTicker) >= sellQty) {
            client.sellStock(enteredTicker, sellQty, matchedStock.rate);
            System.out.println("Sale completed successfully.");
        } else {
            System.out.println("Sale failed: Insufficient holdings in portfolio.");
        }
    }

    public static void showPortfolio() {
        System.out.println("\n--- Current Account Summary ---");
        System.out.println("Wallet Balance: INR " + client.cashBalance);
        
        boolean zeroHoldings = true;
        double overallValue = client.cashBalance;

        for (int i = 0; i < client.totalOwnedCount; i++) {
            if (client.ownedQuantities[i] > 0) {
                zeroHoldings = false;
                Stock currentStock = locateStock(client.ownedSymbols[i]);
                double equityValue = currentStock.rate * client.ownedQuantities[i];
                overallValue = overallValue + equityValue;
                System.out.println(client.ownedSymbols[i] + ": " + client.ownedQuantities[i] + " units | Current Valuation: INR " + equityValue);
            }
        }

        if (zeroHoldings) {
            System.out.println("No active stocks found in your portfolio.");
        }
        System.out.println("Total Net Worth: INR " + overallValue);
    }

    public static Stock locateStock(String symbol) {
        for (int i = 0; i < marketData.length; i++) {
            if (marketData[i].symbol.equalsIgnoreCase(symbol)) {
                return marketData[i];
            }
        }
        return null;
    }

    public static void saveUserData() {
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(saveFile));
            fileWriter.write(String.valueOf(client.cashBalance));
            fileWriter.newLine();

            for (int i = 0; i < client.totalOwnedCount; i++) {
                if (client.ownedQuantities[i] > 0) {
                    fileWriter.write(client.ownedSymbols[i] + "," + client.ownedQuantities[i]);
                    fileWriter.newLine();
                }
            }
            fileWriter.close();
        } catch (IOException error) {
            System.out.println("Warning: System could not write database file.");
        }
    }

    public static void loadUserData() {
        File dataTarget = new File(saveFile);
        if (!dataTarget.exists()) {
            return;
        }

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(dataTarget));
            String cashLine = fileReader.readLine();
            if (cashLine != null) {
                client.cashBalance = Double.parseDouble(cashLine);
            }

            String recordLine;
            while ((recordLine = fileReader.readLine()) != null) {
                String[] segments = recordLine.split(",");
                if (segments.length == 2) {
                    String tickerCode = segments[0];
                    int shareCount = Integer.parseInt(segments[1]);
                    
                    client.ownedSymbols[client.totalOwnedCount] = tickerCode;
                    client.ownedQuantities[client.totalOwnedCount] = shareCount;
                    client.totalOwnedCount++;
                }
            }
            fileReader.close();
        } catch (Exception error) {
            System.out.println("Initializing with clean parameters.");
        }
    }
}