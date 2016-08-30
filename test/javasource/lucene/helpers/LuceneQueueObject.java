package lucene.helpers;

/*
 * Helper object to pass documents to the background-consumer process that indexes them.
 */

import org.apache.lucene.document.Document;

public class LuceneQueueObject {
	private Document document;
	private LuceneAction queueAction;
	public LuceneQueueObject(LuceneAction action, Document document) {
		this.queueAction = action;
		this.document = document;
	}
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	public LuceneAction getQueueAction() {
		return queueAction;
	}
	public void setQueueAction(LuceneAction queueAction) {
		this.queueAction = queueAction;
	}
	public boolean isUpdate() {
		return queueAction == LuceneAction.UPDATE;
	}
	public boolean isDelete() {
		return queueAction == LuceneAction.DELETE;
	}
}


