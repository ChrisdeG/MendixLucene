package lucene.helpers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import com.mendix.core.Core;
import com.mendix.logging.ILogNode;

import lucene.proxies.constants.Constants;


/**
 * A factory for creating Lucene objects.
 *
 * @author Chris de Gelder
 */
public class LuceneFactory  {

	public static final Version LUCENE_VERSION = Version.LUCENE_43;
	/** for the lucene index directory */
	private static final String LUCENE_SUFFIX = ".idx";
	/** for the directory of the lucene index */
	private static final String LUCENE_PREFIX = "/lucene_";
	public static final String NodeName = "Lucene";
	public static ILogNode logger = Core.getLogger(NodeName);
	public static String MXID = "mxid";
	public static String MXTYPE = "mxtype";
	public static String TEXT = "text";
	
	/** The instance. */
	private static LuceneFactory instance = null;
	
	/** The analyzer. */
	private StandardAnalyzer analyzer;
	
	/** The interval seconds. */
	private static Long intervalSeconds = 10L;
	
	/** The index writers. */
	private Map<Long, IndexWriter> indexWriters = new HashMap<Long, IndexWriter>(); 
	
	/** The directories. */
	private Map<Long, Directory> directories = new HashMap<Long, Directory>(); 
	
	/** The background tasks. */
	private Map<Long, LuceneIndexerRunnable> backgroundTasks = new HashMap<Long, LuceneIndexerRunnable>(); 
	
	/** The executor. */
	static ScheduledExecutorService executor;

	/**
	 * Gets the single instance of LuceneFactory.
	 *
	 * @return single instance of LuceneFactory
	 */
	public static LuceneFactory getInstance() {
		if(instance == null) {
			instance = new LuceneFactory();
			executor = Executors.newSingleThreadScheduledExecutor();
			intervalSeconds = Constants.getLuceneIntervalSeconds() != null ? Constants.getLuceneIntervalSeconds() : 10L;
		}
		return instance;
	}
	
	/**
	 * Gets the background task. Every index has its own background process
	 *
	 * @param indexId the index id
	 * @return the background task
	 */
	public LuceneIndexerRunnable getBackgroundTask(Long indexId){
		if (backgroundTasks.get(indexId)==null) {
			LuceneIndexerRunnable periodicTask = new LuceneIndexerRunnable(indexId, instance);
			executor.scheduleAtFixedRate(periodicTask, 0, intervalSeconds, TimeUnit.SECONDS);
			backgroundTasks.put(indexId,  periodicTask);
		}
		return backgroundTasks.get(indexId);
	}

	/**
	 * Gets the lucene analyzer.
	 *
	 * @return the analyzer
	 */
	public StandardAnalyzer getAnalyzer() { 
		if (this.analyzer == null) {
			this.analyzer = new StandardAnalyzer(LUCENE_VERSION);
		}
		return this.analyzer;
	}

	/**
	 * Gets the directory. Directories are cached for reuse.
	 *
	 * @param indexId the index id
	 * @return the directory
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Directory getDirectory(Long indexId) throws IOException {
		if (directories.get(indexId)==null) {
			File file = getIndexDirectory(indexId);  
			Directory dir = FSDirectory.open(file);
			directories.put(indexId, dir);
		}
		return directories.get(indexId);
	}


	/**
	 * Gets the index writer.
	 *
	 * @param indexId the index id
	 * @return the index writer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public IndexWriter getIndexWriter(Long indexId) throws IOException {
		if (indexWriters.get(indexId) == null) {
			logger.trace("getIndexWriter: creating new IndexWriter for index " + indexId);
			IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, getAnalyzer());
			IndexWriter writer = new IndexWriter(getDirectory(indexId), config);
			indexWriters.put(indexId, writer);
		} else {
			logger.trace("getIndexWriter: returning existing IndexWriter for index " + indexId);
		}
		return indexWriters.get(indexId);
	}

	/**
	 * Close index writer.
	 *
	 * @param indexId the index id
	 */
	public void closeIndexWriter(Long indexId) {
		IndexWriter indexWriter = indexWriters.get(indexId);
		Directory dir = directories.get(indexId);
		if (indexWriter != null) {
			try {
				logger.debug("Closing index " + indexId);
				indexWriter.close();
				logger.trace("Closed index " + indexId);

				// remove from the list so it will be re-generated in getIndexWriter
				IndexWriter removedIndexWriter = indexWriters.remove(indexId);

				// check whether index writer could be removed
				if (removedIndexWriter != null) {
					logger.trace("Removed index writer for index " + indexId + " from map");
				} else {
					logger.error("Could not remove index writer for index " + indexId + " from map");
				}
			} catch (IOException e) {
				logger.error("Error closing indexwriter " + indexId, e);
			} 
		}
		/*

		*/		
	}
	
