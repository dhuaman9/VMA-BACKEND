package pe.gob.sunass.vma.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import pe.gob.sunass.vma.exception.FailledValidationException;
import pe.gob.sunass.vma.exception.ResourceNotFoundException;
import pe.gob.sunass.vma.service.AlfrescoService;
import pe.gob.sunass.vma.service.RegistroVMAService;

@RestController
@RequestMapping("/archivo")
public class ArchivosController {

	private static Logger logger = LoggerFactory.getLogger(ArchivosController.class);

	@Autowired
	private AlfrescoService alfrescoService;

	@Autowired
	private RegistroVMAService registroVMAService;

	@PostMapping("/upload")
	public ResponseEntity<?> uploadFiles(@RequestParam(value = "file") MultipartFile file,
			@RequestParam(value = "registroVMAId") Integer registroVMAId,
			@RequestParam(value = "preguntaId") Integer preguntaId,
			@RequestParam(value = "respuestaId", required = false) Integer respuestaId) {
		try {
			registroVMAService.saveRespuestaVMAArchivo(file, registroVMAId, preguntaId, respuestaId);
			return new ResponseEntity<>(HttpStatus.OK);

		} catch (FailledValidationException ex) { // BadRequestException ex

			throw ex;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	 @GetMapping("/{nodeId}/download")
//	  public ResponseEntity<Map<String, String>> downloadFile(@PathVariable String nodeId) {
//		 	return alfrescoService.downloadFile(nodeId);
//	  }
//	 
	@GetMapping("/{nodeId}/download")
	public ResponseEntity<Map<String, String>> downloadFile(@PathVariable String nodeId) {
		try {
			logger.info("Llamada al servicio de descarga");
			// Llamada al servicio de descarga
			return alfrescoService.downloadFile(nodeId);

		} catch (ResourceNotFoundException e) {
			// Si el archivo no se encuentra, devolver error 404
			logger.info(e.getMessage(), e);
			Map<String, String> errorResponse = Map.of("message", "File not found");
			return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			// Si hay un error en la solicitud HTTP (401, 403, 500, etc.), devolver error
			// con detalles
			logger.info("HttpClientErrorException | HttpServerErrorException  " + e.getMessage(), e);
			Map<String, String> errorResponse = Map.of("message", "Error downloading file: " + e.getMessage(), "status",
					String.valueOf(e.getStatusCode()));
			return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getStatusCode().value()));
		} catch (Exception e) {
			logger.info("otro error:" + e.getMessage(), e);
			// Capturar cualquier otra excepción genérica
			Map<String, String> errorResponse = Map.of("message", "Internal Server Error", "error", e.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			logger.info("ningun error:");
		}
	}

	@DeleteMapping("/{nodeId}")
	public ResponseEntity<Void> deleteFile(@PathVariable String nodeId) {
		return alfrescoService.deleteFile(nodeId);
	}

}
