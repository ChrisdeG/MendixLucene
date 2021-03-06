// This file was generated by Mendix Modeler.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package lucene.actions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.mendix.core.Core;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import lucene.helpers.LuceneFactory;
import com.mendix.systemwideinterfaces.core.IMendixObject;

/**
 * Restore a previously backed up lucene filedocument to a index. Will overwrite data.
 */
public class BackupRestoreFromFiledocument extends CustomJavaAction<Long>
{
	private Long indexId;
	private IMendixObject __luceneBackup;
	private lucene.proxies.LuceneBackup luceneBackup;

	public BackupRestoreFromFiledocument(IContext context, Long indexId, IMendixObject luceneBackup)
	{
		super(context);
		this.indexId = indexId;
		this.__luceneBackup = luceneBackup;
	}

	@Override
	public Long executeAction() throws Exception
	{
		this.luceneBackup = __luceneBackup == null ? null : lucene.proxies.LuceneBackup.initialize(getContext(), __luceneBackup);

		// BEGIN USER CODE
		EraseDirectory();
		File directory = LuceneFactory.getInstance().getIndexDirectory(indexId);
		directory.mkdirs();
		ZipInputStream zipIn = new ZipInputStream(Core.getFileDocumentContent(getContext(), luceneBackup.getMendixObject()));
		long totalUnzipped = 0L;
		try {
			ZipEntry entry = zipIn.getNextEntry();
			// iterates over entries in the zip file
			while (entry != null) {
				String filePath = directory + File.separator + entry.getName();
				if (!entry.isDirectory()) {
					// if the entry is a file, extracts it
					totalUnzipped += entry.getSize();
					extractFile(zipIn, filePath);
				} else {
					// if the entry is a directory, make the directory
					File dir = new File(filePath);
					dir.mkdir();
				}
				zipIn.closeEntry();
				entry = zipIn.getNextEntry();
			}
		} finally {
			zipIn.close();
		}
		return totalUnzipped;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@Override
	public String toString()
	{
		return "BackupRestoreFromFiledocument";
	}

	// BEGIN EXTRA CODE
	private static final int BUFFER_SIZE = 2048;
	public void EraseDirectory() {
		File directory = LuceneFactory.getInstance().getIndexDirectory(indexId);
		if (directory.exists()) {
			for (File file : directory.listFiles()) {
				if (file != null) {
					if (file.isFile()) {
						file.delete();
					}
				}
			}
		}
	}
	/*
	 * Extract one file from a zip file
	 */
	private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		final FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		try {
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
			try {
				byte[] bytesIn = new byte[BUFFER_SIZE];
				int read = 0;
				while ((read = zipIn.read(bytesIn)) != -1) {
					bos.write(bytesIn, 0, read);
				}
			} finally {
				bos.close();
			}
		} finally {
			fileOutputStream.close();
		}
	}		
	// END EXTRA CODE
}
