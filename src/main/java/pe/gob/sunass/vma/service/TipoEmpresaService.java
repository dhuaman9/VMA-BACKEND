package pe.gob.sunass.vma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.sunass.vma.model.TipoEmpresa;
import pe.gob.sunass.vma.repository.TipoEmpresaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TipoEmpresaService {

    @Autowired
    private TipoEmpresaRepository tipoEmpresaRepository;

    @Transactional(readOnly = true)
    public List<TipoEmpresa> listTipoEmpresas() {
        return tipoEmpresaRepository.findAll();
    }

    public Optional<TipoEmpresa> getTipoEmpresaById(Integer id) {
        return tipoEmpresaRepository.findById(id);
    }
}