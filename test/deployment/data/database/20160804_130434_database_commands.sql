ALTER TABLE "lucenegeneric$lucenebackup" RENAME TO "lucene$lucenebackup";
UPDATE "system$filedocument"
 SET "submetaobjectname" = 'Lucene.LuceneBackup'
 WHERE "submetaobjectname" = 'LuceneGeneric.LuceneBackup';
ALTER TABLE "lucenegeneric$samplecustomer" RENAME TO "lucene$samplecustomer";
ALTER TABLE "lucenegeneric$samplefiledocument" RENAME TO "lucene$samplefiledocument";
UPDATE "system$filedocument"
 SET "submetaobjectname" = 'Lucene.SampleFileDocument'
 WHERE "submetaobjectname" = 'LuceneGeneric.SampleFileDocument';
UPDATE "mendixsystem$entity"
 SET "entity_name" = 'Lucene.LuceneBackup', 
"table_name" = 'lucene$lucenebackup', 
"superentity_id" = '170ce49d-f29c-4fac-99a6-b55e8a3aeb39'
 WHERE "id" = 'ecdbc2ad-9cf3-4952-a5ea-e05a8ff1161b';
ALTER TABLE "lucene$samplecustomer"
	ADD "address" varchar(200) NULL;
UPDATE "mendixsystem$entity"
 SET "entity_name" = 'Lucene.SampleCustomer', 
"table_name" = 'lucene$samplecustomer', 
"superentity_id" = NULL
 WHERE "id" = 'c02fa90c-9f90-463f-b7bd-f40eaff89f79';
INSERT INTO "mendixsystem$attribute" ("id", 
"entity_id", 
"attribute_name", 
"column_name", 
"type", 
"length", 
"default_value", 
"is_auto_number")
 VALUES ('f85243c7-cd6b-434d-9e17-a11bfbe7b107', 
'c02fa90c-9f90-463f-b7bd-f40eaff89f79', 
'Address', 
'address', 
30, 
200, 
'', 
FALSE);
UPDATE "mendixsystem$entity"
 SET "entity_name" = 'Lucene.SampleFileDocument', 
"table_name" = 'lucene$samplefiledocument', 
"superentity_id" = '170ce49d-f29c-4fac-99a6-b55e8a3aeb39'
 WHERE "id" = 'ef480921-e184-44a6-b884-f21befc2ff41';
UPDATE "mendixsystem$version"
 SET "versionnumber" = '4.0.7', 
"lastsyncdate" = '20160804 13:01:59';
