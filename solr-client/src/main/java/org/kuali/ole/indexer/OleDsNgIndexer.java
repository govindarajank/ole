package org.kuali.ole.indexer;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.kuali.ole.dao.OleMemorizeService;
import org.kuali.ole.util.HelperUtil;
import org.kuali.ole.util.MarcUtil;
import org.kuali.ole.util.ReportUtil;

import java.text.DateFormat;
import java.util.*;

/**
 * Created by SheikS on 11/26/2015.
 */
public abstract class OleDsNgIndexer extends MarcUtil {

    private SolrRequestReponseHandler solrRequestReponseHandler;
    public static final String ID_FIELD_PREFIX = "id_disc_";
    private OleMemorizeService oleMemorizeService;
    protected ReportUtil reportUtil;

    public abstract void deleteDocument(String id);

    public abstract SolrInputDocumentAndDocumentMap buildSolrInputDocument(Object object, Map<String, SolrInputDocument> parameterMap);

    protected void assignUUIDs(SolrInputDocument solrInputDocument) throws Exception {
        SolrInputField idField = solrInputDocument.getField(ID);
        String uuid = null;
        if (null == idField) {
            if (null == uuid) {
                // Generate UUID.
                uuid = UUID.randomUUID().toString();
                uuid = ID_FIELD_PREFIX + uuid; // identifies the uuid generated by discovery module.
            }
            solrInputDocument.addField(ID, uuid);
            solrInputDocument.addField(UNIQUE_ID, uuid);
        } else {
            if (null != uuid) {
                // Use the supplied UUID.
                solrInputDocument.setField(ID, uuid);
                solrInputDocument.setField(UNIQUE_ID, uuid);
            } else {

                if (idField.getValue() instanceof List) {
                    List<String> uuidList = (List<String>) idField.getValue();
                    uuid = uuidList.get(0);
                } else if (idField.getValue() instanceof String) {
                    uuid = (String) idField.getValue();
                }

                if (null == uuid) {
                    // Generate UUID.
                    uuid = UUID.randomUUID().toString();
                    uuid = ID_FIELD_PREFIX + uuid; // identifies the uuid generated by discovery module.
                    idField.setValue(uuid, 1.0f);
                }
                SolrInputField uniqueIdField = solrInputDocument.getField(UNIQUE_ID);
                if (null == uniqueIdField) {
                    solrInputDocument.setField(UNIQUE_ID, uuid);
                } else {
                    if (uniqueIdField.getValue() == null) {
                        solrInputDocument.setField(UNIQUE_ID, uuid);
                    }
                }
            }
        }
    }

    protected void assignUUIDs(List<SolrInputDocument> solrDocs, List<String> uuids) throws Exception {
        if ((null == solrDocs) || (solrDocs.size() == 0)) {
            return;
        }
        if ((null != uuids) && (uuids.size() < solrDocs.size())) {
            throw new Exception(
                    "Insufficient UUIDs(" + uuids.size() + ") specified for documents(" + solrDocs.size() + ".");
        }
        for (int i = 0; i < solrDocs.size(); i++) {
            SolrInputDocument solrInputDocument = solrDocs.get(i);
            SolrInputField idField = solrInputDocument.getField(ID);
            String uuid = null;
            if (null != uuids) {
                // Get the supplied UUID.
                uuid = uuids.get(i);
            }
            if (null == idField) {
                if (null == uuid) {
                    // Generate UUID.
                    uuid = UUID.randomUUID().toString();
                    uuid = ID_FIELD_PREFIX + uuid; // identifies the uuid generated by discovery module.
                }
                solrInputDocument.addField(ID, uuid);
                solrInputDocument.addField(UNIQUE_ID, uuid);
            } else {
                if (null != uuid) {
                    // Use the supplied UUID.
                    solrInputDocument.setField(ID, uuid);
                    solrInputDocument.setField(UNIQUE_ID, uuid);
                } else {

                    if (idField.getValue() instanceof List) {
                        List<String> uuidList = (List<String>) idField.getValue();
                        uuid = uuidList.get(0);
                    } else if (idField.getValue() instanceof String) {
                        uuid = (String) idField.getValue();
                    }

                    if (null == uuid) {
                        // Generate UUID.
                        uuid = UUID.randomUUID().toString();
                        uuid = ID_FIELD_PREFIX + uuid; // identifies the uuid generated by discovery module.
                        idField.setValue(uuid, 1.0f);
                    }
                    SolrInputField uniqueIdField = solrInputDocument.getField(UNIQUE_ID);
                    if (null == uniqueIdField) {
                        solrInputDocument.setField(UNIQUE_ID, uuid);
                    } else {
                        if (uniqueIdField.getValue() == null) {
                            solrInputDocument.setField(UNIQUE_ID, uuid);
                        }
                    }
                }
            }
        }
    }

