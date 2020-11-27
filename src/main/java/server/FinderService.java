package main.java.server;

import java.util.logging.Logger;

import io.grpc.stub.StreamObserver;
import the.north.winterfell.rpc.payload.FinderGrpc.FinderImplBase;
import the.north.winterfell.rpc.payload.ResultStatus;
import the.north.winterfell.rpc.payload.SearchUserRequest;
import the.north.winterfell.rpc.payload.SearchUserRequest.SearchRequestType;
import the.north.winterfell.rpc.payload.SearchUserResponse;
import the.north.winterfell.rpc.payload.User;

public class FinderService extends FinderImplBase {

	private Logger logger = Logger.getLogger(FinderService.class.getName());
	public FinderService() {
		super();
	}

	@Override
	public void repeatedSearch(SearchUserRequest request, StreamObserver<SearchUserResponse> responseObserver) {
		logger.info("Running STREAMING Service " + request.getSearchRequestType().name() + " for user Id : " + request.getUserId() + " and user Name: " + request.getUserName());
		SearchUserResponse response=  null;
		if (SearchRequestType.MULTISEARCH.equals(request.getSearchRequestType()))
		{
			for(int i = 1; i < 3; i ++)
			{
				response=buildErrorResponse(i);
			
				responseObserver.onNext(response);
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			response= buildSuccessResponse();
			responseObserver.onNext(response);
			
			responseObserver.onCompleted();
		}
		else {
			response=buildSuccessResponse();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}
	}
	
	@Override
	public void search(SearchUserRequest request, StreamObserver<SearchUserResponse> responseObserver) {
		logger.info("Running " + request.getSearchRequestType().name() + " for user Id : " + request.getUserId() + " and user Name: " + request.getUserName());
		
		SearchUserResponse response=  null;
		
			response=buildSuccessResponse();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		
	}
	
	private SearchUserResponse buildErrorResponse(int i) {
		ResultStatus resultStatus1 = null;
		if(i==1)
		resultStatus1 = ResultStatus.newBuilder().
				setErrorCode("404").
				setErrorMessage("Unable to find the user in the Winterfell Castle")
				.setLevel(1)
				.build();
		else if (i==2)
			resultStatus1  = ResultStatus.newBuilder().
				setErrorCode("404").
				setErrorMessage("Unable to find the user in the Night's Watch Wall")
				.setLevel(2)
				.build();

		return SearchUserResponse.newBuilder().addResultStatusArr(resultStatus1)
				.build();
	}
	
	private SearchUserResponse buildSuccessResponse() {
		ResultStatus resultStatus1 = ResultStatus.newBuilder().
				setErrorCode("200").
				setErrorMessage("Found Him")
				.setLevel(1)
				.build();
		User johnSnow = User.newBuilder().setAge(35).setCountry("Iron Islands").setUserId("303")
				.setUserName("John Snow").build();
		return SearchUserResponse.newBuilder().addResultStatusArr(resultStatus1)
				.setUser(johnSnow).build();
	}
}
