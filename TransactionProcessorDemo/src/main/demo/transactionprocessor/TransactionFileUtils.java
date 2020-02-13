package demo.transactionprocessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author Vinayasimha Patil
 *
 */
public class TransactionFileUtils {

	private static Logger logger = Logger.getLogger(TransactionFileUtils.class.getName());

	private static File TRANSACTION_FILE_DIR;
	private static File TRANSACTION_FILE_PENDING_DIR;
	private static File TRANSACTION_FILE_PROCESSING_DIR;
	private static File TRANSACTION_FILE_PROCESSED_DIR;
	private static File TRANSACTION_FILE_REPORTS_DIR;

	public static void setUpProcessorSystem(String transactionFilesPath) {
		
		if (transactionFilesPath == null || transactionFilesPath.isEmpty()) {
			logger.severe("$TRANSACTION_PROCESSING variable is not defined.");
			System.exit(0);
		}

		TRANSACTION_FILE_DIR = new File(transactionFilesPath);
		if (!TRANSACTION_FILE_DIR.exists()) {
			logger.severe(transactionFilesPath + " does not exist.");
			System.exit(0);
		}

		TRANSACTION_FILE_PENDING_DIR = new File(TRANSACTION_FILE_DIR, "pending");
		TRANSACTION_FILE_PROCESSING_DIR = new File(TRANSACTION_FILE_DIR, "processing");
		TRANSACTION_FILE_PROCESSED_DIR = new File(TRANSACTION_FILE_DIR, "processed");
		TRANSACTION_FILE_REPORTS_DIR = new File(TRANSACTION_FILE_DIR, "reports");

		TRANSACTION_FILE_PENDING_DIR.mkdirs();
		TRANSACTION_FILE_PROCESSING_DIR.mkdirs();
		TRANSACTION_FILE_PROCESSED_DIR.mkdirs();
		TRANSACTION_FILE_REPORTS_DIR.mkdirs();
	}

	public static File getPendingTransactionFile() {

		File[] transactionFiles = TRANSACTION_FILE_PENDING_DIR
				.listFiles((dir, name) -> name.startsWith("finance_customer_transactions-") && name.endsWith(".csv"));

		if (transactionFiles == null || transactionFiles.length == 0)
			return null;

		Arrays.sort(transactionFiles, Comparator.comparing(File::lastModified));

		return transactionFiles[0];
	}

	public static void processTransactionFile(File transactionFile) throws IOException {

		logger.info("Processing file: " + transactionFile.getAbsolutePath());

		File processingFile = new File(TRANSACTION_FILE_PROCESSING_DIR, transactionFile.getName());
		Files.move(transactionFile.toPath(), processingFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		TransactionsReport report = parseTransactionFile(processingFile);

		File processedFile = new File(TRANSACTION_FILE_PROCESSED_DIR, transactionFile.getName());
		Files.move(processingFile.toPath(), processedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		String transactionFilename = transactionFile.getName();
		String timestamp = transactionFilename.substring(transactionFilename.indexOf('-') + 1,
				transactionFilename.indexOf('.'));
		String reportFileName = "finance_customer_transactions_report-" + timestamp + ".txt";

		File reportFile = new File(TRANSACTION_FILE_REPORTS_DIR, reportFileName);
		Files.write(reportFile.toPath(), report.toString().getBytes(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);

		logger.info("Completed processing of file: " + transactionFile.getAbsolutePath());
	}

	public static TransactionsReport parseTransactionFile(File transactionFile) {
		TransactionsReport report = new TransactionsReport(transactionFile.getName());

		try (Stream<String> stream = Files.lines(transactionFile.toPath())) {

			stream.forEach(report::processTransactionRecord);

		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		return report;
	}

}
