package br.com.cvmatch.vaga.service;

import br.com.cvmatch.common.exception.RecursoNaoEncontradoException;
import br.com.cvmatch.usuario.UsuarioRepository;
import br.com.cvmatch.vaga.dto.VagaRequest;
import br.com.cvmatch.vaga.dto.VagaResponse;
import br.com.cvmatch.vaga.entity.Vaga;
import br.com.cvmatch.vaga.mapper.VagaMapper;
import br.com.cvmatch.vaga.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VagaService {

    private final VagaRepository vagaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VagaMapper mapper;

    @Transactional
    public VagaResponse criar(UUID usuarioId, VagaRequest requisicao) {
        Vaga vaga = new Vaga(
                usuarioRepository.getReferenceById(usuarioId),
                requisicao.titulo().trim(),
                empresaOuNulo(requisicao.empresa()),
                requisicao.descricao().strip());

        return mapper.paraResponse(vagaRepository.saveAndFlush(vaga));
    }

    @Transactional(readOnly = true)
    public List<VagaResponse> listar(UUID usuarioId) {
        return mapper.paraResponses(vagaRepository.findAllByUsuarioIdOrderByCriadoEmDesc(usuarioId));
    }

    @Transactional(readOnly = true)
    public VagaResponse buscar(UUID id, UUID usuarioId) {
        return mapper.paraResponse(buscarDoUsuario(id, usuarioId));
    }

    @Transactional
    public VagaResponse atualizar(UUID id, UUID usuarioId, VagaRequest requisicao) {
        Vaga vaga = buscarDoUsuario(id, usuarioId);
        vaga.atualizar(
                requisicao.titulo().trim(),
                empresaOuNulo(requisicao.empresa()),
                requisicao.descricao().strip());
        return mapper.paraResponse(vaga);
    }

    @Transactional
    public void excluir(UUID id, UUID usuarioId) {
        vagaRepository.delete(buscarDoUsuario(id, usuarioId));
    }

    private Vaga buscarDoUsuario(UUID id, UUID usuarioId) {
        return vagaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vaga não encontrada"));
    }

    private String empresaOuNulo(String empresa) {
        return StringUtils.hasText(empresa) ? empresa.trim() : null;
    }
}
