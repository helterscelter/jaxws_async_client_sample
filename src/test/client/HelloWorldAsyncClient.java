package test.client;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.ws.*;

import example.helloWorld.jaxws.*;
import com.sun.xml.ws.client.BindingProviderProperties;

public class HelloWorldAsyncClient {
	
	private static HelloWorld_Service hwService;
	private static HelloWorld hwPort;
	static {
		hwService = new HelloWorld_Service();
		//hwService.setExecutor(new TimingThreadPool());  // limit our async calls to one thread

		hwPort = hwService.getHelloWorldSOAP();

		((BindingProvider) hwPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,"http://localhost:7001/helloWorld");
		((BindingProvider) hwPort).getRequestContext().put(BindingProviderProperties.CONNECT_TIMEOUT,0);
		((BindingProvider) hwPort).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT,0);

	}
	

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		
		
		// test the Sync call
		/*
		System.out.println("Calling SYNC api");
		String resp = hwPort.helloWorld("'Helter Scelter Thinks this is a bug'");
		System.out.println("Resp: " + resp);
		*/
		
		// now, lets call this async
		int cnt=10;
		List<Response<HelloWorldResponse>> lstResponses = new ArrayList<Response<HelloWorldResponse>>(cnt);
		for (int i=0; i< cnt; i++ ){
			// this should initiate the request via submitting to the executor for the service.
			long ts = System.currentTimeMillis();
			System.out.println(ts + "::  calling async #" + i);
			lstResponses.add(hwPort.helloWorldAsync(System.currentTimeMillis() + ":: " + i + ": 'Helter Scelter Thinks this is a bug'"));
		}
		
		// now cancel the requests
		for (int i=0; i<cnt; i++){
			long ts = System.currentTimeMillis();
			System.out.println(ts + "::  Canceling #" + i);
			lstResponses.get(i).cancel(true);

		}

		// see if the object thinks it's cancelled
		for (int i=0; i<cnt; i++){
			long ts = System.currentTimeMillis();
			Response<HelloWorldResponse> r = lstResponses.get(i);
			System.out.println(ts + ":: " + i + ": isCanceled:" + r.isCancelled());
			System.out.println(ts + ":: " + i + ": isDone:" + r.isDone());
		}
		
		// try and wait for a response
		for (int i=0; i<cnt; i++){
			long ts = System.currentTimeMillis();
			Response<HelloWorldResponse> r = lstResponses.get(i);
			try {
				HelloWorldResponse resp1 = r.get();
			} catch (Exception e) {
				System.out.println("Caught Exception for #" + i);
				e.printStackTrace(System.out);
			} 
			
		}
		
		System.out.println("Sleeping for 30 seconds");
		Thread.sleep(30000);
		System.out.println("Done sleeping, exiting");

		
	}

}
