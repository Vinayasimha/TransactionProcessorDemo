package demo.transactionprocessor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Vinayasimha Patil
 *
 */
public class FileProcessorTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {

		File transactionFolder = tempFolder.newFolder("Transactions");
		TransactionFileUtils.setUpProcessorSystem(transactionFolder.getAbsolutePath());

		File file1 = new File(transactionFolder, "pending/finance_customer_transactions-20170801060101.csv");
		StringBuilder records = new StringBuilder();
		records.append("\"Customer Account#\", \"Transaction Amount\"").append(System.lineSeparator());
		records.append("123456789, 100.00").append(System.lineSeparator());
		records.append("987654321, -50.00").append(System.lineSeparator());
		records.append("123456788, 100").append(System.lineSeparator());
		records.append("123456789, -100").append(System.lineSeparator());
		records.append("987654321, -50.00").append(System.lineSeparator());
		records.append("987654322, 50.00").append(System.lineSeparator());

		Files.write(file1.toPath(), records.toString().getBytes(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);

		File file2 = new File(transactionFolder, "pending/finance_customer_transactions-20170812060101.csv");
		records = new StringBuilder();
		records.append("\"Customer Account#\", \"Transaction Amount\"").append(System.lineSeparator());
		records.append("   ").append(System.lineSeparator());
		records.append("123456789, 100.00").append(System.lineSeparator());
		records.append("987654321, -50.00").append(System.lineSeparator());
		records.append("A123456788, 100").append(System.lineSeparator());
		records.append("123456789, -100").append(System.lineSeparator());
		records.append("987654321, -50.00").append(System.lineSeparator());
		records.append("987654322, M50.00").append(System.lineSeparator());
		records.append("987654323, -50.00, 10").append(System.lineSeparator());

		Files.write(file2.toPath(), records.toString().getBytes(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void test() {
		FileProcessor processor = new FileProcessor();
		processor.process();

		File transactionsDir = new File(tempFolder.getRoot(), "Transactions");

		File file1 = new File(transactionsDir, "processed/finance_customer_transactions-20170801060101.csv");
		File file2 = new File(transactionsDir, "processed/finance_customer_transactions-20170812060101.csv");

		Assert.assertTrue(file1.getAbsolutePath() + " should exist after processing.", file1.exists());
		Assert.assertTrue(file2.getAbsolutePath() + " should exist after processing.", file2.exists());

		file1 = new File(transactionsDir, "pending/finance_customer_transactions-20170801060101.csv");
		file2 = new File(transactionsDir, "pending/finance_customer_transactions-20170812060101.csv");

		Assert.assertFalse(file1.getAbsolutePath() + " should not exist after processing.", file1.exists());
		Assert.assertFalse(file2.getAbsolutePath() + " should not exist after processing.", file2.exists());

		file1 = new File(transactionsDir, "reports/finance_customer_transactions_report-20170801060101.txt");
		file2 = new File(transactionsDir, "reports/finance_customer_transactions_report-20170812060101.txt");

		Assert.assertTrue(file1.getAbsolutePath() + " should exist after processing.", file1.exists());
		Assert.assertTrue(file2.getAbsolutePath() + " should exist after processing.", file2.exists());
	}

}
