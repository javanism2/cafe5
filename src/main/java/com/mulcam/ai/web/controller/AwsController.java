package com.mulcam.ai.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

@RestController
public class AwsController {
	
	HashMap<String,ArrayList<String> > map=new HashMap<String,ArrayList<String> >();
	
	@PostMapping("aws")
	public String upload(@RequestParam("file") MultipartFile file,HttpSession session) {
		System.out.println(file);
		
		try {
			String fileName=file.getOriginalFilename();
			file.transferTo(new File("d:\\temp\\"+fileName));
			
			//필요 시 AWS S3 Bucket에 이미지 업로드
			//aws_photo_upload(fileName);
			
			//얼굴 비교
			//String user_id=(String) session.getAttribute("user_id");
			String user_id="jes";
			ArrayList<String> list=map.get(user_id);
			
			String returnMsg=null;
			if(list==null) {//처음인 상황
				list=new ArrayList<String>();
				list.add(fileName);
				map.put(user_id, list);				
			}else if(list.size()==1){
				list.add(fileName);
				returnMsg=compareFaces(user_id);
			}
			return returnMsg==null ? "upload ok" : returnMsg ;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "upload fail!!!";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "upload fail!!!";
		} catch(Exception e) {
			e.printStackTrace();
			return "upload fail!!!";
		}
	}
	
	//s3 bucket에 이미지 저장
	public  void aws_photo_upload(String fileName) throws IOException {
        Regions clientRegion = Regions.AP_NORTHEAST_2;
        String bucketName = "cafe2jes1";
        String stringObjKeyName = fileName;
        String fileObjKeyName = fileName;
        fileName ="d:\\temp\\"+fileName;
        

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
	
	public String compareFaces(String user_id) throws Exception{
	     Float similarityThreshold = 70F;
	     
	     ArrayList<String> list=map.get(user_id);
	   
	     String sourceImage = "d:\\temp\\"+list.get(0);
	     String targetImage = "d:\\temp\\"+list.get(1);
	     list=null;
	     map.remove(user_id);
	     
	     ByteBuffer sourceImageBytes=null;
	     ByteBuffer targetImageBytes=null;

	     AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

	     //Load source and target images and create input parameters
	     try (InputStream inputStream = new FileInputStream(new File(sourceImage))) {
	        sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
	     }
	     catch(Exception e)
	     {
	         System.out.println("Failed to load source image " + sourceImage);
	         System.exit(1);
	     }
	     try (InputStream inputStream = new FileInputStream(new File(targetImage))) {
	         targetImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
	     }
	     catch(Exception e)
	     {
	         System.out.println("Failed to load target images: " + targetImage);
	         System.exit(1);
	     }

	     Image source=new Image()
	          .withBytes(sourceImageBytes);
	     Image target=new Image()
	          .withBytes(targetImageBytes);

	     CompareFacesRequest request = new CompareFacesRequest()
	             .withSourceImage(source)
	             .withTargetImage(target)
	             .withSimilarityThreshold(similarityThreshold);

	     // Call operation
	     CompareFacesResult compareFacesResult=rekognitionClient.compareFaces(request);

	     float similarity=0;
	     
	     // Display results
	     List <CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
	     for (CompareFacesMatch match: faceDetails){
	       ComparedFace face= match.getFace();
	       BoundingBox position = face.getBoundingBox();
	       similarity=match.getSimilarity();
	       System.out.println("Face at " + position.getLeft().toString()
	             + " " + position.getTop()
	             + " matches with " + match.getSimilarity().toString()
	             + "% confidence.");

	     }
	     List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();

	     System.out.println("There was " + uncompared.size()
	          + " face(s) that did not match");
	     
	     if(similarity<60) {
	    	 return "who";
	     }
	     return "ok";
	 }

}









