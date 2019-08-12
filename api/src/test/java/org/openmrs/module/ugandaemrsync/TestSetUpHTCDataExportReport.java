package org.openmrs.module.ugandaemrsync;

import org.junit.Test;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionServiceImpl;
import org.openmrs.module.ugandaemrreports.reports.SetUpHTCDataExportReport;
import org.openmrs.module.ugandaemrsync.tasks.SendRecencyDataToCentralServerTask;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SkipBaseSetup
public class TestSetUpHTCDataExportReport extends StandaloneContextSensitiveTest {
	
	private SendRecencyDataToCentralServerTask sendRecencyDataToCentralServerTask;
	
	@Autowired
	@Qualifier("reportingReportDefinitionService")
	protected ReportDefinitionService reportingReportDefinitionService;
	
	@Autowired
	private SetUpHTCDataExportReport reportManager;
	
	@Test
	public void testGetRecencyData() {
		sendRecencyDataToCentralServerTask.getRecencyData();
		assertNotNull(sendRecencyDataToCentralServerTask.getRecencyData());
	}
	
	@Test
	public void testHTCDataExport() throws Exception {
		
		//		EvaluationContext context = new EvaluationContext();
		//		context.addParameterValue("startDate", DateUtil.parseDate("2018-11-01", "yyyy-MM-dd"));
		//		context.addParameterValue("endDate", DateUtil.parseDate("2018-11-30", "yyyy-MM-dd"));
		//
		//		ReportDefinition reportDefinition = reportManager.constructReportDefinition();
		//		ReportData reportData = reportingReportDefinitionService.evaluate(reportDefinition, context);
		//
		//		System.out.println(reportData.toString());
		
		assertTrue(true);
	}
}
