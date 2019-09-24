package org.epics.archiverappliance.mgmt;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import org.epics.archiverappliance.config.ConfigServiceForTests;
import org.epics.archiverappliance.config.DefaultConfigService;
import org.epics.archiverappliance.mgmt.policy.ExecutePolicy;
import org.epics.archiverappliance.mgmt.policy.PolicyConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PolicyExecutionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSimplePolicyExecution() throws Exception {
		DefaultConfigService configService = new ConfigServiceForTests(new File("./src/sitespecific/tests/classpathfiles"));
		try(InputStream is = configService.getPolicyText()) {
			HashMap<String, Object> pvInfo = new HashMap<String, Object>();
			pvInfo.put("eventRate", Float.valueOf(1.0f));
			pvInfo.put("storageRate", Float.valueOf(1.0f));
			pvInfo.put("RTYP", "ai");
			try(ExecutePolicy executePolicy = new ExecutePolicy(configService)) { 
				PolicyConfig policyConfig = executePolicy.computePolicyForPV("test", pvInfo);
				assertTrue("policyConfig is null", policyConfig != null);
				assertTrue("dataStores is null", policyConfig.getDataStores() != null && policyConfig.getDataStores().length > 1);
			}
		}
	}
	
	@Test
	public void testForLeaks() throws Exception {
		DefaultConfigService configService = new ConfigServiceForTests(new File("./src/sitespecific/tests/classpathfiles"));
		for(int i = 0; i < 10000; i++) { 
			try(InputStream is = configService.getPolicyText()) {
				HashMap<String, Object> pvInfo = new HashMap<String, Object>();
				pvInfo.put("eventRate", Float.valueOf(1.0f));
				pvInfo.put("storageRate", Float.valueOf(1.0f));
				pvInfo.put("RTYP", "ai");
				try(ExecutePolicy executePolicy = new ExecutePolicy(configService)) { 
					PolicyConfig policyConfig = executePolicy.computePolicyForPV("test" + i, pvInfo);
					assertTrue("policyConfig is null", policyConfig != null);
					assertTrue("dataStores is null", policyConfig.getDataStores() != null && policyConfig.getDataStores().length > 1);
				}
			}
		}
	}

}
