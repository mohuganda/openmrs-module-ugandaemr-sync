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
}
