package main.java.server;

import java.io.IOException;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServerStarter {
	private static Logger logger = Logger.getLogger(GrpcServerStarter.class.getName());
	public GrpcServerStarter() {
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		Server grpcServer = 
				ServerBuilder.forPort(9090).addService(new FinderService()).build();
		grpcServer.start();
		logger.info("GRPC Server Started");
		grpcServer.awaitTermination();
		
	}
}