    public List getSolrDocumentBySolrId(String uniqueId) {
        String query = "(" + UNIQUE_ID + ":" + uniqueId + ")";
        return getSolrRequestReponseHandler().retriveResults(query);
    }

    protected SolrDocumentList getSolrDocumentByUUID(String identifier) {
        String query = "id:" + identifier;
        return getSolrRequestReponseHandler().getSolrDocumentList(query);
    }

    public void commitDocumentToSolr(List<SolrInputDocument> solrInputDocuments){
        getSolrRequestReponseHandler().updateSolr(solrInputDocuments);
    }

    public void deleteBibDocumentFromSolr(String bibId) {
        String query = BIB_ID + bibId;
        getSolrRequestReponseHandler().deleteFromSolr(query);
    }

    public UpdateResponse indexDeletedBibInfoToSolr(String bibId) {
        String newId = bibId + "_d";
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.setField(DOC_TYPE, BIBLIOGRAPHIC_DELETE);
        solrInputDocument.setField(DATE_UPDATED, new Date());
        solrInputDocument.setField(UNIQUE_ID, newId);
        solrInputDocument.setField(ID, newId);
        solrInputDocument.setField(LOCALID_DISPLAY, DocumentLocalId.getDocumentIdDisplay(bibId));
        return getSolrRequestReponseHandler().updateSolr(Collections.singletonList(solrInputDocument), false);
    }

    public void buildSolrInputDocFromSolrDoc(Map<String,Object> solrFieldMap, SolrInputDocument solrInputDocument) {
        if (solrFieldMap != null && solrFieldMap.size() > 0) {
            Set<String> resultField = solrFieldMap.keySet();
            for (Iterator<String> iterator1 = resultField.iterator(); iterator1.hasNext(); ) {
                String key = iterator1.next();
                if (!key.equalsIgnoreCase(_VERSION_)) {
                    Object value = solrFieldMap.get(key);
                    solrInputDocument.addField(key, value);
                }
            }
        }
    }

    public SolrInputDocument buildSolrInputDocFromSolrDoc(SolrDocument solrDocument) {
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        if (null != solrDocument) {
            Map<String, Collection<Object>> solrDocMap = solrDocument.getFieldValuesMap();
            if (solrDocMap != null && solrDocMap.size() > 0) {
                Set<String> keySet = solrDocMap.keySet();
                for (Iterator<String> iterator1 = keySet.iterator(); iterator1.hasNext(); ) {
                    String key = iterator1.next();
                    if (!key.equalsIgnoreCase(_VERSION_)) {
                        Object value = solrDocMap.get(key);
                        solrInputDocument.addField(key, value);
                    }
                }
            }
        }
        return solrInputDocument;
    }

    protected void removeFieldFromSolrInputDocument(SolrInputDocument holdingsSolrInputDoc) {
        holdingsSolrInputDoc.removeField(TITLE_SORT);
        holdingsSolrInputDoc.removeField(TITLE_SEARCH);
        holdingsSolrInputDoc.removeField(AUTHOR_SEARCH);
        holdingsSolrInputDoc.removeField(PUBLISHER_SEARCH);
        holdingsSolrInputDoc.removeField(ISSN_SEARCH);
        holdingsSolrInputDoc.removeField(ISBN_SEARCH);
        holdingsSolrInputDoc.removeField(FORMAT_SEARCH);
        holdingsSolrInputDoc.removeField(LANGUAGE_SEARCH);
        holdingsSolrInputDoc.removeField(PUBLICATIONDATE_SEARCH);
        holdingsSolrInputDoc.removeField(TITLE_DISPLAY);
        holdingsSolrInputDoc.removeField(AUTHOR_DISPLAY);
        holdingsSolrInputDoc.removeField(PUBLISHER_DISPLAY);
        holdingsSolrInputDoc.removeField(ISSN_DISPLAY);
        holdingsSolrInputDoc.removeField(ISBN_DISPLAY);
        holdingsSolrInputDoc.removeField(FORMAT_DISPLAY);
        holdingsSolrInputDoc.removeField(LANGUAGE_DISPLAY);
        holdingsSolrInputDoc.removeField(PUBLICATIONDATE_DISPLAY);
        holdingsSolrInputDoc.removeField(HOLDINGS_CALLNUMBER_DISPLAY);
        holdingsSolrInputDoc.removeField(HOLDINGS_CALLNUMBER_SEARCH);
        holdingsSolrInputDoc.removeField(HOLDINGS_LOCATION_DISPLAY);
        holdingsSolrInputDoc.removeField(HOLDINGS_LOCATION_SEARCH);
    }

