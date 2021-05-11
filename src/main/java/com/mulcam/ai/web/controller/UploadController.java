package com.mulcam.ai.web.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mulcam.ai.util.BreakTimeTTS;


@RestController
public class UploadController {
	
	@PostMapping("upload")
	public void upload(@RequestParam("file") MultipartFile file,HttpServletRequest request) {
		
		
		try {
//			ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
//			
//			Resource stateResource = applicationContext.getResource("classpath:static/test.txt");
//			BufferedReader readerState = new BufferedReader(new InputStreamReader(stateResource.getInputStream()));
//
//			System.out.println("============>"+readerState.readLine());			
			
			 File dir = new File("/upload");
			 if(!dir.exists()) {
			      //Creating the directory
			      boolean bool = dir.mkdir();
			      if(bool){
			         System.out.println("Directory created successfully");
			         
			      }else{
			         System.out.println("Sorry couldn’t create specified directory");
			      }
			 }
			 
			 file.transferTo(new File("/upload/"+file.getOriginalFilename()));

			
			new BreakTimeTTS().main("aaa", "bbb");
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void s3upload() throws IOException {
        Regions clientRegion = Regions.AP_NORTHEAST_2;
        String bucketName = "cafe2jes1";
        String stringObjKeyName = "target.jpg";
        String fileObjKeyName = "target.jpg";
        String fileName = "d:\\temp\\target.jpg";

        try {
            //This code expects that you have AWS credentials set up per:
            // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .build();

            // Upload a text string as a new object.
            s3Client.putObject(bucketName, stringObjKeyName, "Uploaded String Object");

            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(fileName));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("title", "someTitle");
            request.setMetadata(metadata);
            s3Client.putObject(request);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }

}