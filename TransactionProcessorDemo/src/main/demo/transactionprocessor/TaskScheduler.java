package demo.transactionprocessor;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author Vinayasimha Patil
 *
 */
public class TaskScheduler {

	private static Logger logger = Logger.getLogger(TaskScheduler.class.getName());

	public static void scheduleTransactionProcessor(int hourOfDay) {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

		FileProcessor fileProcessor = new FileProcessor();
		long taskStartTime = getTaskStartTime(hourOfDay);

		service.scheduleAtFixedRate(fileProcessor, taskStartTime, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
	}

	private static long getTaskStartTime(int hourOfDay) {

		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.HOUR_OF_DAY) > hourOfDay) {

			// As time is crossed for the current day, task will be scheduled starting from
			// next day.
			calendar.add(Calendar.DATE, 1);
		}

		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		logger.info("Transaction file processor scheduled for every day at " + hourOfDay + ":00 hours, statring from "
				+ new Date(calendar.getTimeInMillis()));
		return calendar.getTimeInMillis() - System.currentTimeMillis();
	}

}
