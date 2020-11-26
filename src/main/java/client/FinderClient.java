package main.java.client;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import main.java.server.FinderService;
import the.north.winterfell.rpc.payload.FinderGrpc;
import the.north.winterfell.rpc.payload.FinderGrpc.FinderBlockingStub;
import the.north.winterfell.rpc.payload.FinderGrpc.FinderStub;
import the.north.winterfell.rpc.payload.SearchUserRequest;
import the.north.winterfell.rpc.payload.SearchUserRequest.SearchRequestType;
import the.north.winterfell.rpc.payload.SearchUserResponse;

public class FinderClient {
	private static final Logger logger = Logger.getLogger(FinderService.class.getName());
	public FinderClient() {
	
	}
	
	public static void main(String[] args) {
		
		singleBlockingSearch();
		//simulateStreaming();
	}
	
	private static void singleBlockingSearch() {
		ManagedChannel channel = 
				ManagedChannelBuilder.forTarget("192.168.1.2:9090").
				keepAliveTime(10, TimeUnit.SECONDS).
				usePlaintext()
				.build();
		
		SearchUserRequest userRequest = SearchUserRequest.newBuilder()
				.setUserName("John Snow").setUserId(303)
				.setSearchRequestType(SearchRequestType.SINGLERECORD)
				.build();
		FinderBlockingStub stub = FinderGrpc.newBlockingStub(channel);
		SearchUserResponse value = stub.search(userRequest);
		logger.info("Gt Response from Server : " + 
				value.getResultStatusArr(0).getErrorCode() + " " + 
							value.getResultStatusArr(0).getErrorMessage() );
		logger.info(value.getUser().getUserName() + " was found in : " + value.getUser().getCountry());
		channel.shutdownNow();
	}
	
	private static void simulateStreaming() {
		ManagedChannel channel = 
				ManagedChannelBuilder.forTarget("192.168.1.2:9090").
				keepAliveTime(10, TimeUnit.SECONDS).
				usePlaintext()
				.build();
		
		SearchUserRequest userRequest = SearchUserRequest.newBuilder()
				.setUserName("John Snow").setUserId(303)
				.setSearchRequestType(SearchRequestType.MULTISEARCH)
				.build();
		FinderStub stub = FinderGrpc.newStub(channel);
		
		stub.repeatedSearch(userRequest, new StreamObserver<SearchUserResponse>() {
			
			@Override
			public void onNext(SearchUserResponse value) {
				logger.info("Gt Response from Server : " + 
			value.getResultStatusArr(0).getErrorCode() + " " + 
						value.getResultStatusArr(0).getErrorMessage() );
				if(value.getResultStatusArr(0).getErrorCode() .equals("200"))
					logger.info(value.getUser().getUserName() + " was found in : " + value.getUser().getCountry());
			}
			
			@Override
			public void onError(Throwable t) {
				logger.severe("Exception in Client  : " + t);
				t.printStackTrace();
				
			}
			
			@Override
			public void onCompleted() {
				// TODO Auto-generated method stub
				logger.info("End of transmission from Server");
				channel.shutdownNow();
			}
		});
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
