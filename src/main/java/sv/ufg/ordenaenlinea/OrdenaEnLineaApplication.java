package sv.ufg.ordenaenlinea;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OrdenaEnLineaApplication {

	@Value("${aws.sdk.accessKey}")
	private String accessKey;
	@Value("${aws.sdk.secretKey}")
	private String secretKey;
	@Value("${aws.sdk.region}")
	private String region;

	public static void main(String[] args) {
		SpringApplication.run(OrdenaEnLineaApplication.class, args);
	}

	@Bean
	public AmazonS3 s3() {
		AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

		return AmazonS3ClientBuilder
				.standard()
				.withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
				.build();
	}
}
