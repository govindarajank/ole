<%--
 Copyright 2007 The Kuali Foundation

 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.opensource.org/licenses/ecl2.php

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="System Configuration" />
<div class="body">
    <strong>OLE</strong></br><br/>

        <portal:portalLink displayTitle="true"
                               title="Data Mapping Field Definition"
                               url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.ole.sys.businessobject.DataMappingFieldDefinition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /><br/>
        <portal:portalLink displayTitle="true"   title="External Data Sources (z39.50 connection)"
                               url="${ConfigProperties.application.url}/ole-kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.ole.describe.bo.ExternalDataSourceConfig&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&showMaintenanceLinks=true"/><br/>
        <portal:portalLink displayTitle="true"
                               title="Functional Field Description"
                               url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.ole.sys.businessobject.FunctionalFieldDescription&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /><br/>
        <portal:portalLink displayTitle="true"
                               title="Message Of The Day"
                               url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.ole.fp.businessobject.MessageOfTheDay&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /><br/>
       <portal:portalLink displayTitle="true" title="System Options"
                               url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.ole.sys.businessobject.SystemOptions&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /><br/>
        <portal:portalLink displayTitle="true" title="Search Framework Configuration"
                           url="${ConfigProperties.application.url}/ole-kr-krad/documentconfigcontroller?viewId=DocumentConfig&methodToCall=start"/> <br/>
        <portal:portalLink displayTitle="true" title="GOKb Configuration"
                           url="${ConfigProperties.application.url}/ole-kr-krad/oleGOKbConfigController?viewId=GokbConfig&methodToCall=start"/> <br/>


    <br/>
    <strong>PARAMETERS</strong> </br><br/>

        <portal:portalLink displayTitle="true" title="Namespace"
                               url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.namespace.NamespaceBo&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /><br/>
        <portal:portalLink displayTitle="true" title="Parameter"
                               url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.parameter.ParameterBo&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /><br/>
        <portal:portalLink displayTitle="true"
                               title="Parameter Component"
                               url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /><br/>
        <portal:portalLink displayTitle="true" title="Parameter Type"
                               url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.parameter.ParameterTypeBo&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /><br/>

</div>
<channel:portalChannelBottom />
