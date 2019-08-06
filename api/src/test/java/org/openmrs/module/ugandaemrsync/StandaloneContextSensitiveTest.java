package org.openmrs.module.ugandaemrsync;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Properties;

// @ContextConfiguration(locations = {"classpath:openmrs-servlet.xml"}, inheritLocations = false)
// @SkipBaseSetup
public abstract class StandaloneContextSensitiveTest extends BaseModuleContextSensitiveTest {
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	/**
	 * @return MS Note: use port 3306 as standard, 5538 for sandbox 5.5 mysql environment
	 */
	@Override
	public Properties getRuntimeProperties() {
//		Properties p = super.getRuntimeProperties();
		Properties p = new Properties();
	 	p.setProperty("connection.url", "jdbc:mysql://localhost:3306/ugandaemr?autoReconnect=true&sessionVariables=storage_engine%3DInnoDB&useUnicode=true&characterEncoding=UTF-8");
		p.setProperty("connection.username", "openmrs");
		p.setProperty("connection.password", "openmrs");
		p.setProperty("junit.username", "admin");
		p.setProperty("junit.password", "test");
		
		return p;
	}
	
	@Before
	public void setupForTest() throws Exception {
		if (!Context.isSessionOpen()) {
			Context.openSession();
		}
		Context.clearSession();
		authenticate();
	}
	
	//	@Override
	//	public void deleteAllData() throws Exception {
	//	}
}
