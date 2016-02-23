package org.kuali.ole.dsng.rest.handler.bib;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.kuali.ole.DocumentUniqueIDPrefix;
import org.kuali.ole.constants.OleNGConstants;
import org.kuali.ole.docstore.common.constants.DocstoreConstants;
import org.kuali.ole.docstore.engine.service.storage.rdbms.pojo.BibInfoRecord;
import org.kuali.ole.docstore.engine.service.storage.rdbms.pojo.BibRecord;
import org.kuali.ole.dsng.rest.Exchange;
import org.marc4j.marc.Record;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by pvsubrah on 12/23/15.
 */
public class CreateBibHandler extends BibHandler {

    @Override
    public Boolean isInterested(String operation) {
        List<String> operationsList = getListFromJSONArray(operation);
        for (Iterator iterator = operationsList.iterator(); iterator.hasNext(); ) {
            String op = (String) iterator.next();
            if (op.equals("111") || op.equals("211")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void process(JSONObject requestJsonObject, Exchange exchange) {
        String newBibContent = null;
        try {

            BibRecord bibRecord = (BibRecord) exchange.get(OleNGConstants.BIB);

            if (StringUtils.isBlank(bibRecord.getBibId())) {
                newBibContent = requestJsonObject.getString(OleNGConstants.MODIFIED_CONTENT);
                String createdBy = requestJsonObject.getString(OleNGConstants.UPDATED_BY);
                String createdDateString = (String) requestJsonObject.get(OleNGConstants.UPDATED_DATE);

                bibRecord.setContent(newBibContent);
                bibRecord.setCreatedBy(createdBy);
                bibRecord.setUniqueIdPrefix(DocumentUniqueIDPrefix.PREFIX_WORK_BIB_MARC);

                Timestamp createdDate = getDateTimeStamp(createdDateString);

                bibRecord.setDateCreated(createdDate);
                BibRecord createdBibRecord = getBibDAO().save(bibRecord);

                String modifiedcontent = process001And003(newBibContent, createdBibRecord.getBibId());
                bibRecord.setContent(modifiedcontent);

                setDataMappingValues(bibRecord, requestJsonObject, exchange);

                getBibDAO().save(bibRecord);

                saveBibInfoRecord(bibRecord,true);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}