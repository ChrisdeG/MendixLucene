ALTER TABLE "unittest$entity" RENAME TO "6c373eb33e0f4862a7c21095a39695d8";
DELETE FROM "mendixsystem$entity" 
 WHERE "id" = 'e7630e93-2797-402d-ae09-1438e47c1812';
DELETE FROM "mendixsystem$entityidentifier" 
 WHERE "id" = 'e7630e93-2797-402d-ae09-1438e47c1812';
DELETE FROM "mendixsystem$sequence" 
 WHERE "attribute_id" IN (SELECT "id"
 FROM "mendixsystem$attribute"
 WHERE "entity_id" = 'e7630e93-2797-402d-ae09-1438e47c1812');
DELETE FROM "mendixsystem$attribute" 
 WHERE "entity_id" = 'e7630e93-2797-402d-ae09-1438e47c1812';
DROP TABLE "6c373eb33e0f4862a7c21095a39695d8";
UPDATE "mendixsystem$version"
 SET "versionnumber" = '4.0.7', 
"lastsyncdate" = '20160829 10:17:32';
