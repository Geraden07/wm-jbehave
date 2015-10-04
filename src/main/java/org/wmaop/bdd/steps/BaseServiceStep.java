package org.wmaop.bdd.steps;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.wm.app.b2b.client.ServiceException;
import com.wm.data.IData;
import com.wm.util.coder.IDataXMLCoder;
import com.wm.util.coder.InvalidDatatypeException;

public abstract class BaseServiceStep {

	protected class ServiceSplit {
		String packageName;
		String serviceName;
		ServiceSplit(String p, String s) {
			packageName = p;
			serviceName = s;
		}
	}
	
	protected IData invokeService(ExecutionContext executionContext, String serviceName, IData idata) throws ServiceException {
		ServiceSplit svc = splitQualifiedServiceName(serviceName);
		return executionContext.getConnectionContext().invoke(svc.packageName, svc.serviceName, idata);

	}
	
	protected ServiceSplit splitQualifiedServiceName(String svc) {
		int colonPos = svc.lastIndexOf(':');
		return new ServiceSplit(svc.substring(0, colonPos), svc.substring(colonPos+1));
	}

	protected InputStream streamFromClasspathResource(String fileName) {
		try {
			return this.getClass().getClassLoader().getResourceAsStream(fileName);
		} catch (Exception e) {
			throw new RuntimeException("Unable to load file '" + fileName + "' from the classpath.  Cause is: " + e.getMessage());
		}
	}

	protected String stringFromClasspathResource(String fileName) throws Exception{
		try {
			return FileUtils.readFileToString(new File(fileName));
		} catch (Exception e) {
			throw new RuntimeException("Unable to load file '" + fileName + "' from the classpath.  Cause is: " + e.getMessage());
		}
	}
	
	protected IData idataFromClasspathResource(String fileName) throws Exception {
		return new IDataXMLCoder().decode(streamFromClasspathResource(fileName));
	}

	protected String loadFromClasspathAsString(String fileName) {
		try(InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
			return IOUtils.toString(is);
		} catch (Exception e) {
			throw new RuntimeException("Unable to load " + fileName + " from the classpath");
		}
	}
	
	abstract void execute(ExecutionContext executionContext) throws Exception;
}
