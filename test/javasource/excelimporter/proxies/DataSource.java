// This file was generated by Mendix Modeler.
//
// WARNING: Code you write here will be lost the next time you deploy the project.

package excelimporter.proxies;

public enum DataSource
{
	CellValue(new String[][] { new String[] { "en_US", "Cell Value" }, new String[] { "en_GB", "Cell Value" }, new String[] { "en_ZA", "Cell Value" }, new String[] { "nl_NL", "Cel waarde" } }),
	DocumentPropertyRowNr(new String[][] { new String[] { "en_US", "Document property - row number" }, new String[] { "en_GB", "Document property - row number" }, new String[] { "en_ZA", "Document property - row number" }, new String[] { "nl_NL", "Document eigenschap - rij nummer" } }),
	DocumentPropertySheetNr(new String[][] { new String[] { "en_US", "Document property - sheet number" }, new String[] { "en_GB", "Document property - sheet number" }, new String[] { "en_ZA", "Document property - sheet number" }, new String[] { "nl_NL", "Document eigenschap - sheet nummer" } }),
	StaticValue(new String[][] { new String[] { "en_US", "Static Value - Copy caption into the member" }, new String[] { "nl_NL", "Statische Waarrde - Kopier de titel in de Member" }, new String[] { "en_GB", "Static Value - Copy caption into the member" }, new String[] { "en_ZA", "Static Value - Copy caption into the member" } });

	private java.util.Map<String,String> captions;

	private DataSource(String[][] captionStrings)
	{
		this.captions = new java.util.HashMap<String,String>();
		for (String[] captionString : captionStrings)
			captions.put(captionString[0], captionString[1]);
	}

	public String getCaption(String languageCode)
	{
		if (captions.containsKey(languageCode))
			return captions.get(languageCode);
		return captions.get("en_US");
	}

	public String getCaption()
	{
		return captions.get("en_US");
	}
}