	public void closeDirectory(Long indexId) {
		FSDirectory dir = (FSDirectory) directories.get(indexId);
		if (dir != null) {
			logger.debug("Closing dir " + indexId);
			dir.close();
			logger.trace("Closed dir " + indexId);

			// remove from the list so it will be re-generated in getIndexWriter
			Directory removedDir = directories.remove(indexId);

			// check whether index writer could be removed
			if (removedDir != null) {
				logger.trace("Removed directory for index " + indexId + " from map");
			} else {
				logger.error("Could not remove directory for index " + indexId + " from map");
			} 
		}		
	}

	/**
	 * Delete index.
	 *
	 * @param indexId the index id
	 * @return true, if successful
	 * @throws Exception if indexer is busy (Queue not empty)
	 */
	public boolean deleteIndex(Long indexId, Boolean deleteFiles) throws Exception {
		IndexWriter indexWriter = getIndexWriter(indexId);
		if (indexWriter != null) {
			if (getBackgroundTask(indexId).queueSize() > 0) {
				throw new Exception("Lucene indexer is busy processing updates. Can not delete now.");
			}
			logger.debug("Deleting index " + indexId);
			indexWriter.deleteAll();
			indexWriter.commit();
			if (deleteFiles) {
				logger.debug("Cleanup files, index " + indexId);
				try {
					File dir = getIndexDirectory(indexId);
					for(File file: dir.listFiles()) 
					    if (!file.isDirectory()) { 
							logger.trace("Cleanup files, index " + indexId + " file " + file.getName());
					        if (!file.delete()) {;
					        	logger.trace("error deleting file, index " + indexId + " file " + file.getName());
					        }
					    }
					dir.delete();
				} catch (Exception e) {
					logger.debug("Error cleanup files, index" + indexId, e);
				}
			}
			logger.debug("Index deleted " + indexId);
		} else {
			logger.error("Error deleting index: not a valid indexId");
			return false;
		}
		return true;
	}



	/**
	 * Adds the document to the index
	 *
	 * @param index the index
	 * @param document the document
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 
	public boolean addDocument(Long index, Document document) throws IOException {
		logger.trace("Adding document to index " + index);
		IndexWriter iw = getIndexWriter(index);
		iw.addDocument(document);
		iw.commit();
		return true;
	} */

	/**
	 * Adds the document queued.
	 *
	 * @param index the index
	 * @param document the document
	 * @throws Exception 
	 */
	public void addDocumentQueued(Long indexId, Document document) throws Exception {
		logger.trace("Adding document to queue for index " + indexId);
		getBackgroundTask(indexId).addToQueue(LuceneAction.UPDATE, document);
	}

	/**
	 * Delete document queued.
	 *
	 * @param indexId the index number
	 * @param document the lucene document 
	 * @throws Exception 
	 */
	public void deleteDocumentQueued(Long indexId, Document document) throws Exception {
		logger.trace("Adding delete action to queue for index " + indexId);
		getBackgroundTask(indexId).addToQueue(LuceneAction.DELETE, document);
	}

	/**
	 * Gets the directory, for example 'data/tmp/lucene_0.idx'
	 * for an on-premise project you can set it to a any directory Mendix can use.
	 *
	 * @param indexId the index id
	 * @return the file
	 */
	public File getIndexDirectory(Long indexId) {
		return new File(Core.getConfiguration().getTempPath() + LUCENE_PREFIX + Long.toString(indexId) + LUCENE_SUFFIX);        
	}

	/**
	 * Wait for close.
	 *
	 * @param indexId the index id
	 * @return true, if successful
	 */
	public boolean waitForClose(Long indexId) {
		logger.trace("Wait for closing index " + indexId);
		if (getBackgroundTask(indexId) != null) {
			return getBackgroundTask(indexId).waitForClose(indexId);
		}
		return true;
	}

	/**
	 * Indexing ready.
	 *
	 * @param indexId the index id
	 * @return the boolean
	 */
	public Boolean indexingReady(Long indexId) {
		if (getBackgroundTask(indexId) != null) {
			return getBackgroundTask(indexId).indexingReady();
		}
		logger.error("Cannot wait for index ready" + indexId);
		return false;
	}


}
