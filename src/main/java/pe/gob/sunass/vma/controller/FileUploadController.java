package pe.gob.sunass.vma.controller;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pe.gob.sunass.vma.dto.ArchivoDTO;
import pe.gob.sunass.vma.exception.FailledValidationException;
import pe.gob.sunass.vma.service.AlfrescoService;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

	
	 private static Logger logger = LoggerFactory.getLogger(FileUploadController.class);
	 
		@Autowired
	    private AlfrescoService alfrescoService;
		
		

	    /*@PostMapping("/upload")
	    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
	        try {
	            String response = alfrescoService.uploadFile(file);
	            return ResponseEntity.ok(response);
	        } catch (IOException e) {
	            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
	        }
	    }*/
	    
		 @PostMapping("/upload/pdf")
		    public ResponseEntity<ArchivoDTO> uploadPdfFile(@RequestParam("file") MultipartFile file) {
		        try {
		            ArchivoDTO archivoDTO = alfrescoService.uploadFile(file);
		            return new ResponseEntity<>(archivoDTO, HttpStatus.OK);
		            
		        } catch (FailledValidationException ex) {  //BadRequestException ex
			    	
			    	throw ex;
			    } catch (Exception ex) {
				      logger.error(ex.getMessage(), ex);
				       return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
				 }
		    }

		    @PostMapping("/upload/pdf-word")
		    public ResponseEntity<ArchivoDTO> uploadPDFWordFile(@RequestParam("file") MultipartFile file) {
		        try {
		            ArchivoDTO archivoDTO = alfrescoService.uploadFile(file);
		            return new ResponseEntity<>(archivoDTO, HttpStatus.OK);
		        } catch (FailledValidationException ex) {  //BadRequestException ex
			    	
			    	throw ex;
			    } catch (Exception ex) {
				      logger.error(ex.getMessage(), ex);
				       return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
				 }
		    }

		    @PostMapping("/upload/excel")
		    public ResponseEntity<ArchivoDTO> uploadExcelFile(@RequestParam("file") MultipartFile file) {
		        try {
		            ArchivoDTO archivoDTO = alfrescoService.uploadFile(file);
		            return new ResponseEntity<>(archivoDTO, HttpStatus.OK);
		        } catch (FailledValidationException ex) {  //BadRequestException ex
			    	
			    	throw ex;
			    } catch (Exception ex) {
				      logger.error(ex.getMessage(), ex);
				       return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
				 }
		    }
	   
	    
}
