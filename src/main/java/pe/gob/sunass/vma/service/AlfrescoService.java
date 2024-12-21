package pe.gob.sunass.vma.service;

import java.io.IOException;
import java.time.LocalDate;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pe.gob.sunass.vma.configuration.AlfrescoProperties;
import pe.gob.sunass.vma.configuration.AlfrescoProperties.AppCredential;
import pe.gob.sunass.vma.dto.ArchivoDTO;
import pe.gob.sunass.vma.exception.FailledValidationException;
import pe.gob.sunass.vma.model.cuestionario.Archivo;
import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;
import pe.gob.sunass.vma.model.cuestionario.RespuestaVMA;
import pe.gob.sunass.vma.repository.ArchivoRepository;
import pe.gob.sunass.vma.constants.Constants;

import java.util.*;
import java.util.regex.Pattern;

import java.util.regex.Matcher;

import org.springframework.http.*;

import pe.gob.sunass.vma.repository.RespuestaVMARepository;
import pe.gob.sunass.vma.util.UserUtil;

@Service
public class AlfrescoService {

	private static Logger logger = LoggerFactory.getLogger(EmpresaService.class);

	@Autowired
	AlfrescoProperties alfrescoProperties;

	@Autowired
	ArchivoRepository archivoRepository;

	@Autowired
	private UserUtil userUtil;

	private final AppCredential appCredential;
	
	private final RestTemplate restTemplate;
	
	
	@Autowired
	private RespuestaVMARepository respuestaVMARepository;

	public AlfrescoService(@Qualifier("myAppCredentialAlfresco") AppCredential appCredential, RestTemplate restTemplate) {
		 this.appCredential = appCredential;
	     this.restTemplate = restTemplate;
	}

	
	public ArchivoDTO uploadFile(MultipartFile file, RegistroVMA registroVMA, Integer respuestaId) throws IOException {
		validateFile(file);
		return processFile(file, registroVMA, respuestaId);
		
	}

