package org.wmaop.bdd.steps;

import static org.junit.Assert.*;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;


public class AssertionVerifyStep extends BaseServiceStep {

	private final IData idata;
	private final int expectedInvokeCount;
	private final String assertionId;

	public AssertionVerifyStep(String assertionId, int invokeCount) {
		idata = IDataFactory.create();
		this.expectedInvokeCount = invokeCount;
		this.assertionId = assertionId;
		IDataCursor cursor = idata.getCursor();
		IDataUtil.put(cursor, ADVICE_ID, assertionId);
		cursor.destroy();
	}

	@Override
	void execute(ExecutionContext executionContext) throws Exception {
		IData pipeline = invokeService(executionContext, ASSERTION_INVOKE_COUNT, idata); // Dont require IData
		IDataCursor cursor = pipeline.getCursor();
		int actualInvokeCount = IDataUtil.getInt(cursor, "invokeCount", 0);
		assertEquals("Expected " + assertionId + " to be called " + expectedInvokeCount + " times but was called " + actualInvokeCount + " times", expectedInvokeCount, actualInvokeCount);
	}

}
