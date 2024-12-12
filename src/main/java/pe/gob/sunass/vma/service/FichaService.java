package pe.gob.sunass.vma.service;

import java.time.LocalDate;
import java.time.Year;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import pe.gob.sunass.vma.assembler.FichaAssembler;
import pe.gob.sunass.vma.dto.FichaDTO;
import pe.gob.sunass.vma.exception.FailledValidationException;
import pe.gob.sunass.vma.model.FichaRegistro;
import pe.gob.sunass.vma.repository.FichaRepository;
import pe.gob.sunass.vma.util.UserUtil;

@Service
public class FichaService {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(EmpresaService.class);

	@Autowired
	private FichaRepository fichaRepository;

	@Autowired
	private UserUtil userUtil;

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public List<FichaDTO> findAll() throws Exception {
		List<FichaRegistro> listFicha = this.fichaRepository.findAllByOrderByIdFichaRegistroDesc();
		List<FichaDTO> listDTO = FichaAssembler.buildDtoModelCollection(listFicha);

		return listDTO;
	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public FichaDTO findById(Integer id) throws Exception {

		FichaDTO dto = null;
		Optional<FichaRegistro> opt = this.fichaRepository.findById(id);

		if (opt.isPresent()) {
			FichaRegistro ficha = opt.get();
			dto = FichaAssembler.buildDtoModel(ficha);
		}

		return dto;
	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public FichaDTO registrar(FichaDTO dto) throws Exception {
		if (dto == null) {
			throw new FailledValidationException("Los datos son obligatorios");
		} else if (dto.getAnio() == null || dto.getAnio().isEmpty()) {
			throw new FailledValidationException("el anio es obligatorio");
		} else if (dto.getFechaInicio() == null) {
			throw new FailledValidationException("La Fecha de Inicio es obligatorio");
		} else if (dto.getFechaFin() == null) {
			throw new FailledValidationException("La Fecha Fin es obligatorio");
		}

		else if (dto.getAnio().equals(this.fichaRepository.findAnioFichaRegistro(dto.getAnio()))) {
			logger.info("año seleccionado ya esta registrado");
			throw new FailledValidationException("El año ya esta registrado, debe elegir otro  año.");
		} else if (dto.getFechaInicio().isAfter(dto.getFechaFin()) || dto.getFechaInicio().isEqual(dto.getFechaFin())) {
			logger.info(" La fecha de inicio es mayor o igual que la fecha fin.");
			throw new FailledValidationException("La Fecha de Inicio no debe ser mayor o igual a la Fecha Fin");
		}

		else if ((Integer.parseInt(dto.getAnio()) > dto.getFechaInicio().getYear()) 
					&& (Integer.parseInt(dto.getAnio()) > dto.getFechaFin().getYear()) ) { // pendiente de validar con el
																						// usuario DF
			throw new FailledValidationException("El año ingresado no puede ser mayor que el año de la fecha de inicio o fin");
		}

		else if (!validarFechas(dto.getFechaInicio(), dto.getFechaFin())) { // Validar que el rango de fechas no
																			// interfiera con ningún rango existente.

			logger.info("Error, el rango de fechas se solapa con un rango de fechas existente.");
			throw new FailledValidationException(" La feha de inicio o fin,  se solapa con otras fechas registradas.");

		}

		List<FichaRegistro> listaFichas = this.fichaRepository.findAllByOrderByIdFichaRegistroDesc();

		if (listaFichas != null && !listaFichas.isEmpty()) {

		} else {
			logger.info("No hay fechas registradas en la base de datos.");
		}

		FichaRegistro fichaRegistro = new FichaRegistro();
		fichaRegistro.setAnio(dto.getAnio());
		fichaRegistro.setFechaInicio(dto.getFechaInicio());
		fichaRegistro.setFechaFin(dto.getFechaFin());
		fichaRegistro.setCreatedAt(new Date());
		fichaRegistro.setUpdatedAt(null);
		fichaRegistro.setIdUsuarioRegistro(userUtil.getCurrentUserId());
		fichaRegistro.setIdUsuarioActualizacion(null);

		fichaRegistro = this.fichaRepository.save(fichaRegistro);

		return FichaAssembler.buildDtoModel(fichaRegistro);
	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public FichaDTO update(FichaDTO dto) throws Exception {
		if (dto == null) {
			throw new Exception("Todos los datos son obligatorios");
		}

		FichaRegistro fichaRegistro = null;
		Optional<FichaRegistro> optFichaRegistro = this.fichaRepository.findById(dto.getIdFichaRegistro());

		if (optFichaRegistro.isPresent()) {
			fichaRegistro = optFichaRegistro.get();

			if (dto.getAnio() != null && !dto.getAnio().isEmpty()) {

				fichaRegistro.setAnio(dto.getAnio());

			}

			if (dto.getFechaInicio() != null) {
				if (!dto.getFechaInicio().equals(fichaRegistro.getFechaInicio())) {
					List<FichaRegistro> listaFichas = this.fichaRepository.validarFechaInicioFin(dto.getFechaInicio(),
							null);

					if (listaFichas != null && listaFichas.size() > 0) {
						throw new FailledValidationException("La fecha de inicio  ya existe!.");
					}

					fichaRegistro.setFechaInicio(dto.getFechaInicio());
				}
			}

			if (dto.getFechaFin() != null) {
				if (!dto.getFechaFin().equals(fichaRegistro.getFechaFin())) {
					List<FichaRegistro> listaFichas = this.fichaRepository.validarFechaInicioFin(dto.getFechaFin(),
							null);

					if (listaFichas != null && listaFichas.size() > 0) {
						throw new FailledValidationException("La fecha fin  ya existe!.");
					}

					fichaRegistro.setFechaFin(dto.getFechaFin());
				}
			}

			if (this.fichaRepository.nroRegistroPorAnioUpdate(dto.getAnio(), dto.getFechaInicio(),
					dto.getIdFichaRegistro()) > 0) {
				throw new FailledValidationException("Ya existe otro periodo registrado en este año."); //
			}

			List<FichaRegistro> listPeriodoVMA = this.fichaRepository.findAllByOrderByIdFichaRegistroDesc(); // lista de  Periodos  ordenados en desc, segun el ID .
			if (listPeriodoVMA != null && !listPeriodoVMA.isEmpty()) {

				logger.info("fecha inicio " + dto.getFechaInicio());
				logger.info("fecha fin " + dto.getFechaFin());
				if (dto.getFechaInicio().isAfter(dto.getFechaFin())
						|| dto.getFechaInicio().isEqual(dto.getFechaFin())) {
					logger.info("error: La fecha de inicio es mayor o igual que la fecha fin.");
					throw new FailledValidationException("Error: La Fecha de Inicio es mayor o igual que la Fecha Fin");
				}

				if (Integer.parseInt(dto.getAnio()) > (dto.getFechaInicio().getYear())) {
					throw new FailledValidationException(
							"Error: El año  no puede ser mayor que el año de la fecha de inicio.");
				}

				if (validarFechasForUpdate(dto.getIdFichaRegistro(), dto.getFechaInicio(), dto.getFechaFin())) { // Validar  que  el rango  de  fechas  no interfiera  con  ningún rango  existente.

					logger.info("El rango de fechas es correcto, no se cruza con ningun rango.");

				} else {
					logger.info("Error, el rango de fechas se cruza con un rango de fechas existente.");
					throw new FailledValidationException("Error: El rango de fechas se cruza con un rango registrado.");
				}

			} else {
				logger.info("No hay fichas en la base de datos.");
			}

			fichaRegistro.setUpdatedAt(new Date());
			fichaRegistro.setIdUsuarioActualizacion(userUtil.getCurrentUserId());
			fichaRegistro = this.fichaRepository.save(fichaRegistro);
		}

		return FichaAssembler.buildDtoModel(fichaRegistro);

	}

	// para exponer al front
	public boolean validarFechaEnRango(LocalDate fechaactual) {

		boolean enRango = false;
		List<FichaRegistro> listaFichas = this.fichaRepository.findAllByOrderByIdFichaRegistroDesc();

		if (listaFichas != null && !listaFichas.isEmpty()) {

			for (FichaRegistro itemFicha : listaFichas) {

				if (((fechaactual.isAfter(itemFicha.getFechaInicio())
						|| fechaactual.isEqual(itemFicha.getFechaInicio()))
						&& (fechaactual.isBefore(itemFicha.getFechaFin()))
						|| fechaactual.isEqual(itemFicha.getFechaFin()))) {
					enRango = true;
					break;
				}
			}

		} else {
			logger.info("No hay periodos registrados en la base de datos.");
			enRango = false;
		}
		return enRango;

	}

	public boolean validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
		long count = fichaRepository.validarFechas(fechaInicio, fechaFin);
		return count == 0;
	}

	public boolean validarFechasForUpdate(Integer idFichaRegistro, LocalDate fechaInicio, LocalDate fechaFin) {
		long count = fichaRepository.validarFechasForUpdate(idFichaRegistro, fechaInicio, fechaFin);
		return count == 0;
	}

	public FichaDTO fichaEnPeriodo(LocalDate fechaActual) throws Exception {

		FichaDTO dto = null;
		Optional<FichaRegistro> opt = this.fichaRepository.validarRangoConFecha(fechaActual);
		logger.info("this.fichaRepository.validarRangoConFecha(fechaActual)-"
				+ this.fichaRepository.validarRangoConFecha(fechaActual));
		if (opt.isPresent()) {
			FichaRegistro ficha = opt.get();
			dto = FichaAssembler.buildDtoModel(ficha);
		}

		return dto;
	}

	public Integer contarDiasFaltantesEnPeriodoActual() {
		List<Integer> diasRestantes = fichaRepository.findDiasRestantes();
		if (diasRestantes.isEmpty()) {
			return -1; // No hay registros coincidentes
		}
		return diasRestantes.get(0); // Devuelve el primer resultado
	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public FichaDTO obtenerPeriodoActivo() {

		FichaDTO dto = null;
		Optional<FichaRegistro> opt = this.fichaRepository.findOptionalPeriodosActivos();

		if (opt.isPresent()) {
			FichaRegistro ficha = opt.get();
			dto = FichaAssembler.buildDtoModel(ficha);
			return dto;
		} else {
			return null;
		}

	}

	/**
	 * expresiones cron :
	 *  1.- 0 0 0 26 12 * A las 12:00 del 26 de diciembre de cada año , para produccion
	 *  2.- 0 * * * * * cada minuto  , para pruebas en dev o local
	 *  3.- 0 * /2 * * * * cada 2 minutos   , para pruebas en dev o local
	 */
	@Scheduled(cron = " 0 0 0 26 12 * ")
	public void registrarFichaRegistroProximoAnio() { // para registrar el proximo trimestre del proximo año : del 01 de enero al 31 de marzo.
		int proximoAnio = Year.now().getValue() + 1;

		LocalDate fechaInicio = LocalDate.of(proximoAnio, 1, 1);
		LocalDate fechaFin = LocalDate.of(proximoAnio, 3, 31);

		// Crea una instancia de FichaRegistro y asigna valores
		FichaRegistro fichaRegistro = new FichaRegistro();
		fichaRegistro.setAnio(String.valueOf(Year.now().getValue()));
		fichaRegistro.setFechaInicio(fechaInicio);
		fichaRegistro.setFechaFin(fechaFin);
		fichaRegistro.setCreatedAt(new Date()); // dato de auditoria

		// Guarda el registro en la base de datos
		fichaRepository.save(fichaRegistro);

		System.out.println("Trimestre registrado: " + fichaRegistro);

	}

}
