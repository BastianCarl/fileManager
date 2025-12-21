package com.example.demo;
import com.amazonaws.regions.Regions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DemoApplication {

	private static final String BUCKET_NAME = "demo";
	private static final Regions REGION = Regions.EU_CENTRAL_1;
	private static final String ACCESS_KEY = "AWS_ACCESS_KEY_ID";
	private static final String SECRET_KEY = "AWS_SECRET_ACCESS_KEY";
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
