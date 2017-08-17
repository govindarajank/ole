-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: org/kuali/ole/3.0.5/db.changelog-20170718.xml
-- *********************************************************************

-- Lock Database
-- Changeset org/kuali/ole/3.0.5/db.changelog-20170718.xml::OLE_LOAD_KRCR_PARM_T_oracle::ole
INSERT INTO KRCR_PARM_T (NMSPC_CD, CMPNT_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, VAL, PARM_DESC_TXT, EVAL_OPRTR_CD, APPL_ID) VALUES ('OLE-DLVR', 'Deliver', 'PATRON_RECEIPT', 'OLE-9214', '1', 'CONFG', 'Patron Receipt', 'This Parameter is used to configure the subject line of patron bill notice', 'A', 'KUALI')
/

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, LIQUIBASE) VALUES ('OLE_LOAD_KRCR_PARM_T_oracle', 'ole', 'org/kuali/ole/3.0.5/db.changelog-20170718.xml', SYSTIMESTAMP, 1, '7:268996a77f05fbcdcf9a3d46275a211d', 'loadData', '', 'EXECUTED', '3.2.0')
/

-- Release Database Lock
-- Release Database Lock
