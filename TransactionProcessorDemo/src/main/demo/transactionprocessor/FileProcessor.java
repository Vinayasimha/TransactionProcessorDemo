/**
 * 
 */
package demo.transactionprocessor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vinayasimha Patil
 *
 */
public class FileProcessor implements Runnable {

	private static Logger logger = Logger.getLogger(FileProcessor.class.getName());

	@Override
	public void run() {
		try {
			long startTime = System.currentTimeMillis();
			while (true) {
				File pendingFile = TransactionFileUtils.getPendingTransactionFile();
				if (pendingFile == null) {
					long wating = System.currentTimeMillis() - startTime;
					long watingInMinutes = TimeUnit.MILLISECONDS.toMinutes(wating);
					if (watingInMinutes > 10) {
						logger.info("Waited for 10 minutes but not got any more files");
						break;
					} else {
						logger.info("waiting... After 2 minutes again pending files will be checked.");
						Thread.sleep(TimeUnit.MINUTES.toMillis(2));
					}
				} else {
					TransactionFileUtils.processTransactionFile(pendingFile);
				}
			}

		} catch (Exception e) {
			// Catching all exceptions so that scheduler will not be impacted.
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void process() {
		File pendingFile = TransactionFileUtils.getPendingTransactionFile();
		while (pendingFile != null) {
			try {
				TransactionFileUtils.processTransactionFile(pendingFile);
				pendingFile = TransactionFileUtils.getPendingTransactionFile();
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
}
