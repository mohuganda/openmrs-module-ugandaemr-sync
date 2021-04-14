package org.openmrs.module.ugandaemrsync.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.TextTemplateRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRHttpURLConnection;
import org.openmrs.module.ugandaemrsync.server.SyncGlobalProperties;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig.*;

/**
 * Posts Analytics data to the central server
 */

@Component
public class SendReportsTask extends AbstractTask {

    protected Log log = LogFactory.getLog(getClass());
    boolean sent=false;

     Date startDate;

     Date endDate;

     String reportDefinitionUuid;

    public SendReportsTask() {
        super();
    }

    public SendReportsTask(Date startDate, Date endDate, String reportDefinitionUuid) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportDefinitionUuid = reportDefinitionUuid;
    }



    UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();

    SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();


    @Override
    public void execute() {


        if (!isGpReportsServerUrlSet()) {
            return;
        }
        if (!isGpDhis2OrganizationUuidSet()) {
            return;
        }

        String reportsServerUrlEndPoint = syncGlobalProperties.getGlobalProperty(GP_SEND_NEXT_GEN_REPORTS_SERVER_URL);
        String reportsBaseUrl = ugandaEMRHttpURLConnection.getBaseURL(reportsServerUrlEndPoint);


        //Check internet connectivity
        if (!ugandaEMRHttpURLConnection.isConnectionAvailable()) {
            return;
        }

        //Check destination server availability
        if (!ugandaEMRHttpURLConnection.isServerAvailable(reportsBaseUrl)) {
            return;
        }
        log.info("Sending Report to server ");
        String bodyText = generateReport(reportDefinitionUuid,startDate,endDate);

        HttpResponse httpResponse = ugandaEMRHttpURLConnection.httpPost(reportsServerUrlEndPoint, bodyText, syncGlobalProperties.getGlobalProperty(GP_DHIS2_ORGANIZATION_UUID), syncGlobalProperties.getGlobalProperty(GP_DHIS2_ORGANIZATION_UUID));
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK || httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
            sent=true;
            log.info("Report  has been sent to central server");
        } else {
            log.info("Http response status code: " + httpResponse.getStatusLine().getStatusCode() + ". Reason: " + httpResponse.getStatusLine().getReasonPhrase()); }

    }

    private String generateReport(String uuid, Date startDate, Date endDate){
        String strOutput="";
        try{
           ReportDefinition rd= getReportDefinitionService().getDefinitionByUuid(uuid);
            if (rd == null) {
                throw new IllegalArgumentException("unable to find  report with uuid "
                        + uuid);
            }else {
                List<ReportDesign> reportDesigns = Context.getService(ReportService.class).getReportDesigns(rd, TextTemplateRenderer.class, false);
                ReportDesign reportDesign = reportDesigns.stream().filter(p -> "JSON".equals(p.getName())).findAny().orElse(null);
                System.out.println(reportDesign.getUuid());
                String reportRendergingMode = JSON_REPORT_RENDERER_TYPE + "!" + reportDesign.getUuid();
                RenderingMode renderingMode = new RenderingMode(reportRendergingMode);
                if (!renderingMode.getRenderer().canRender(rd)) {
                    throw new IllegalArgumentException("Unable to render Report with " + reportRendergingMode);
                }
                Map<String, Object> parameterValues = new HashMap<String, Object>();

                parameterValues.put("endDate", endDate);
                parameterValues.put("startDate", startDate);
                EvaluationContext context = new EvaluationContext();
                context.setParameterValues(parameterValues);
                ReportData reportData = getReportDefinitionService().evaluate(rd, context);
                ReportRequest reportRequest = new ReportRequest();
                reportRequest.setReportDefinition(new Mapped<ReportDefinition>(rd, context.getParameterValues()));
                reportRequest.setRenderingMode(renderingMode);
                File file = new File(OpenmrsUtil.getApplicationDataDirectory() + "sendReports");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                renderingMode.getRenderer().render(reportData, renderingMode.getArgument(), fileOutputStream);

                strOutput = readOutputFile(strOutput);
                log.error(strOutput);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return strOutput;
    }


    public boolean isGpReportsServerUrlSet() {
        if (isBlank(syncGlobalProperties.getGlobalProperty(GP_SEND_NEXT_GEN_REPORTS_SERVER_URL))) {
            log.info("Send Reports server URL is not set");
            return false;
        }
        return true;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getReportDefinitionUuid() {
        return reportDefinitionUuid;
    }

    public void setReportDefinitionUuid(String reportDefinitionUuid) {
        this.reportDefinitionUuid = reportDefinitionUuid;
    }

    private ReportDefinitionService getReportDefinitionService() {
        return Context.getService(ReportDefinitionService.class);
    }

    public boolean isGpDhis2OrganizationUuidSet() {
        if (isBlank(syncGlobalProperties.getGlobalProperty(GP_DHIS2_ORGANIZATION_UUID))) {
            log.info("DHIS2 Organization UUID is not set");
            return false;
        }
        return true;
    }

    public String readOutputFile(String strOutput) throws Exception {
        FileInputStream fstreamItem = new FileInputStream(OpenmrsUtil.getApplicationDataDirectory() + "sendReports");
        DataInputStream inItem = new DataInputStream(fstreamItem);
        BufferedReader brItem = new BufferedReader(new InputStreamReader(inItem));
        String phraseItem;

        if (!(phraseItem = brItem.readLine()).isEmpty()) {
            strOutput = strOutput + phraseItem + System.lineSeparator();
            while ((phraseItem = brItem.readLine()) != null) {
                strOutput = strOutput + phraseItem + System.lineSeparator();
            }
        }

        fstreamItem.close();

        return strOutput;
    }

}
