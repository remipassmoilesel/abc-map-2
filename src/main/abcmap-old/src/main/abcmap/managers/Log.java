package abcmap.managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

	/** Le niveau de log du programme */
	private static final Level LOG_LEVEL = Level.ALL;

	/** La racine des log */
	private static final File LOG_ROOT = new File("log");

	/** Le logger du programme */
	private static Logger logger;

	/** Le gestionnaire d'écriture sur disque */
	private static ImmediateFileHandler fileHandler;

	public static void init() throws IOException {

		// creer le répertoire de log si besoin
		if (LOG_ROOT.isDirectory() == false) {
			LOG_ROOT.mkdirs();
		}

		// le chemin par defaut des logs
		String pattern = Paths.get(LOG_ROOT.getAbsolutePath(), "log_%g.txt")
				.toString();

		// le gestionnaire d'ecriture sur disque
		fileHandler = new ImmediateFileHandler(pattern, 10000, 10, true);
		fileHandler.setFormatter(new SimpleFormatter());
		fileHandler.setLevel(LOG_LEVEL);

		// le logger principal
		// il doit être anonyme oubien il sera supprimé rapidement par le GC
		// voir
		// http://stackoverflow.com/questions/4050617/log-messages-lost-in-few-specific-situations
		logger = Logger.getAnonymousLogger();
		logger.setLevel(LOG_LEVEL);
		logger.addHandler(fileHandler);

	}

	/**
	 * Gestionnaire de log spécial: ecrit tout de suite après chaque log.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private static class ImmediateFileHandler extends FileHandler {

		public ImmediateFileHandler(String pattern, int limit, int count,
				boolean append) throws IOException, SecurityException {
			super(pattern, limit, count, append);
		}

		@Override
		public synchronized void publish(final LogRecord record) {
			super.publish(record);
			flush();
		}

	}

	/**
	 * Afficher tous les gestionnaires de journaux
	 */
	public static void printHandlers() {

		Handler[] handlers = logger.getHandlers();

		System.out.println("Log.printHandlers()");
		System.out.println("Handlers: " + handlers.length);

		for (Handler h : handlers) {
			System.out.println(h.getClass() + ": " + h.getLevel() + ", " + h);
		}
	}

	public static void debug(Throwable e) {
		logger.log(Level.WARNING, getCallLocation(), e);
	}

	public static void debug(String txt) {
		logger.log(Level.WARNING, getCallLocation() + " " + txt);
	}

	public static void debug(String txt, Throwable e) {
		logger.log(Level.WARNING, getCallLocation() + " " + txt, e);
	}

	public static void error(Throwable e) {
		logger.log(Level.SEVERE, getCallLocation(), e);
	}

	public static void error(String txt, Throwable e) {
		logger.log(Level.SEVERE, getCallLocation() + " " + txt, e);
	}

	public static void error(String txt) {
		logger.log(Level.SEVERE, getCallLocation() + " " + txt);
	}

	public static void info(Throwable e) {
		logger.log(Level.INFO, getCallLocation(), e);
	}

	public static void info(String txt) {
		logger.log(Level.INFO, getCallLocation() + " " + txt);
	}

	public static void info(String txt, Throwable e) {
		logger.log(Level.INFO, getCallLocation() + " " + txt, e);
	}

	/**
	 * Retourne le nom de la méthode et la ligne d'appel d'une méthode de log.
	 * 
	 * @return
	 */
	private static String getCallLocation() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		return "[Location: " + stack[stack.length - 2].toString() + "]";
	}

}
