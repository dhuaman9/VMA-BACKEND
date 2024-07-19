package pe.gob.sunass.vma.service;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pe.gob.sunass.vma.configuration.AlfrescoProperties;
import pe.gob.sunass.vma.dto.ArchivoDTO;
import pe.gob.sunass.vma.exception.FailledValidationException;
import pe.gob.sunass.vma.model.Archivo;
import pe.gob.sunass.vma.repository.ArchivoRepository;
import java.util.Base64;
import java.util.Date;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class AlfrescoService {

	@Autowired
	AlfrescoProperties alfrescoProperties;
	
	@Autowired
    ArchivoRepository archivoRepository;
	
	private final RestTemplate restTemplate;
	
	 public AlfrescoService(RestTemplate restTemplate) {
	        this.restTemplate = restTemplate;
	    }
	 
	
	 public ArchivoDTO uploadFile(MultipartFile file) throws IOException {
	        validateFile(file);
//	        return processFile(file);
	        return new ArchivoDTO();
	    }

	    private void validateFile(MultipartFile file) {
	        String fileType = file.getContentType();
	        long fileSize = file.getSize();

	        if ("application/pdf".equals(fileType)) {
	            validatePDF(fileSize);
	        } else if ("application/msword".equals(fileType) || "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(fileType)) {
	        	validateWordOrPDF(fileSize);
	        } else if ("application/vnd.ms-excel".equals(fileType) || "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(fileType)) {
	            validateExcel(fileSize);
	        } else {
	            throw new FailledValidationException("Unsupported file type.");
	        }
	    }

	    private void validatePDF(long fileSize) {
	        if (fileSize > 5 * 1024 * 1024) {
	            throw new FailledValidationException("El tamaño del archivo PDF excede los 5MB.");
	        }
	    }

	    private void validateWordOrPDF(long fileSize) {
	        if (fileSize > 20 * 1024 * 1024) {
	            throw new FailledValidationException("El tamaño del archivo Word o PDF excede los 20MB.");
	        }
	    }

	    private void validateExcel(long fileSize) {
	        if (fileSize > 20 * 1024 * 1024) {
	            throw new FailledValidationException("El tamaño del archivo Excel excede los 20MB.");
	        }
	    }

	    private ArchivoDTO processFile(MultipartFile file) throws IOException {
	        String uploadUrl = alfrescoProperties.getUrl() + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/"
	                + alfrescoProperties.getSpaceStore() + "/children";
	        CloseableHttpClient client = HttpClients.createDefault();
	        HttpPost post = new HttpPost(uploadUrl);

	        post.addHeader("Authorization", "Basic " + Base64.getEncoder()
	                .encodeToString((alfrescoProperties.getUser() + ":" + alfrescoProperties.getPassword()).getBytes()));
	        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	        builder.addBinaryBody("filedata", file.getInputStream(), ContentType.DEFAULT_BINARY, file.getOriginalFilename());
	        builder.addTextBody("name", file.getOriginalFilename(), ContentType.TEXT_PLAIN);
	        builder.addTextBody("relativePath", alfrescoProperties.getCarpeta(), ContentType.TEXT_PLAIN);

	        post.setEntity(builder.build());

	        HttpResponse response = client.execute(post);
	        String responseString = EntityUtils.toString(response.getEntity());
	        client.close();

	        if (response.getStatusLine().getStatusCode() == 201) {
	            ObjectMapper objectMapper = new ObjectMapper();
	            JsonNode responseJson = objectMapper.readTree(responseString);
	            String idAlfresco = responseJson.get("entry").get("id").asText();

	            Archivo archivo = new Archivo();
	            archivo.setNombreArchivo(file.getOriginalFilename());
	            archivo.setIdAlfresco(idAlfresco);
	            archivo.setCreatedAt(new Date());
	            archivo.setUpdatedAt(null);
	            archivo = archivoRepository.save(archivo);

	            ArchivoDTO archivoDTO = new ArchivoDTO();
	            archivoDTO.setIdArchivo(archivo.getIdArchivo());
	            archivoDTO.setNombreArchivo(archivo.getNombreArchivo());
	            archivoDTO.setIdAlfresco(archivo.getIdAlfresco());

	            return archivoDTO;
	        } else {
	            throw new RuntimeException("Failed to upload file, status code: " + response.getStatusLine().getStatusCode());
	        }
	    }
	    
	    //descarga de ficheros
	    public ResponseEntity<byte[]> downloadFile(String nodeId) {//ArchivoDTO  
	        String url = alfrescoProperties.getUrl() + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + nodeId + "/content";

	        HttpHeaders headers = new HttpHeaders();
	        headers.setBasicAuth(alfrescoProperties.getUser(), alfrescoProperties.getPassword());

	        HttpEntity<String> entity = new HttpEntity<>(headers);

	        try {
	            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

	            HttpHeaders responseHeaders = new HttpHeaders();
	            responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	            responseHeaders.setContentDisposition(ContentDisposition.attachment().filename("archivo.pdf").build());

	            return new ResponseEntity<>(response.getBody(), responseHeaders, HttpStatus.OK);
	        } catch (Exception e) {
	            // Log the error or handle it as needed
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	    
	    
}
