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
import org.springframework.web.multipart.MultipartFile;


import pe.gob.sunass.vma.exception.FailledValidationException;
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

		} catch (FailledValidationException ex) {  //BadRequestException ex

			throw ex;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	    
//	@PutMapping("/{id}")  //dhr se usa? 
//    public ResponseEntity<ArchivoDTO> updateFile(@PathVariable("id") Integer archivoId,
//                                                  @RequestParam("file") MultipartFile file) {
//        try {
//            // Llamar al servicio para actualizar el archivo
//            ArchivoDTO updatedArchivoDTO = alfrescoService.updateFile(archivoId, file);
//            // Devolver una respuesta con el archivo actualizado
//            
//           //return ResponseEntity.ok(updatedArchivoDTO);
//            return new ResponseEntity<ArchivoDTO>(updatedArchivoDTO,
//                    HttpStatus.ACCEPTED);
//        } catch (ConflictException e) {
//            throw e; // Let the GlobalExceptionHandler handle it
//        }catch (IOException e) {
//            
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
	

//  
	 @GetMapping("/{nodeId}/download")
	  public ResponseEntity<Map<String, String>> downloadFile(@PathVariable String nodeId) {
		 	return alfrescoService.downloadFile(nodeId);
	  }
	 
	  
	  
	 @DeleteMapping("/{nodeId}")
	  public ResponseEntity<Void> deleteFile(@PathVariable String nodeId) {
	        return alfrescoService.deleteFile(nodeId);
	 }
	    
}
