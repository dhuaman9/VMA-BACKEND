package pe.gob.sunass.vma.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pe.gob.sunass.vma.dto.ArchivoDTO;
import pe.gob.sunass.vma.dto.UsuarioDTO;
import pe.gob.sunass.vma.exception.ConflictException;
import pe.gob.sunass.vma.exception.FailledValidationException;
import pe.gob.sunass.vma.exception.ResourceNotFoundException;
import pe.gob.sunass.vma.service.AlfrescoService;

@RestController
@RequestMapping("/archivo")
public class ArchivosController {

	
	private static Logger logger = LoggerFactory.getLogger(ArchivosController.class);
	 
	@Autowired
	private AlfrescoService alfrescoService;
		
	  

	@PostMapping("/upload")
	public ResponseEntity<List<ArchivoDTO>> uploadFiles(@RequestParam(value = "pdf", required = false) MultipartFile pdf,
											@RequestParam(value = "pdfWord", required = false) MultipartFile pdfWord,
											@RequestParam(value = "excel", required = false) MultipartFile excel) {
		try {
			List<ArchivoDTO> archivosDTO = new ArrayList<>();
			if(Objects.nonNull(pdf)) {
				archivosDTO.add(alfrescoService.uploadFile(pdf));
			}

			if(Objects.nonNull(pdfWord)) {
				archivosDTO.add(alfrescoService.uploadFile(pdfWord));
			}
			if(Objects.nonNull(excel)) {
				archivosDTO.add(alfrescoService.uploadFile(excel));
			}

			return new ResponseEntity<>(archivosDTO, HttpStatus.OK);

		} catch (FailledValidationException ex) {  //BadRequestException ex

			throw ex;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	    
	@PutMapping("/{id}")
    public ResponseEntity<ArchivoDTO> updateFile(@PathVariable("id") Integer archivoId,
                                                  @RequestParam("file") MultipartFile file) {
        try {
            // Llamar al servicio para actualizar el archivo
            ArchivoDTO updatedArchivoDTO = alfrescoService.updateFile(archivoId, file);
            // Devolver una respuesta con el archivo actualizado
            
           //return ResponseEntity.ok(updatedArchivoDTO);
            return new ResponseEntity<ArchivoDTO>(updatedArchivoDTO,
                    HttpStatus.ACCEPTED);
        } catch (ConflictException e) {
            throw e; // Let the GlobalExceptionHandler handle it
        }catch (IOException e) {
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
	
//	@PutMapping("/{id}")
//    public ResponseEntity<ArchivoDTO> updateFile2(@PathVariable Integer id,
//            @RequestParam("file") MultipartFile file) {
//        try {
//            ArchivoDTO updatedArchivo = alfrescoService.updateFile(id, file);
//            return ResponseEntity.ok(updatedArchivo);
//        } catch (RuntimeException | IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//  
	 @GetMapping("/{nodeId}/download")
	  public ResponseEntity<byte[]> downloadFile(@PathVariable String nodeId) {
	        return alfrescoService.downloadFile(nodeId);
	  }
	 
	  
	  
	 @DeleteMapping("/{nodeId}")
	  public ResponseEntity<Void> deleteFile(@PathVariable String nodeId) {
	        return alfrescoService.deleteFile(nodeId);
	 }
	    
}