    protected void addBibInfoForHoldingsOrItems(SolrInputDocument destinationSolrInputDocument, SolrInputDocument sourceSolrInputDocument) {
        destinationSolrInputDocument.addField(TITLE_SEARCH, sourceSolrInputDocument.getFieldValues(TITLE_SEARCH));
        destinationSolrInputDocument.addField(TITLE_SORT, sourceSolrInputDocument.getFieldValues(TITLE_SORT));
        destinationSolrInputDocument.addField(AUTHOR_SEARCH, sourceSolrInputDocument.getFieldValues(AUTHOR_SEARCH));
        destinationSolrInputDocument.addField(PUBLICATIONPLACE_DISPLAY, sourceSolrInputDocument.getFieldValues(PUBLICATIONPLACE_DISPLAY));
        destinationSolrInputDocument.addField(PUBLISHER_SEARCH, sourceSolrInputDocument.getFieldValues(PUBLISHER_SEARCH));
        destinationSolrInputDocument.setField(PUBLISHER_SORT, sourceSolrInputDocument.getFieldValues(PUBLISHER_SORT));
        destinationSolrInputDocument.addField(ISSN_SEARCH, sourceSolrInputDocument.getFieldValues(ISSN_SEARCH));
        destinationSolrInputDocument.addField(ISBN_SEARCH, sourceSolrInputDocument.getFieldValues(ISBN_SEARCH));
        destinationSolrInputDocument.addField(FORMAT_SEARCH, sourceSolrInputDocument.getFieldValues(FORMAT_SEARCH));
        destinationSolrInputDocument.addField(LANGUAGE_SEARCH, sourceSolrInputDocument.getFieldValues(LANGUAGE_SEARCH));
        destinationSolrInputDocument.addField(PUBLICATIONDATE_SEARCH, sourceSolrInputDocument.getFieldValues(PUBLICATIONDATE_SEARCH));
        destinationSolrInputDocument.addField(MDF_035A, sourceSolrInputDocument.getFieldValues(MDF_035A));
        destinationSolrInputDocument.addField(TITLE_DISPLAY, sourceSolrInputDocument.getFieldValues(TITLE_DISPLAY));
        destinationSolrInputDocument.addField(AUTHOR_DISPLAY, sourceSolrInputDocument.getFieldValues(AUTHOR_DISPLAY));
        destinationSolrInputDocument.addField(PUBLISHER_DISPLAY, sourceSolrInputDocument.getFieldValues(PUBLISHER_DISPLAY));
        destinationSolrInputDocument.addField(ISSN_DISPLAY, sourceSolrInputDocument.getFieldValues(ISSN_DISPLAY));
        destinationSolrInputDocument.addField(ISBN_DISPLAY, sourceSolrInputDocument.getFieldValues(ISBN_DISPLAY));
        destinationSolrInputDocument.addField(FORMAT_DISPLAY, sourceSolrInputDocument.getFieldValues(FORMAT_DISPLAY));
        destinationSolrInputDocument.addField(LANGUAGE_DISPLAY, sourceSolrInputDocument.getFieldValues(LANGUAGE_DISPLAY));
        destinationSolrInputDocument.addField(PUBLICATIONDATE_DISPLAY, sourceSolrInputDocument.getFieldValues(PUBLICATIONDATE_DISPLAY));
    }

