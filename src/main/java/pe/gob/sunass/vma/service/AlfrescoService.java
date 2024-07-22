package pe.gob.sunass.vma.service;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pe.gob.sunass.vma.configuration.AlfrescoProperties;
import pe.gob.sunass.vma.dto.ArchivoDTO;
import pe.gob.sunass.vma.exception.ConflictException;
import pe.gob.sunass.vma.exception.FailledValidationException;
import pe.gob.sunass.vma.model.Archivo;
import pe.gob.sunass.vma.repository.ArchivoRepository;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;

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
        return processFile(file);
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
	    
	   //descarga

	    public ResponseEntity<Map<String, String>> downloadFile(String nodeId) {
	        String url = alfrescoProperties.getUrl() + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + nodeId + "/content";

	        HttpHeaders headers = new HttpHeaders();
	        headers.setBasicAuth(alfrescoProperties.getUser(), alfrescoProperties.getPassword());

	        HttpEntity<String> entity = new HttpEntity<>(headers);

	        try {
	            // Make the GET request to retrieve the file
	            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

	            // Extract Content-Disposition header to get the filename
	            List<String> contentDispositionList = response.getHeaders().get("Content-Disposition");
	            String fileName = "archivo.descargado"; // Default value

	            if (contentDispositionList != null && !contentDispositionList.isEmpty()) {
	                String contentDisposition = contentDispositionList.get(0);
	                Pattern pattern = Pattern.compile("filename=\"?([^\";]*)\"?");
	                Matcher matcher = pattern.matcher(contentDisposition);
	                if (matcher.find()) {
	                    fileName = matcher.group(1);
	                }
	            } else {
	                // Determine the filename based on content type if not provided
	                String contentType = response.getHeaders().getContentType().toString();
	                fileName = determineFileName(contentType);
	            }

	            // Encode the file content in Base64
	            String encodedContent = Base64.getEncoder().encodeToString(response.getBody());

	            // Create a response body with filename and content
	            Map<String, String> responseBody = Map.of(
	                "filename", fileName,
	                "content", encodedContent
	            );

	            // Set headers
	            HttpHeaders responseHeaders = new HttpHeaders();
	            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

	            // Return response entity
	            return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);
	        } catch (Exception e) {
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	        
	    }
	    
	    private String determineFileName(String contentType) {
	        switch (contentType) {
	            case "application/pdf":
	                return "documento.pdf";
	            case "application/msword":
	                return "documento.doc";
	            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
	                return "documento.docx";
	            case "application/vnd.ms-excel":
	                return "documento.xls";
	            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
	                return "documento.xlsx";
	            // Agrega más casos según sea necesario
	            default:
	                return "archivo.bin"; // Valor por defecto si no se reconoce el tipo
	        }
	    }
	    
	    //borrar archivo
	    public ResponseEntity<Void> deleteFile(String nodeId) {
	        String url = alfrescoProperties.getUrl() + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + nodeId;

	        HttpHeaders headers = new HttpHeaders();
	        headers.setBasicAuth(alfrescoProperties.getUser(), alfrescoProperties.getPassword());

	        try {
	            restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
	            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	        } catch (Exception e) {
	            // Log the error or handle it as needed
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	    
	    //process file 2, para actualizar
	   
	    @Transactional
	    public ArchivoDTO updateFile(Integer id, MultipartFile file) throws IOException {
	        validateFile(file);

	        Optional<Archivo> archivoOptional = archivoRepository.findByIdArchivo(id);
	        if (!archivoOptional.isPresent()) {
	            throw new ConflictException("Archivo con ID " + id + " no encontrado.");
	        }

	        Archivo archivo = archivoOptional.get();

	        // Check if a file with the same name already exists (optional business rule)
			//AGREGAR AL GUARDAR EL ARCHIVO CON LOS MILISECONDS ACTUALES
	        if (archivoRepository.existsByNombreArchivoAndIdArchivoNot(file.getOriginalFilename(), id)) {
	            throw new ConflictException("El archivo ya existe.");
	        }

	        // borrando el archivo anteriro del  Alfresco
	        if (archivo.getIdAlfresco() != null) {
	            deleteFileFromAlfresco(archivo.getIdAlfresco());
	        }
	        
	        // subiendo file al Alfresco
	        String alfrescoFileId = uploadFileToAlfresco(file);

	        // Update los datos en la tabla
	        archivo.setNombreArchivo(file.getOriginalFilename());
	        archivo.setIdAlfresco(alfrescoFileId);
	        archivo.setUpdatedAt(new Date());

	        archivo = archivoRepository.save(archivo);

	        ArchivoDTO archivoDTO = new ArchivoDTO();
	        archivoDTO.setIdArchivo(archivo.getIdArchivo());
	        archivoDTO.setNombreArchivo(archivo.getNombreArchivo());
	        archivoDTO.setIdAlfresco(archivo.getIdAlfresco());

	        return archivoDTO;
	    }

	    private String uploadFileToAlfresco(MultipartFile file) throws IOException {
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
	            return responseJson.get("entry").get("id").asText();
	        } else if (response.getStatusLine().getStatusCode() == 409) {
	            throw new ConflictException("Conflict occurred while updating file in Alfresco.");
	        } else {
	            throw new RuntimeException("Failed to upload file to Alfresco, status code: " + response.getStatusLine().getStatusCode());
	        }
	    }
	    
	    private void deleteFileFromAlfresco(String alfrescoFileId) throws IOException {
	        String deleteUrl = alfrescoProperties.getUrl() + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + alfrescoFileId;

	        CloseableHttpClient client = HttpClients.createDefault();
	        HttpDelete delete = new HttpDelete(deleteUrl);

	        delete.addHeader("Authorization", "Basic " + Base64.getEncoder()
	                .encodeToString((alfrescoProperties.getUser() + ":" + alfrescoProperties.getPassword()).getBytes()));

	        HttpResponse response = client.execute(delete);
	        client.close();

	        if (response.getStatusLine().getStatusCode() != 204) {
	            throw new RuntimeException("Failed to delete file from Alfresco, status code: " + response.getStatusLine().getStatusCode());
	        }
	    }
	    
	    
}
