package org.wmaop.bdd.jbehave;

import java.io.IOException;

import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.context.Context;
import org.wmaop.bdd.steps.BddTestBuilder;
import org.wmaop.bdd.steps.ExecutionContext;

import com.wm.app.b2b.client.ServiceException;

public class WmJBehaveSteps  {

		private static final String EMPTY_IDATA = "<IDataXMLCoder version=\"1.0\"></IDataXMLCoder>";
		private BddTestBuilder testBuilder;
		
		@BeforeScenario
		public void init() throws IOException, ServiceException {
			try {
				testBuilder = new BddTestBuilder(new ExecutionContext("src/test/resources/feature.properties"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@When("invoke $serviceName with $idataFile")
		public void invoke_service(final String serviceName, String idataFile) throws Exception {
			testBuilder.withInvokeService(serviceName, idataFile);
		}
		

		@Given("invoke $serviceName")
		public void invoke_service(final String serviceName) throws Exception {
			testBuilder.withInvokeService(serviceName, EMPTY_IDATA);
		}

		@Given("mock $serviceName always returning $idataFile")
		public void mock_service_always_returning(Context context, String serviceName, String idataFile) throws Exception {
			System.out.println("Context story is " +context.getCurrentStory());
			testBuilder.withMockService("adviceId", "interceptPoint", serviceName, idataFile);
		}

		
		@Given("mock $serviceName returning $idataFile when $jexlPipelineExpression")
		public void mock_service_returning_when(String serviceName, String idataFile, String jexlPipelineExpression)
				throws Throwable {
			testBuilder.withMockService("adviceId", "interceptPoint",serviceName, idataFile, jexlPipelineExpression);
		}
		
		@Given("$assertionId assertion $interceptPoint service $service when $expression")
		public void assertion_service_when(String assertionId, InterceptPoint interceptPoint, String serviceName, String expression) {
                           			
		}
		@Given("$assertionId assertion $invokePosition service $service always")
		public void assertion_service(String assertionId, String invokePosition, String serviceName, String expression) {
                           			
		}

		@Then("assertion $assertionId was invoked $invokeCount times")
		public void assertion_was_invoked_times(String assertionId, int invokeCount) throws Throwable {
			
		}
		
		@Then("service $serviceName was invoked $invokeCount times")
		public void service_was_invoked_times(@Named("serviceName") String serviceName, @Named("invokeCount") int invokeCount) throws Throwable {
			System.out.println("verifying service" +serviceName+" was invoked "+invokeCount );
			// Register assertion
		}

		@Then("pipeline has $jexlExpression")
		public void pipeline_has_foo_data(String jexlExpression) throws Throwable {
			testBuilder.withPipelineExpression(jexlExpression);
		}
		
		@Then("exception $exception was thrown")
		public void exception_was_thrown(String exceptionName) {
			System.out.println("Exception was thrown");
		}
}
