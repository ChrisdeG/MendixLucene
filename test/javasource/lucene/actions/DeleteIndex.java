// This file was generated by Mendix Modeler.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package lucene.actions;

import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import lucene.helpers.LuceneFactory;

/**
 * Clears the complete index. Some files will remain as artifacts.
 * 
 * deleteFiles will delete the files from disk.
 * 
 * 
 */
public class DeleteIndex extends CustomJavaAction<Boolean>
{
	private Long indexId;
	private Boolean cleanUpDirectory;

	public DeleteIndex(IContext context, Long indexId, Boolean cleanUpDirectory)
	{
		super(context);
		this.indexId = indexId;
		this.cleanUpDirectory = cleanUpDirectory;
	}

	@Override
	public Boolean executeAction() throws Exception
	{
		// BEGIN USER CODE
		return LuceneFactory.getInstance().deleteIndex(indexId, cleanUpDirectory);
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@Override
	public String toString()
	{
		return "DeleteIndex";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
