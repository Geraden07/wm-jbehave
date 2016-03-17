package org.wmaop.bdd.steps;

import org.wmaop.util.jexl.ExpressionProcessor;
import org.wmaop.util.jexl.IDataJexlContext;
import static org.junit.Assert.*;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;

public class DocumentMatchStep extends BaseServiceStep {

	private String documentReference;
	private String idataFile;
	private String documentName;

	public DocumentMatchStep(String document, String idataFile) {
		this.documentName = document;
		this.documentReference = ExpressionProcessor.escapedToEncoded(document);
		this.idataFile = idataFile;
	}

	@Override
	void execute(ExecutionContext executionContext) throws Exception {
		IData potential = idataFile == null ? IDataFactory.create() : getDocumentContents();
		IData doc = ((IDataJexlContext) new IDataJexlContext(executionContext.getPipeline()).get(documentReference))
				.toIData();
		if (!matches(doc, potential, documentName, true)) {
			fail("Failed to match " + documentReference + " in pipeline with file " + idataFile);
		}
	}

	private IData getDocumentContents() throws Exception {
		IData idreturn;
		IData idata = idataFromClasspathResource(idataFile);
		IDataCursor idc = idata.getCursor();
		if (IDataUtil.size(idc) == 1) {
			IData id = IDataUtil.getIData(idc, documentName);
			if (id != null) {
				idreturn = id;
			} else {
				idreturn = idata;
			}
		} else {
			idreturn = idata;
		}
		idc.destroy();
		return idreturn;
	}

	boolean matches(IData document, IData potential, String prefix, boolean reportFail) {
		IDataCursor pc = potential.getCursor();
		IDataCursor dc = document.getCursor();
		while (pc.next()) {
			String key = pc.getKey();
			Object docObj = IDataUtil.get(dc, key);
			Object potObj = pc.getValue();
			if (docObj instanceof IData && potObj instanceof IData) {
				if (!matches((IData) docObj, (IData) potObj, prefix + '.' + key, reportFail)) {
					if (reportFail) {
						fail("Failed to match values for " + prefix + '.' + key);
					}
					return false;
				}
			} else if (docObj instanceof IData[]) {
				IData[] potArr = (potObj instanceof IData[]) ? ((IData[]) potObj) : new IData[]{(IData) potObj}; 
				for (IData pot : potArr) {
					boolean potMatch = false;
					for (IData doc : ((IData[]) docObj)) {
						if (matches(doc, pot, prefix + '.' + key, false)) {
							potMatch = true;
							break;
						}
					}
					if (!potMatch) {
						fail("Failed to match " + prefix + '.' + key);
						return false;
					}
				}
			} else {
				if (docObj == null || potObj == null) {
					if (reportFail) {
						fail("Failed to locate element in pipeline: " + prefix + '.' + key);
					}
					return false;
				}
				Object docVal;
				if (docObj instanceof IData) {
					IDataCursor cursor = ((IData) docObj).getCursor();
					docVal = IDataUtil.get(cursor , "*body");
					cursor.destroy();
				} else {
					docVal = docObj;
				}
				if (!docVal.equals(potObj)) {
					if (reportFail) {
						fail("Element " + prefix + '.' + key + " has pipeline value of '" + docObj
								+ "' but test value of '" + potObj + "'");
					}
					return false;
				}
			}
		}
		return true;
	}
}