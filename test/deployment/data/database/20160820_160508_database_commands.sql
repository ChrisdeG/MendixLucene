CREATE TABLE "unittest$report" (
	"id" int8 NOT NULL,
	PRIMARY KEY("id"));
INSERT INTO "mendixsystem$entity" ("id", 
"entity_name", 
"table_name", 
"superentity_id")
 VALUES ('3e39bf75-4da7-43e3-9c17-931f6a43c486', 
'UnitTest.Report', 
'unittest$report', 
'170ce49d-f29c-4fac-99a6-b55e8a3aeb39');
UPDATE "mendixsystem$version"
 SET "versionnumber" = '4.0.7', 
"lastsyncdate" = '20160820 16:05:06';