	private void validateFile(MultipartFile file) {
	    try {
	        String fileType = file.getContentType();
	        long fileSize = file.getSize();

	        if ("application/pdf".equals(fileType)) {
	            validatePDF(fileSize);
	        } else if ("application/msword".equals(fileType)
	                || "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(fileType)) {
	            validateWordOrPDF(fileSize);
	        } else if ("application/vnd.ms-excel".equals(fileType)
	                || "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(fileType)) {
	            validateExcel(fileSize);
	        } else {
	            throw new FailledValidationException("Unsupported file type.");
	        }
	    } catch (FailledValidationException e) {
	        // Manejo específico para excepciones de validación
	    	logger.info("Error de validación del archivo: " + e.getMessage());
	    	System.out.println("Error de validación del archivo: " + e.getMessage());
	        throw e; // Relanzar la excepción si necesitas propagarla
	    } catch (Exception e) {
	        // Manejo general para otras excepciones inesperadas
	    	logger.info("Error inesperado durante la validación del archivo: " + e.getMessage());
	        throw new RuntimeException("Error al validar el archivo. Por favor, intente nuevamente.", e);
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

	
	private ArchivoDTO processFile(MultipartFile file, RegistroVMA registroVMA, Integer respuestaId) throws IOException {
	    CloseableHttpClient client = null;
	    try {
	        // Obtener la fecha actual para nombrar la subcarpeta
	        LocalDate now = LocalDate.now();
	        String mesAnio = now.getMonthValue() + "-" + now.getYear();

	        // Verificar si la subcarpeta ya existe
	        String subcarpetaId = getOrCreateSubfolder(mesAnio);

	        // URL de subida del archivo a la subcarpeta creada
	        String uploadUrl = appCredential.getAlfrescoHost() + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/"
	                + subcarpetaId + "/children";

	        client = HttpClients.createDefault();
	        HttpPost post = new HttpPost(uploadUrl);

	        post.addHeader("Authorization", "Basic " + Base64.getEncoder()
	                .encodeToString((appCredential.getAlfrescoUsername() + ":" + appCredential.getAlfrescoPassword()).getBytes()));

	        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	        builder.addBinaryBody("filedata", file.getInputStream(), ContentType.DEFAULT_BINARY, file.getOriginalFilename());
	        builder.addTextBody("name", getFilenameWithUUID(file.getOriginalFilename()), ContentType.TEXT_PLAIN);

	        post.setEntity(builder.build());

	        HttpResponse response = client.execute(post);
	        int statusCode = response.getStatusLine().getStatusCode();
	        String responseString = EntityUtils.toString(response.getEntity());

	        if (statusCode == 201) {
	            // Procesar respuesta exitosa
	            ObjectMapper objectMapper = new ObjectMapper();
	            JsonNode responseJson = objectMapper.readTree(responseString);
	            String idAlfresco = responseJson.get("entry").get("id").asText();

	            if (Objects.isNull(respuestaId)) {
	                return mapToArchivoDTO(grabarArchivo(file.getOriginalFilename(), idAlfresco, registroVMA));
	            }

	            Optional<RespuestaVMA> respuesta = respuestaVMARepository.findById(respuestaId);

	            if (respuesta.isEmpty()) {
	                return mapToArchivoDTO(grabarArchivo(file.getOriginalFilename(), idAlfresco, registroVMA));
	            }

	            RespuestaVMA respuestaVMA = respuesta.get();
	            Archivo archivoByIdAlfresco = archivoRepository.findArchivoByIdAlfresco(respuestaVMA.getRespuesta());

	            if (Objects.nonNull(archivoByIdAlfresco)) {
	                archivoByIdAlfresco.setIdAlfresco(idAlfresco);
	                archivoByIdAlfresco.setNombreArchivo(getFilenameWithUUID(file.getOriginalFilename()));
	                archivoByIdAlfresco.setUpdatedAt(new Date());
	                return mapToArchivoDTO(archivoRepository.save(archivoByIdAlfresco));
	            }

	            return mapToArchivoDTO(grabarArchivo(file.getOriginalFilename(), idAlfresco, registroVMA));
	        } else {
	            // Capturar error de Alfresco
	        	logger.info("logger - Error al subir archivo a Alfresco. Código de estado: " + statusCode 
	                    + ". Respuesta: " + responseString);
	            throw new RuntimeException("Error al subir archivo a Alfresco. Código de estado: " + statusCode 
	                    + ". Respuesta: " + responseString);
	        }
	    } catch (IOException e) {
	    	logger.info("logger - Error de comunicación con Alfresco: " + e.getMessage());
	        throw new RuntimeException("Error de comunicación con Alfresco: " + e.getMessage(), e);
	    } catch (RuntimeException e) {
	    	logger.info("logger - Error durante el procesamiento del archivo: " + e.getMessage());
	        throw new RuntimeException("Error durante el procesamiento del archivo: " + e.getMessage(), e);
	    } finally {
	        if (client != null) {
	            try {
	                client.close();
	            } catch (IOException e) {
	            	logger.info("logger - Error al cerrar el cliente HTTP: " + e.getMessage());
	                System.err.println("Error al cerrar el cliente HTTP: " + e.getMessage());
	            }
	        }
	    }
	}

	// grabando los datos del archivo a la BD, pero No al alfresco
	private Archivo grabarArchivo(String nombreArchivo, String idAlfresco, RegistroVMA registroVMA) throws IOException {
		Archivo archivo = new Archivo();
		archivo.setNombreArchivo(getFilenameWithUUID(nombreArchivo));
		archivo.setIdAlfresco(idAlfresco);
		archivo.setRegistroVma(registroVMA);
		archivo.setUsername(userUtil.getCurrentUsername());
		archivo.setIdUsuarioRegistro(userUtil.getCurrentUserId());
		archivo.setCreatedAt(new Date());
		archivo.setUpdatedAt(null);
		return archivoRepository.save(archivo);
	}

	private ArchivoDTO mapToArchivoDTO(Archivo archivo) {
		ArchivoDTO archivoDTO = new ArchivoDTO();
		archivoDTO.setIdArchivo(archivo.getIdArchivo());
		archivoDTO.setNombreArchivo(archivo.getNombreArchivo());
		archivoDTO.setIdAlfresco(archivo.getIdAlfresco());
		return archivoDTO;
	}

	private String getOrCreateSubfolder(String folderName) throws IOException {
		// URL para listar los contenidos en la carpeta base
		String listFoldersUrl = appCredential.getAlfrescoHost()
				+ "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + appCredential.getAlfrescoSpaceStore()
				+ "/children";

		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(listFoldersUrl);

		get.addHeader("Authorization", "Basic " + Base64.getEncoder()
				.encodeToString((appCredential.getAlfrescoUsername() + ":" + appCredential.getAlfrescoPassword()).getBytes()));

		HttpResponse response = client.execute(get);
		String responseString = EntityUtils.toString(response.getEntity());
		client.close();

		System.out.println(responseString); // Imprime la respuesta JSON para depuración
		logger.info("Se imprime la respuesta JSON  -  responseString: " +responseString);

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode responseJson = objectMapper.readTree(responseString);

		// Navegar a la lista de entradas
		JsonNode entriesNode = responseJson.path("list").path("entries");
		if (entriesNode.isMissingNode()) {
			
			logger.info("The expected 'entries' node is missing in the response.");
			throw new RuntimeException("The expected 'entries' node is missing in the response.");
		}

		// Comprobar si la carpeta ya existe
		for (JsonNode entryNode : entriesNode) {
			JsonNode entry = entryNode.path("entry");
			String name = entry.path("name").asText();
			if (name.equals(folderName)) {
				return entry.path("id").asText(); // Retorna el ID de la carpeta existente
			}
		}

		// Si no existe, crear la carpeta
		String createFolderUrl = appCredential.getAlfrescoHost()
				+ "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + appCredential.getAlfrescoSpaceStore()
				+ "/children";

		CloseableHttpClient clientCreate = HttpClients.createDefault();
		HttpPost postCreate = new HttpPost(createFolderUrl);

		postCreate.addHeader("Authorization", "Basic " + Base64.getEncoder()
				.encodeToString((appCredential.getAlfrescoUsername() + ":" + appCredential.getAlfrescoPassword()).getBytes()));

		String jsonPayload = "{ \"name\": \"" + folderName + "\", \"nodeType\": \"cm:folder\" }";
		postCreate.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

		HttpResponse responseCreate = clientCreate.execute(postCreate);
		String responseStringCreate = EntityUtils.toString(responseCreate.getEntity());
		clientCreate.close();

		if (responseCreate.getStatusLine().getStatusCode() == 201) {
			JsonNode responseJsonCreate = objectMapper.readTree(responseStringCreate);
			return responseJsonCreate.path("entry").path("id").asText();
		} else {
			logger.info("Failed to create folder, status code: " + responseCreate.getStatusLine().getStatusCode());
			
			throw new RuntimeException(
					"Failed to create folder, status code: " + responseCreate.getStatusLine().getStatusCode());
		}
	}

	public ResponseEntity<Map<String, String>> downloadFile(String nodeId) {
		String url = appCredential.getAlfrescoHost() + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/"
				+ nodeId + "/content";

		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(appCredential.getAlfrescoUsername(), appCredential.getAlfrescoPassword());

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

			// Codificar el contenido del archivo en Base64.
			String encodedContent = Base64.getEncoder().encodeToString(response.getBody());

			// crea el response body con el filename y content
			Map<String, String> responseBody = Map.of("filename", fileName, "content", encodedContent);

			// Set headers
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.APPLICATION_JSON);

			// retorna el response entity
			return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			// Captura errores 4xx como 404 (No encontrado) o 401 (No autorizado)
			logger.info("Error en la solicitud: " + e.getMessage());
			System.err.println("Error en la solicitud: " + e.getMessage());
			return new ResponseEntity<>(Map.of("error", "Error en la solicitud: " + e.getStatusCode()),
					e.getStatusCode());
		} catch (HttpServerErrorException e) {
			// Captura errores 5xx relacionados con el servidor
			logger.info("Error en el servidor: " + e.getMessage());
			System.err.println("Error en el servidor: " + e.getMessage());
			return new ResponseEntity<>(Map.of("error", "Error en el servidor: " + e.getStatusCode()),
					e.getStatusCode());
		} catch (ResourceAccessException e) {
			// Captura errores de conexión (como si el servicio está caído)
			logger.info("No se pudo acceder al servicio de Alfresco: " + e.getMessage());
			System.err.println("No se pudo acceder al servicio de Alfresco: " + e.getMessage());
			return new ResponseEntity<>(
					Map.of("error", "No se pudo acceder al servicio de Alfresco. Verifique la conexión."),
					HttpStatus.SERVICE_UNAVAILABLE);
		} catch (Exception e) {
			// Manejo general para cualquier otro tipo de excepción
			logger.info("Error inesperado: " + e.getMessage());
			System.err.println("Error inesperado: " + e.getMessage());
			return new ResponseEntity<>(
					Map.of("error", "Ocurrió un error inesperado. Consulte los registros para más detalles."),
					HttpStatus.INTERNAL_SERVER_ERROR);
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

	// borrar archivo
	public ResponseEntity<Void> deleteFile(String nodeId) {
		String url = appCredential.getAlfrescoHost() + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + nodeId;

		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(appCredential.getAlfrescoUsername(), appCredential.getAlfrescoPassword());

		try {
			restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			logger.info("Error inesperado al borrar archivo : " + e.getMessage());
			// Log the error or handle it as needed
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// para generar el sufijo aleatorio para el nombre de los archivos.
	public String getFilenameWithUUID(String originalFilename) {// getFilenameWithTimestamp

		// Obtener la extensión del archivo y obtener nombre del archivo sin la
		// extensión
		String filenameWithoutExtension;
		String extension = "";
		int dotIndex = originalFilename.lastIndexOf('.');

		if (dotIndex != -1 && dotIndex < originalFilename.length() - 1) {
			extension = originalFilename.substring(dotIndex);
			filenameWithoutExtension = originalFilename.substring(0, dotIndex);
		} else {
			filenameWithoutExtension = originalFilename; // No hay extensión
		}

		// Construir el nuevo nombre del archivo
		logger.info("uuid generado para el archivo : " + UUID.randomUUID());
		return filenameWithoutExtension + "_" + UUID.randomUUID() + extension;
	}

}
