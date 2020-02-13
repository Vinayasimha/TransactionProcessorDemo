package demo.transactionprocessor;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Vinayasimha Patil
 *
 */
public class TransactionsReport {

	private String fileProcessed;
	private Map<Long, Integer> accountsAndTransactions = new HashMap<>();
	private double totalCredits = 0;
	private double totalDebits = 0;
	private int skippedTransactions = 0;

	private boolean headerSkipped = false;

	public TransactionsReport(String fileProcessed) {
		this.fileProcessed = fileProcessed;
	}

	public void setFileProcessed(String fileProcessed) {
		this.fileProcessed = fileProcessed;
	}

	public void addTransaction(long accountNo, double amount) {
		Integer transactions = accountsAndTransactions.get(accountNo);
		if (transactions == null)
			transactions = 1;
		else
			transactions = transactions + 1;

		accountsAndTransactions.put(accountNo, transactions);

		if (amount < 0) {
			totalDebits += Math.abs(amount);
		} else {
			totalCredits += amount;
		}
	}

	public void incrementSkippedTransactions() {
		skippedTransactions++;
	}

	public void processTransactionRecord(String record) {
		if (record == null || record.trim().isEmpty()) {
			// Ignoring empty lines
			return;
		}

		if (!headerSkipped) {
			// First line will be treated as header.
			headerSkipped = true;
			return;
		}

		String[] columns = record.split(",");
		if (columns.length != 2) {
			incrementSkippedTransactions();
			return;
		}

		long accountNo = 0;
		try {
			accountNo = Long.parseLong(columns[0].trim());
		} catch (NumberFormatException e) {
			incrementSkippedTransactions();
			return;
		}

		double amount = 0;
		try {
			amount = Double.parseDouble(columns[1].trim());
		} catch (NumberFormatException e) {
			incrementSkippedTransactions();
			return;
		}

		addTransaction(accountNo, amount);
	}

	@Override
	public String toString() {

		Locale enAULocale = new Locale.Builder().setLanguage("en").setRegion("AU").build();
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(enAULocale);
		NumberFormat numberFormat = NumberFormat.getInstance(enAULocale);

		int numberOfAccount = accountsAndTransactions.size();

		StringBuilder report = new StringBuilder();
		report.append("File Processed: ").append(fileProcessed).append(System.lineSeparator());
		report.append("Total Accounts: ").append(numberFormat.format(numberOfAccount)).append(System.lineSeparator());
		report.append("Total Credits : ").append(currencyFormat.format(totalCredits)).append(System.lineSeparator());
		report.append("Total Debits : ").append(currencyFormat.format(totalDebits)).append(System.lineSeparator());
		report.append("Skipped Transactions: ").append(skippedTransactions);

		return report.toString();
	}
}
