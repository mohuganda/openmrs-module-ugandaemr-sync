package org.openmrs.module.ugandaemrsync.page.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.ugandaemrsync.server.SyncGlobalProperties;
import org.openmrs.module.ugandaemrsync.tasks.SendReportsTask;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig.*;

public class SendReportsPageController {

    protected final Log log = LogFactory.getLog(SendReportsPageController.class);

    SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
    String reportUuids=  syncGlobalProperties.getGlobalProperty(GP_SEND_NEXT_GEN_REPORTS_SERVER_REPORT_UUIDS);
    List<ReportDefinition> rds = getReportDefinitions(reportUuids);


    @Autowired
    SendReportsTask sendReportsTask;
    public void get(@SpringBean PageModel pageModel, @RequestParam(value = "breadcrumbOverride", required = false) String breadcrumbOverride) {
        pageModel.put("breadcrumbOverride", breadcrumbOverride);
        pageModel.put("errorMessage", "");
        pageModel.put("reportDefinitions", rds);
    }

    public void post(@SpringBean PageModel pageModel,
                     @RequestParam(value = "breadcrumbOverride", required = false) String breadcrumbOverride,
                     @RequestParam("startDate") String periodStartDate,
                     @RequestParam("endDate") String periodEndDate,
                     @RequestParam("reportDefinition") String uuid) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date startDate = dateFormat.parse(periodStartDate);
            Date endDate = dateFormat.parse(periodEndDate);
             sendReportsTask= new SendReportsTask(startDate,endDate,uuid);
            sendReportsTask.execute();
            if(sendReportsTask.isSent()){
                pageModel.put("errorMessage", "Report"+
                        getReportDefinitionService().getDefinitionByUuid(uuid).getName()+
                        "from period" + startDate + "to" + endDate + "successfully sent");
            }else{
                pageModel.put("errorMessage", "Report not sent");
            }
        }catch (ParseException e){
            pageModel.put("errorMessage", e.getMessage());
        }
        pageModel.put("breadcrumbOverride", breadcrumbOverride);
        pageModel.put("reportDefinitions", rds);
    }


    private ReportDefinitionService getReportDefinitionService() {
        return Context.getService(ReportDefinitionService.class);
    }

    private List<ReportDefinition> getReportDefinitions(String uuids){
        List<String> ids= new ArrayList<>();
        List<ReportDefinition> reportDefinitions = new ArrayList<>();
        if(!uuids.isEmpty()&& uuids!=""){
            ids = Arrays.asList(uuids.split(","));
        }
        if(ids !=null){
            for (String id:ids) {
              reportDefinitions.add(getReportDefinitionService().getDefinitionByUuid(id));
            }
        }
        return reportDefinitions;
    }

}
