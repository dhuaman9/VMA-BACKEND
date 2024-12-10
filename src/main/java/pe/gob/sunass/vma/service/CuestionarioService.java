package pe.gob.sunass.vma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.sunass.vma.dto.*;
import pe.gob.sunass.vma.model.cuestionario.Alternativa;
import pe.gob.sunass.vma.model.cuestionario.Cuestionario;
import pe.gob.sunass.vma.model.cuestionario.Metadato;
import pe.gob.sunass.vma.model.cuestionario.Pregunta;
import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;
import pe.gob.sunass.vma.model.cuestionario.RespuestaVMA;
import pe.gob.sunass.vma.model.cuestionario.Seccion;
import pe.gob.sunass.vma.model.cuestionario.TipoPregunta;
import pe.gob.sunass.vma.repository.CuestionarioRepository;
import pe.gob.sunass.vma.repository.RegistroVMARepository;
import pe.gob.sunass.vma.repository.RespuestaVMARepository;
import pe.gob.sunass.vma.repository.SeccionRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CuestionarioService {

	@Autowired
	private CuestionarioRepository cuestionarioRepository;

	@Autowired
	private SeccionRepository seccionRepository;
	@Autowired
	private RespuestaVMARepository respuestaVMARepository;
	@Autowired
	private RegistroVMARepository registroVMARepository;

	CuestionarioService(SeccionRepository seccionRepository) {
		this.seccionRepository = seccionRepository;
	}

	public Optional<Cuestionario> findById(Integer idCuestionario) {
		Optional<Cuestionario> cuestionario = cuestionarioRepository.findById(idCuestionario);

		cuestionario.ifPresent(cuestionarioEncontrado -> {
			cuestionarioEncontrado.setSecciones(seccionRepository.findAllByCuestionarioId(idCuestionario));
		});

		return cuestionario;
	}

	// obtiene el cuestionario segun el ultimo ID
	public Optional<Cuestionario> getLastCuestionario() {
		Optional<Cuestionario> cuestionario = cuestionarioRepository.getLastCuestionario();

		cuestionario.ifPresent(cuestionarioEncontrado -> {
			cuestionarioEncontrado
					.setSecciones(seccionRepository.findAllByCuestionarioId(cuestionario.get().getIdCuestionario()));
		});

		return cuestionario;
	}

	public CuestionarioDTO getCuestionarioConRespuestas(Integer idRegistroVma) {
		Optional<RegistroVMA> registroVMAOpt = registroVMARepository.findById(idRegistroVma);

		if (registroVMAOpt.isEmpty()) {
			return null;
		}

		Optional<Cuestionario> lastCuestionario = getLastCuestionario();

		if (lastCuestionario.isEmpty()) {
			return null;
		}

		List<RespuestaVMA> respuestas = respuestaVMARepository.findByRegistroVMAId(idRegistroVma);

		Cuestionario cuestionario = lastCuestionario.get();
		DatosUsuarioRegistradorDto datosUsuarioRegistradorDto = null;
		if (registroVMAOpt.get().getEstado().equals("COMPLETO")) {
			datosUsuarioRegistradorDto = new DatosUsuarioRegistradorDto(registroVMAOpt.get().getNombreCompleto(),
					registroVMAOpt.get().getEmail(), registroVMAOpt.get().getTelefono());
		}

		return mapToCuestionarioDTO(cuestionario, respuestas, idRegistroVma, datosUsuarioRegistradorDto);
	}

	private CuestionarioDTO mapToCuestionarioDTO(Cuestionario cuestionario, List<RespuestaVMA> respuestas,
			Integer idRegistroVma, DatosUsuarioRegistradorDto datosUsuarioRegistradorDto) {
		return new CuestionarioDTO(
				cuestionario.getIdCuestionario(), cuestionario.getNombre(), cuestionario.getSecciones().stream()
						.map(sec -> mapToSeccionDTO(sec, respuestas, idRegistroVma)).collect(Collectors.toList()),
				datosUsuarioRegistradorDto);
	}

	private SeccionDTO mapToSeccionDTO(Seccion seccion, List<RespuestaVMA> respuestas, Integer idRegistroVma) {
		return new SeccionDTO(seccion.getIdSeccion(), seccion.getNombre(), seccion.getOrden(),
				seccion.getPreguntas().stream()
						.map(pregunta -> mapToPreguntaDTO(pregunta,
								respuestasByPreguntaId(respuestas, pregunta.getIdPregunta()), false, idRegistroVma))
						.collect(Collectors.toList()));
	}

	private List<RespuestaVMA> respuestasByPreguntaId(List<RespuestaVMA> respuestas, Integer idPregunta) {
		return respuestas.stream().filter(respuesta -> respuesta.getIdPregunta().equals(idPregunta))
				.collect(Collectors.toList());
	}

	private PreguntaDTO mapToPreguntaDTO(Pregunta pregunta, List<RespuestaVMA> respuestas, boolean esDependiente,
			Integer idRegistroVma) {
		RespuestaDTO respuestaDTO = null;
		if (respuestas.size() == 1
				&& (pregunta.getAlternativas().isEmpty() || pregunta.getTipoPregunta().equals(TipoPregunta.RADIO))
				&& !esDependiente) {
			RespuestaVMA respuesta = respuestas.get(0);
			respuestaDTO = new RespuestaDTO(respuesta.getIdRespuestaVMA(), respuesta.getIdAlternativa(),
					respuesta.getIdPregunta(), respuesta.getRespuesta());
		} else if (esDependiente) {

			RespuestaVMA respuesta = respuestaVMARepository
					.findRespuestasByIdPreguntaAndRegistroVma(pregunta.getIdPregunta(), idRegistroVma);
			if (Objects.nonNull(respuesta)) {
				respuestaDTO = new RespuestaDTO(respuesta.getIdRespuestaVMA(), respuesta.getIdAlternativa(),
						respuesta.getIdPregunta(), respuesta.getRespuesta());
			}

		}

		return new PreguntaDTO(pregunta.getIdPregunta(), pregunta.getDescripcion(), pregunta.getOrden(),
				pregunta.getTipoPregunta(),
				pregunta.getAlternativas().stream()
						.map(alternativa -> mapToAlternativaDTO(alternativa,
								getRespuestaAlternativa(alternativa.getIdAlternativa(), respuestas)))
						.collect(Collectors.toList()),
				respuestaDTO,
				Objects.nonNull(pregunta.getPreguntaDependiente())
						? mapToPreguntaDTO(pregunta.getPreguntaDependiente(), respuestas, true, idRegistroVma)
						: null,
				Objects.nonNull(pregunta.getMetadato()) ? mapToMetadatoDTO(pregunta.getMetadato()) : null);
	}

	private RespuestaVMA getRespuestaAlternativa(Integer idAlternativa, List<RespuestaVMA> respuestas) {
		return respuestas.stream()
				.filter(res -> res.getIdAlternativa() != null && res.getIdAlternativa().equals(idAlternativa))
				.findFirst().orElse(null);
	}

	private AlternativaDTO mapToAlternativaDTO(Alternativa alternativa, RespuestaVMA respuesta) {
		return new AlternativaDTO(alternativa.getIdAlternativa(), alternativa.getNombreCampo(),
				alternativa.getRequerido(),
				respuesta != null
						? new RespuestaDTO(respuesta.getIdRespuestaVMA(), respuesta.getIdAlternativa(),
								respuesta.getIdPregunta(), respuesta.getRespuesta())
						: null);
	}

	private MetadatoDto mapToMetadatoDTO(Metadato metadato) {
		return new MetadatoDto(metadato.getTipoArchivosPermitidos(), metadato.getMaxSizeInMB(), metadato.isRequerido(),
				metadato.getId(), metadato.isDecimal());
	}
}