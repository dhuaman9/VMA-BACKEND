package pe.gob.sunass.vma.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pe.gob.sunass.vma.dto.EmpresaDTO;
import pe.gob.sunass.vma.exception.FailledValidationException;
import pe.gob.sunass.vma.model.Empresa;
import pe.gob.sunass.vma.model.Role;
import pe.gob.sunass.vma.model.TipoEmpresa;
import pe.gob.sunass.vma.repository.EmpresaRepository;
import pe.gob.sunass.vma.util.UserUtil;
import pe.gob.sunass.vma.assembler.EmpresaAssembler;

@Service
public class EmpresaService {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(EmpresaService.class);

	@Autowired
	private EmpresaRepository empresaRepository;

	@Autowired
	private UserUtil userUtil;

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public List<EmpresaDTO> findAll() throws Exception {
		List<Empresa> listEmpresa = this.empresaRepository.findByExcludingSunassOrderByIdEmpresa();
		List<EmpresaDTO> listDTO = EmpresaAssembler.buildDtoModelCollection(listEmpresa);

		return listDTO;
	}
	
	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public List<TipoEmpresa> findAllTipoEmpresas() throws Exception {
		List<TipoEmpresa> listEmpresa = this.empresaRepository.findTipoEmpresaById();
		
		return listEmpresa;
	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public Page<EmpresaDTO> findByFilter(String filter, Pageable pageable) throws Exception {
		Page<Empresa> pageDomain = this.empresaRepository.findByFilter(filter, pageable);
		Page<EmpresaDTO> pageDTO = EmpresaAssembler.buildDtoModelCollection(pageDomain);
		return pageDTO;
	}

	// paginacion
	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public Page<EmpresaDTO> findAll(Pageable pageable) throws Exception {
		Page<Empresa> pageDomain = this.empresaRepository.findAllEmpresa(pageable); 
		Page<EmpresaDTO> pageDTO = EmpresaAssembler.buildDtoModelCollection(pageDomain);

		return pageDTO;
	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public EmpresaDTO findById(Integer id, boolean dependency) throws Exception {

		EmpresaDTO dto = null;
		Optional<Empresa> opt = this.empresaRepository.findById(id);

		if (opt.isPresent()) {
			Empresa emp = opt.get();
			dto = EmpresaAssembler.buildDtoModel(emp);
		}

		return dto;
	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public EmpresaDTO registrar(EmpresaDTO dto) throws Exception {

		List<Empresa> list = this.empresaRepository.findByEps(dto.getNombre().toUpperCase());

		if (list != null && list.size() > 0) {
			throw new FailledValidationException("La empresa " + dto.getNombre() + " ya existe, registre uno nuevo.");

		}
		
		Optional<TipoEmpresa> optTipoEmpresa = this.empresaRepository.findByTipoEmpresaId(dto.getTipoEmpresa().getIdTipoEmpresa());
		
		Empresa empresa = new Empresa();
		empresa.setNombre(dto.getNombre().toUpperCase());
		empresa.setRegimen(dto.getRegimen());
//		empresa.setTipo(dto.getTipo());
		empresa.setTipoEmpresa(optTipoEmpresa.get()); //reemplaza al enterior.
		
		
		empresa.setCreatedAt(new Date());
		empresa.setUpdatedAt(null);
		empresa.setIdUsuarioRegistro(userUtil.getCurrentUserId());
		empresa.setIdUsuarioActualizacion(null);
		empresa = this.empresaRepository.save(empresa);
		return EmpresaAssembler.buildDtoModel(empresa);
		
	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public EmpresaDTO update(EmpresaDTO dto) throws Exception {
		if (dto == null) {
			throw new Exception("datos son obligatorios");
		}

		Empresa empresa = null;
		Optional<Empresa> optEmpresa = this.empresaRepository.findById(dto.getIdEmpresa());

		if (optEmpresa.isPresent()) {
			empresa = optEmpresa.get();

			if (dto.getNombre() != null && !dto.getNombre().isEmpty()) {
				if (!dto.getNombre().equals(empresa.getNombre())) {
					List<Empresa> list = this.empresaRepository.findByEps(dto.getNombre().toUpperCase());

					if (list != null && list.size() > 0) {
						throw new FailledValidationException(
								"La empresa " + dto.getNombre() + " ya existe, registre uno nuevo.");
					}
					empresa.setNombre(dto.getNombre().toUpperCase());
				}
			}

			if (dto.getRegimen() != null && !dto.getRegimen().isEmpty()) {
				if (!dto.getRegimen().equals(empresa.getRegimen())) {
					empresa.setRegimen(dto.getRegimen());
				}
			}

			if (dto.getTipoEmpresa() != null && dto.getTipoEmpresa().getIdTipoEmpresa()!= null) { 
//				if (!dto.getTipo().equals(empresa.getTipo())) {
//					empresa.setTipo(dto.getTipo());
//				}
				
				Optional<TipoEmpresa> optTipoEmpresa = this.empresaRepository.findByTipoEmpresaId(dto.getTipoEmpresa().getIdTipoEmpresa());

				if (!optTipoEmpresa.isPresent()) {
					throw new Exception(" el  tipo de empresa no existe");
				}
				
				empresa.setTipoEmpresa(optTipoEmpresa.get());
			}

//			if (dto.getTipo() != null && !dto.getTipo().isEmpty()) {
//				if (!dto.getTipo().equals(empresa.getTipo())) {
//					empresa.setTipo(dto.getTipo());
//				}
//			}
			
//			if (dto.getEstado() != null) {   // segun DF, no se usara por el momeno.
//				if (!dto.getEstado().equals(empresa.getEstado())) {
//					empresa.setEstado(dto.getEstado());
//				}
//			}

			empresa.setUpdatedAt(new Date());
			empresa.setIdUsuarioActualizacion(userUtil.getCurrentUserId());
			empresa = this.empresaRepository.save(empresa);
		}

		return EmpresaAssembler.buildDtoModel(empresa);
	}

}