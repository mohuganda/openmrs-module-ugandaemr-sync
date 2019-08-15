package org.openmrs.module.ugandaemrsync;

import org.junit.Test;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionServiceImpl;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.ugandaemrreports.reports.SetUpHTSClientCardDataExportReport2019;
import org.openmrs.module.ugandaemrsync.tasks.SendRecencyDataToCentralServerTask;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SkipBaseSetup
public class TestSetUpHTSClientCardDataExportReport2019 extends StandaloneContextSensitiveTest {
	
	private SendRecencyDataToCentralServerTask sendRecencyDataToCentralServerTask;
	
	@Autowired
	@Qualifier("reportingReportDefinitionService")
	protected ReportDefinitionService reportingReportDefinitionService;
	
	@Autowired
	private SetUpHTSClientCardDataExportReport2019 reportManager;
	
	@Test
	public void testGetRecencyData() {
		assertNotNull(sendRecencyDataToCentralServerTask.getRecencyData());
		assertTrue(true);
	}
	
	@Test
	public void testHTSClientDataExport() throws Exception {
		//		ReportRenderer renderer;
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", DateUtil.parseDate("2018-11-01", "yyyy-MM-dd"));
		context.addParameterValue("endDate", DateUtil.parseDate("2018-11-30", "yyyy-MM-dd"));
		
		ReportDefinition reportDefinition = reportManager.constructReportDefinition();
		ReportData reportData = reportingReportDefinitionService.evaluate(reportDefinition, context);
		new CsvReportRenderer().render(reportData, "7c8301e3-439e-4084-a9f1-dad1a2b6e3c3", System.out);
		//		renderer.getRenderingModes();
		//System.out.println(reportData.getDataSets());
		//System.out.println(new CsvReportRenderer().canRender(reportDefinition));
		System.out.println(reportData.toString());
		assertTrue(true);
	}
	
}
