// This file was generated by Mendix Modeler 6.0.
//
// WARNING: Code you write here will be lost the next time you deploy the project.

package lucene.proxies.constants;

import com.mendix.core.Core;

public class Constants
{
	// These are the constants for the Lucene module

	public static Long getCustomersIndex()
	{
		return (Long)Core.getConfiguration().getConstantValue("Lucene.CustomersIndex");
	}

	public static Long getDocumentIndex()
	{
		return (Long)Core.getConfiguration().getConstantValue("Lucene.DocumentIndex");
	}

	/**
	* the node for logging
	*/
	public static String getlognode()
	{
		return (String)Core.getConfiguration().getConstantValue("Lucene.lognode");
	}

	/**
	* Number of index commands processed in one batch. If the number of command is reached the indexing start always.
	* Valid amounts 100-2000
	*/
	public static Long getLuceneBatchsize()
	{
		return (Long)Core.getConfiguration().getConstantValue("Lucene.LuceneBatchsize");
	}

	/**
	* Time in seconds after the index is closed and optimized. 
	* Valid values 300-10000
	*/
	public static Long getLuceneCloseTimeOutSeconds()
	{
		return (Long)Core.getConfiguration().getConstantValue("Lucene.LuceneCloseTimeOutSeconds");
	}

	/**
	* Interval after which the index queue is processed.
	* Valid values 5-30.
	* If you increase this value it will be slower and will need more memory. If you lower the value to 2 or less the overhead of the index-open/close will take more time. 
	*/
	public static Long getLuceneIntervalSeconds()
	{
		return (Long)Core.getConfiguration().getConstantValue("Lucene.LuceneIntervalSeconds");
	}

	/**
	* Number of results returned after a query.
	*/
	public static Long getLuceneMaxResultCount()
	{
		return (Long)Core.getConfiguration().getConstantValue("Lucene.LuceneMaxResultCount");
	}
}