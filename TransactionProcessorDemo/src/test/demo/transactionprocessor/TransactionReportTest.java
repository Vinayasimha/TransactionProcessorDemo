package demo.transactionprocessor;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vinayasimha Patil
 *
 */
public class TransactionReportTest {

	@Test
	public void testTransactionReport() {

		TransactionsReport transactionsReport = new TransactionsReport(
				"finance_customer_transactions-20171201060101.csv");

		transactionsReport.processTransactionRecord("\"Customer Account#\", \"Transaction Amount\"");
		transactionsReport.processTransactionRecord("123456789, 100.00");
		transactionsReport.processTransactionRecord("987654321, -50.00");
		transactionsReport.processTransactionRecord("123456788, 100");
		transactionsReport.processTransactionRecord("123456789, -100");
		transactionsReport.processTransactionRecord("987654321, -50.00");
		transactionsReport.processTransactionRecord("987654322, 50.00");

		StringBuilder expectedReport = new StringBuilder();
		expectedReport.append("File Processed: finance_customer_transactions-20171201060101.csv")
				.append(System.lineSeparator());
		expectedReport.append("Total Accounts: 4").append(System.lineSeparator());
		expectedReport.append("Total Credits : $250.00").append(System.lineSeparator());
		expectedReport.append("Total Debits : $200.00").append(System.lineSeparator());
		expectedReport.append("Skipped Transactions: 0");

		Assert.assertEquals(expectedReport.toString(), transactionsReport.toString());
	}
	
	@Test
	public void testTransactionReportFormat() {

		TransactionsReport transactionsReport = new TransactionsReport(
				"finance_customer_transactions-20171201060101.csv");

		transactionsReport.processTransactionRecord("\"Customer Account#\", \"Transaction Amount\"");
		transactionsReport.processTransactionRecord("123456789, 100000.05");
		transactionsReport.processTransactionRecord("987654321, -500000.10");
		transactionsReport.processTransactionRecord("123456788, 100");
		transactionsReport.processTransactionRecord("123456789, -100");
		transactionsReport.processTransactionRecord("987654321, -50.25");
		transactionsReport.processTransactionRecord("987654322, 50.70");

		StringBuilder expectedReport = new StringBuilder();
		expectedReport.append("File Processed: finance_customer_transactions-20171201060101.csv")
				.append(System.lineSeparator());
		expectedReport.append("Total Accounts: 4").append(System.lineSeparator());
		expectedReport.append("Total Credits : $100,150.75").append(System.lineSeparator());
		expectedReport.append("Total Debits : $500,150.35").append(System.lineSeparator());
		expectedReport.append("Skipped Transactions: 0");

		Assert.assertEquals(expectedReport.toString(), transactionsReport.toString());
	}
	
	@Test
	public void testTransactionReportWithInvalidRecord() {

		TransactionsReport transactionsReport = new TransactionsReport(
				"finance_customer_transactions-20171201060101.csv");

		transactionsReport.processTransactionRecord("\"Customer Account#\", \"Transaction Amount\"");
		transactionsReport.processTransactionRecord("   "); // Empty Lines will be ignored
		transactionsReport.processTransactionRecord("123456789, 100.00");
		transactionsReport.processTransactionRecord("987654321, -50.00");
		transactionsReport.processTransactionRecord("A123456788, 100");
		transactionsReport.processTransactionRecord("123456789, -100");
		transactionsReport.processTransactionRecord("987654321, -50.00");
		transactionsReport.processTransactionRecord("987654322, M50.00");
		transactionsReport.processTransactionRecord("987654323, -50.00, 10");

		StringBuilder expectedReport = new StringBuilder();
		expectedReport.append("File Processed: finance_customer_transactions-20171201060101.csv")
				.append(System.lineSeparator());
		expectedReport.append("Total Accounts: 2").append(System.lineSeparator());
		expectedReport.append("Total Credits : $100.00").append(System.lineSeparator());
		expectedReport.append("Total Debits : $200.00").append(System.lineSeparator());
		expectedReport.append("Skipped Transactions: 3");

		Assert.assertEquals(expectedReport.toString(), transactionsReport.toString());
	}

}
