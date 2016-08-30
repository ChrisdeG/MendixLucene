ALTER TABLE "lucene$lucenebackup" RENAME COLUMN "backuptype" TO "indextype";
UPDATE "mendixsystem$attribute"
 SET "entity_id" = 'ecdbc2ad-9cf3-4952-a5ea-e05a8ff1161b', 
"attribute_name" = 'IndexType', 
"column_name" = 'indextype', 
"type" = 40, 
"length" = 9, 
"default_value" = '', 
"is_auto_number" = FALSE
 WHERE "id" = '1fba9b9c-6e12-42b9-bca1-48f9937c65dc';
UPDATE "mendixsystem$version"
 SET "versionnumber" = '4.0.7', 
"lastsyncdate" = '20160804 16:57:22';
