package demo.transactionprocessor;

/**
 * @author Vinayasimha Patil
 *
 */
public class TransactionFileProcessor {

	public static void main(String[] args) {

		String transactionFilesPath = System.getenv("TRANSACTION_PROCESSING");
		TransactionFileUtils.setUpProcessorSystem(transactionFilesPath);

		if (args.length == 0) {
			usage();
		}

		boolean clearPendingFiles = args[0].equals("--clear-pending-files");
		if (clearPendingFiles) {
			// Clearing all pending files before scheduling a transaction processor

			FileProcessor processor = new FileProcessor();
			processor.process();
		}

		int i = clearPendingFiles ? 1 : 0;
		for (; i < args.length; i++) {
			try {
				int hourOfDay = Integer.parseInt(args[i]);
				TaskScheduler.scheduleTransactionProcessor(hourOfDay);
			} catch (NumberFormatException e) {
				usage();
			}
		}
	}

	private static void usage() {
		System.out.println("TransactionFileProcessor [--clear-pending-files] <hour_of_day> [<hour_of_day> ...]");
		System.out.println("TransactionFileProcessor 6 21");
		System.out.println("TransactionFileProcessor --clear-pending-files 6 21");
		System.exit(0);
	}
}
