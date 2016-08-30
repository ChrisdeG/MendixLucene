CREATE TABLE "unittest$customer" (
	"id" int8 NOT NULL,
	"address" varchar(200) NULL,
	"city" varchar(200) NULL,
	"name" varchar(200) NULL,
	PRIMARY KEY("id"));
INSERT INTO "mendixsystem$entity" ("id", 
"entity_name", 
"table_name")
 VALUES ('27e2404e-a3ed-4374-a2cf-2f819a2c4b65', 
'UnitTest.Customer', 
'unittest$customer');
INSERT INTO "mendixsystem$attribute" ("id", 
"entity_id", 
"attribute_name", 
"column_name", 
"type", 
"length", 
"default_value", 
"is_auto_number")
 VALUES ('1f6c4f15-4210-43ab-97ab-6448de327a11', 
'27e2404e-a3ed-4374-a2cf-2f819a2c4b65', 
'Address', 
'address', 
30, 
200, 
'', 
FALSE);
INSERT INTO "mendixsystem$attribute" ("id", 
"entity_id", 
"attribute_name", 
"column_name", 
"type", 
"length", 
"default_value", 
"is_auto_number")
 VALUES ('b99e1821-5677-4360-934a-cff122661f74', 
'27e2404e-a3ed-4374-a2cf-2f819a2c4b65', 
'City', 
'city', 
30, 
200, 
'', 
FALSE);
INSERT INTO "mendixsystem$attribute" ("id", 
"entity_id", 
"attribute_name", 
"column_name", 
"type", 
"length", 
"default_value", 
"is_auto_number")
 VALUES ('bbd3e829-c487-46aa-bac6-55f0d304b6d2', 
'27e2404e-a3ed-4374-a2cf-2f819a2c4b65', 
'Name', 
'name', 
30, 
200, 
'', 
FALSE);
UPDATE "mendixsystem$version"
 SET "versionnumber" = '4.0.7', 
"lastsyncdate" = '20160813 21:00:41';