    protected void addHoldingsInfoToItem(SolrInputDocument solrInputDocument, SolrInputDocument sourceInputDocument) {
        solrInputDocument.addField(HOLDINGS_LOCATION_SEARCH, sourceInputDocument.getFieldValue(LOCATION_LEVEL_SEARCH));
        solrInputDocument.addField(HOLDINGS_CALLNUMBER_SEARCH, sourceInputDocument.getFieldValue(CALL_NUMBER_SEARCH));
        solrInputDocument.addField(HOLDINGS_LOCATION_DISPLAY, sourceInputDocument.getFieldValue(LOCATION_LEVEL_DISPLAY));
        solrInputDocument.addField(HOLDINGS_CALLNUMBER_DISPLAY, sourceInputDocument.getFieldValue(CALL_NUMBER_DISPLAY));
        solrInputDocument.addField(HOLDINGS_COPYNUMBER_DISPLAY, sourceInputDocument.getFieldValue(COPY_NUMBER_DISPLAY));
        solrInputDocument.addField(HOLDINGS_COPYNUMBER_SEARCH, sourceInputDocument.getFieldValue(COPY_NUMBER_SEARCH));
        solrInputDocument.addField(HOLDINGS_CALLNUMBER_PREFIX_SEARCH, sourceInputDocument.getFieldValue(CALL_NUMBER_PREFIX_SEARCH));
        solrInputDocument.addField(HOLDINGS_CALLNUMBER_PREFIX_DISPLAY, sourceInputDocument.getFieldValue(CALL_NUMBER_PREFIX_DISPLAY));
        solrInputDocument.addField(HOLDINGS_SHELVING_SCHEME_CODE_SEARCH, sourceInputDocument.getFieldValue(SHELVING_SCHEME_CODE_SEARCH));
        solrInputDocument.addField(HOLDINGS_SHELVING_SCHEME_CODE_DISPLAY, sourceInputDocument.getFieldValue(SHELVING_SCHEME_CODE_DISPLAY));
        solrInputDocument.addField(HOLDINGS_SHELVING_SCHEME_VALUE_SEARCH, sourceInputDocument.getFieldValue(SHELVING_SCHEME_VALUE_SEARCH));
        solrInputDocument.addField(HOLDINGS_SHELVING_SCHEME_VALUE_DISPLAY, sourceInputDocument.getFieldValue(SHELVING_SCHEME_VALUE_DISPLAY));
    }


    public void addLocationLevelsToSolrInputodument(String locationName, String locationLevel, SolrInputDocument solrInputDocument, StringBuffer loactionLevelStr) {
        if (StringUtils.isNotBlank(locationLevel)) {
            if (LOCATION_LEVEL_INSTITUTION.equalsIgnoreCase(locationLevel)) {
                solrInputDocument.addField(LEVEL1LOCATION_DISPLAY, locationName);
                solrInputDocument.addField(LEVEL1LOCATION_SEARCH, locationName);
                appendData(loactionLevelStr,locationName.replace("-",""));
            } else if (LOCATION_LEVEL_CAMPUS.equalsIgnoreCase(locationLevel)) {
                solrInputDocument.addField(LEVEL2LOCATION_DISPLAY, locationName);
                solrInputDocument.addField(LEVEL2LOCATION_SEARCH, locationName);
                appendData(loactionLevelStr,locationName.replace("-",""));
            } else if (LOCATION_LEVEL_LIBRARY.equalsIgnoreCase(locationLevel)) {
                solrInputDocument.addField(LEVEL3LOCATION_DISPLAY, locationName);
                solrInputDocument.addField(LEVEL3LOCATION_SEARCH, locationName);
                appendData(loactionLevelStr,locationName.replace("-",""));
            } else if (LOCATION_LEVEL_COLLECTION.equalsIgnoreCase(locationLevel)) {
                solrInputDocument.addField(LEVEL4LOCATION_DISPLAY, locationName);
                solrInputDocument.addField(LEVEL4LOCATION_SEARCH, locationName);
                appendData(loactionLevelStr,locationName.replace("-",""));
            } else if (LOCATION_LEVEL_SHELVING.equalsIgnoreCase(locationLevel) || LOCATION_LEVEL_SHELVING_1.equalsIgnoreCase(locationLevel)) {
                solrInputDocument.addField(LEVEL5LOCATION_DISPLAY, locationName);
                solrInputDocument.addField(LEVEL5LOCATION_SEARCH, locationName);
                appendData(loactionLevelStr,locationName.replace("-",""));
            }
        }
    }

