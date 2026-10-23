package br.com.cvmatch.curriculo.service;

import br.com.cvmatch.common.exception.ConflitoException;
import br.com.cvmatch.common.exception.RecursoNaoEncontradoException;
import br.com.cvmatch.common.exception.RegraNegocioException;
import br.com.cvmatch.common.util.HashUtils;
import br.com.cvmatch.curriculo.dto.CurriculoDetalheResponse;
import br.com.cvmatch.curriculo.dto.CurriculoResponse;
import br.com.cvmatch.curriculo.entity.Curriculo;
import br.com.cvmatch.curriculo.mapper.CurriculoMapper;
import br.com.cvmatch.curriculo.repository.CurriculoRepository;
import br.com.cvmatch.usuario.Usuario;
import br.com.cvmatch.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurriculoService {

    private static final int TAMANHO_MAXIMO_NOME_ARQUIVO = 255;

    private final CurriculoRepository curriculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PdfTextExtractor pdfTextExtractor;
    private final CurriculoMapper mapper;

    @Transactional
    public CurriculoResponse enviar(UUID usuarioId, String titulo, MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraNegocioException("Envie um arquivo PDF");
        }

        String texto = pdfTextExtractor.extrair(lerBytes(arquivo));
        String hash = HashUtils.sha256(texto);

        if (curriculoRepository.existsByUsuarioIdAndHashConteudo(usuarioId, hash)) {
            throw new ConflitoException("Este currículo já foi enviado anteriormente");
        }

        String tituloNormalizado = titulo.trim();
        int proximaVersao = curriculoRepository.buscarUltimaVersao(usuarioId, tituloNormalizado) + 1;

        Usuario usuario = usuarioRepository.getReferenceById(usuarioId);

        Curriculo salvo = curriculoRepository.saveAndFlush(new Curriculo(
                usuario, tituloNormalizado, proximaVersao, nomeArquivo(arquivo), texto, hash));

        return mapper.paraResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<CurriculoResponse> listar(UUID usuarioId) {
        return mapper.paraResponses(curriculoRepository.findAllByUsuarioIdOrderByCriadoEmDesc(usuarioId));
    }

    @Transactional(readOnly = true)
    public List<CurriculoResponse> listarVersoes(UUID usuarioId, String titulo) {
        return mapper.paraResponses(
                curriculoRepository.findAllByUsuarioIdAndTituloOrderByVersaoAsc(usuarioId, titulo.trim()));
    }

    @Transactional(readOnly = true)
    public CurriculoDetalheResponse buscar(UUID id, UUID usuarioId) {
        return mapper.paraDetalhe(buscarDoUsuario(id, usuarioId));
    }

    @Transactional
    public void excluir(UUID id, UUID usuarioId) {
        curriculoRepository.delete(buscarDoUsuario(id, usuarioId));
    }

    private Curriculo buscarDoUsuario(UUID id, UUID usuarioId) {
        return curriculoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Currículo não encontrado"));
    }

    private byte[] lerBytes(MultipartFile arquivo) {
        try {
            return arquivo.getBytes();
        } catch (IOException e) {
            throw new RegraNegocioException("Não foi possível ler o arquivo enviado");
        }
    }

    private String nomeArquivo(MultipartFile arquivo) {
        String nome = StringUtils.getFilename(StringUtils.cleanPath(
                arquivo.getOriginalFilename() == null ? "" : arquivo.getOriginalFilename()));
        if (!StringUtils.hasText(nome)) {
            return "curriculo.pdf";
        }
        return nome.length() > TAMANHO_MAXIMO_NOME_ARQUIVO
                ? nome.substring(nome.length() - TAMANHO_MAXIMO_NOME_ARQUIVO)
                : nome;
    }
}
