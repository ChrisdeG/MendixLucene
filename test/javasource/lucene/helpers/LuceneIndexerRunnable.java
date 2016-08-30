package lucene.helpers;
/*
 * Runnable that indexes the lucene documents in batches every n seconds or if queue size is bigger than treshold  
 */

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.AlreadyClosedException;

import com.mendix.core.Core;
import com.mendix.logging.ILogNode;

import lucene.proxies.constants.Constants;

// TODO: Auto-generated Javadoc
/**
 * The Class LuceneIndexerRunnable.
 */
public class LuceneIndexerRunnable implements Runnable {

	/** queue can not be bigger than this **/
	private static final int MAXQUEUESIZE = 2000;

	/** The logger. */
	ILogNode logger = Core.getLogger(LuceneFactory.NodeName);

	/** The index id. */
	private long indexId;

	/** The idlecount. */
	private int idlecount = 0;

	/** The lucene factory. */
	private LuceneFactory luceneFactory;

	/** The queue. */
	private LinkedBlockingQueue<LuceneQueueObject> queue= new LinkedBlockingQueue<LuceneQueueObject>();

	/** The batch size. */
	private Long batchSize = 500L;

	/** The max idle count. */
	private Long maxIdleCount = 100L; 

	/** The interval seconds. */
	private Long intervalSeconds = 5L;

	/** The close close time out seconds. */
	private Long closeCloseTimeOutSeconds = 100L;

	private ReentrantLock lock = new ReentrantLock();

	/**
	 * Instantiates a new lucene indexer runnable.
	 *
	 * @param indexId the index id
	 * @param luceneFactory the lucene factory
	 */
	public LuceneIndexerRunnable(Long indexId, LuceneFactory luceneFactory) {
		this.indexId = indexId;
		this.luceneFactory = luceneFactory;
		batchSize = Constants.getLuceneBatchsize() != null ? Constants.getLuceneBatchsize() : 500L;
		intervalSeconds = Constants.getLuceneIntervalSeconds() != null ? Constants.getLuceneIntervalSeconds() : 5L;
		closeCloseTimeOutSeconds = Constants.getLuceneCloseTimeOutSeconds() != null ? Constants.getLuceneCloseTimeOutSeconds() : 100L;
		maxIdleCount = closeCloseTimeOutSeconds / intervalSeconds; 
	}

	/**
	 * add a lucene document to the queue.
	 *
	 * @param action the action
	 * @param document the document
	 * @throws Exception 
	 */

	public void addToQueue(LuceneAction action, Document document) throws Exception {
		queue.offer(new LuceneQueueObject(action, document));
		logger.trace("Added document to queue, current queue size: " + queue.size());

		// queue is getting full before the regular run() is running.
		if (queue.size() >= MAXQUEUESIZE ) {
			logger.error("Queue overflow. Can cause memory errors. Queue size = " + queue.size());
			throw new Exception("Queue overflow. Can cause memory errors. Queue size = " + queue.size());
		}
		if (queue.size() >= batchSize) {
			logger.trace("Start processing based on size");
			runIndexer();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	// process the queue every 5 seconds. consumes the list.
	public void run() {
		Thread.currentThread().setPriority(3);
		// while something in queue
		runIndexer();

	}
	/**
	 * Run indexer.
	 */
	public void runIndexer() {
		lock.lock();
		try {
			int added = 0;
			int deleted = 0;
			if (queue.size()  > 0) {
				idlecount = 0;
				logger.trace("Start indexing, queue size " + queue.size());
				try {
					logger.trace("Get index writer " + indexId);
					IndexWriter indexWriter = luceneFactory.getIndexWriter(indexId);
					while(queue.size() > 0){
						logger.trace("Indexing, queue size " + queue.size());
						try {
							// first peek to prevent the queue seen as empty
							LuceneQueueObject luceneQueueObject = queue.take();
							final Term term = new Term(LuceneFactory.MXID, luceneQueueObject.getDocument().get(LuceneFactory.MXID));
							if (luceneQueueObject.isUpdate()) {
								added++;
								logger.trace("Updating document");
								indexWriter.updateDocument(term, luceneQueueObject.getDocument());
							}
							if (luceneQueueObject.isDelete()) {
								deleted++;
								logger.trace("Deleting document");
								indexWriter.deleteDocuments(term);
							}
							// take the object from queue to set it processed
						} catch (InterruptedException e) {
							logger.error("Error update document", e);
						} catch (AlreadyClosedException e) {
							logger.error("Index writer already closed", e);
						}
					}
					// report the work done
					if (added > 0) {
						logger.trace("Added " + added + " documents ");
					}
					if (deleted > 0) {
						logger.trace("Deleted " + deleted + " documents ");
					}
					if (added==0 && deleted==0) {
						logger.info("Nothing added or deleted");
					}
					indexWriter.commit();			
				} catch (IOException e) {
					logger.error("Error indexing documents", e);
				}
			} else {
				idlecount++;		
				// logger.trace("Nothing to index, idle count = " + idlecount);

				// close the index after 500 seconds.
				if (idlecount > maxIdleCount) {
					logger.trace("Idle count > " + maxIdleCount + ", closing index writer");
					luceneFactory.closeIndexWriter(indexId);
					idlecount = 0;
				}
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Wait until indexing is done and then close the indexwriter. 
	 *
	 * @param indexId the index id
	 * @return true, if successful
	 */
	public boolean waitForClose(Long indexId) {
		if (indexingReady()) {
			luceneFactory.closeIndexWriter(indexId);
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Indexing ready. Waits until the queue is empty.
	 *
	 * @return true, if successful
	 */
	public boolean indexingReady() {
		int i =0;
		// wait while
		while ((lock.isLocked() || queue.size() > 0) && i < 20) {
			try {
				logger.debug("Wait for completing indexing before closing");
				Thread.sleep(1000);
				i++;
			} catch (InterruptedException e) {
				logger.error("Error wait for close", e);
			}
		}
		return (queue.size()==0);
	}

	/**
	 * Queue size.
	 *
	 * @return the size of the queue.
	 */
	public int queueSize() {
		if (lock.isLocked()) {
			return 1;
		}
		return queue.size();
	}
}