    public void appendData(StringBuffer stringBuffer, String data) {
        if(StringUtils.isNotEmpty(data)) {
            stringBuffer.append(data);
            stringBuffer.append(" ");
        }
    }

    protected void addDetails(SolrInputDocument sourceSolrInputDocument, SolrInputDocument destinationSolrInputDocument, String docfiled) {
        List<Object> docFieldValues = new ArrayList<Object>();
        if(destinationSolrInputDocument.containsKey(docfiled)){
            docFieldValues = (List<Object>)  destinationSolrInputDocument.getFieldValues(docfiled);
        }
        if(!docFieldValues.contains(sourceSolrInputDocument.getFieldValue(docfiled)) && null != sourceSolrInputDocument.getFieldValue(docfiled)){
            docFieldValues.add(sourceSolrInputDocument.getFieldValue(docfiled));
        }

        destinationSolrInputDocument.setField(docfiled, docFieldValues);
    }

    public Map addSolrInputDocumentToMap(Map parameterMap, SolrInputDocument solrInputDocument) {
        if(null == parameterMap){
            parameterMap = new HashedMap();
        }
        parameterMap.put(solrInputDocument.getFieldValue(ID),solrInputDocument);
        return parameterMap;
    }

    public SolrInputDocument getSolrInputDocumentFromMap(Map parameterMap, String uuid) {
        if(null == parameterMap){
            parameterMap = new HashedMap();
        }
        return (SolrInputDocument) parameterMap.get(uuid);
    }

    public List<SolrInputDocument> getSolrInputDocumentListFromMap(Map<String, SolrInputDocument> parameterMap) {
        List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();
        if (null != parameterMap) {
            for (Iterator<String> iterator = parameterMap.keySet().iterator(); iterator.hasNext(); ) {
                String key = iterator.next();
                solrInputDocuments.add(parameterMap.get(key));
            }
        }
        return solrInputDocuments;
    }


    public SolrRequestReponseHandler getSolrRequestReponseHandler() {
        if(null == solrRequestReponseHandler) {
            solrRequestReponseHandler = new SolrRequestReponseHandler();
        }
        return solrRequestReponseHandler;
    }

    public void setSolrRequestReponseHandler(SolrRequestReponseHandler solrRequestReponseHandler) {
        this.solrRequestReponseHandler = solrRequestReponseHandler;
    }

/*    public OleDsHelperUtil getOleDsHelperUtil() {
        if(null == oleDsHelperUtil){
            oleDsHelperUtil = new OleDsHelperUtil();
        }
        return oleDsHelperUtil;
    }*/

    public static String convertDateToString(DateFormat dateFormat, Date date) {
        if (null != date) {
            try {
                return dateFormat.format(date);
            } catch(Exception e) {
                System.out.println("Problemed DAte : " +date);
                e.printStackTrace();
            }
        }
        return null;
    }

    public Long getLongValue(Integer value) {
        if(null == value) {
            return null;
        }
        return Long.valueOf(value);
    }

    public String getStringValue(Integer value) {
        if(null == value) {
            return null;
        }
        return String.valueOf(value);
    }

    public boolean getBooleanValueYorN(String value) {
        if(null == value) {
            return false;
        }
        return value.equalsIgnoreCase("Y") ? true : false;
    }

    public OleMemorizeService getOleMemorizeService() {
        if(null == oleMemorizeService) {
            System.out.println("********* Memoize Reinitialize ********* ");
            oleMemorizeService = HelperUtil.getBean(OleMemorizeService.class);
        }
        return oleMemorizeService;
    }

    public void setOleMemorizeService(OleMemorizeService oleMemorizeService) {
        this.oleMemorizeService = oleMemorizeService;
    }

    public ReportUtil getReportUtil() {
        if(null ==  reportUtil) {
            reportUtil = new ReportUtil();
        }
        return reportUtil;
    }

    public void setReportUtil(ReportUtil reportUtil) {
        this.reportUtil = reportUtil;
    }
}
