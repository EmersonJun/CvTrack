package br.com.cvmatch.curriculo;

import br.com.cvmatch.common.exception.ConflitoException;
import br.com.cvmatch.common.exception.RecursoNaoEncontradoException;
import br.com.cvmatch.common.exception.RegraNegocioException;
import br.com.cvmatch.common.util.HashUtils;
import br.com.cvmatch.curriculo.entity.Curriculo;
import br.com.cvmatch.curriculo.mapper.CurriculoMapper;
import br.com.cvmatch.curriculo.repository.CurriculoRepository;
import br.com.cvmatch.curriculo.service.CurriculoService;
import br.com.cvmatch.curriculo.service.PdfTextExtractor;
import br.com.cvmatch.usuario.Usuario;
import br.com.cvmatch.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurriculoServiceTest {

    private static final String TEXTO = "Texto extraído do currículo com mais de cinquenta caracteres.";

    @Mock private CurriculoRepository curriculoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PdfTextExtractor pdfTextExtractor;

    private CurriculoService service;
    private final UUID usuarioId = UUID.randomUUID();
    private final MockMultipartFile arquivo =
            new MockMultipartFile("arquivo", "C:\\fakepath\\meu-cv.pdf", "application/pdf", new byte[]{1, 2, 3});

    @BeforeEach
    void setUp() {
        service = new CurriculoService(curriculoRepository, usuarioRepository, pdfTextExtractor,
                Mappers.getMapper(CurriculoMapper.class));
    }

    @Test
    void enviarDeveCriarAProximaVersaoDoTitulo() {
        when(pdfTextExtractor.extrair(any())).thenReturn(TEXTO);
        when(curriculoRepository.existsByUsuarioIdAndHashConteudo(usuarioId, HashUtils.sha256(TEXTO))).thenReturn(false);
        when(curriculoRepository.buscarUltimaVersao(usuarioId, "CV Backend")).thenReturn(2);
        when(usuarioRepository.getReferenceById(usuarioId)).thenReturn(new Usuario("Ana", "ana@email.com", "hash"));
        when(curriculoRepository.saveAndFlush(any(Curriculo.class))).thenAnswer(chamada -> chamada.getArgument(0));

        var resposta = service.enviar(usuarioId, "  CV Backend  ", arquivo);

        ArgumentCaptor<Curriculo> captor = ArgumentCaptor.forClass(Curriculo.class);
        verify(curriculoRepository).saveAndFlush(captor.capture());
        Curriculo salvo = captor.getValue();

        assertThat(salvo.getVersao()).isEqualTo(3);
        assertThat(salvo.getTitulo()).isEqualTo("CV Backend");
        assertThat(salvo.getNomeArquivo()).isEqualTo("meu-cv.pdf");
        assertThat(salvo.getHashConteudo()).hasSize(64);
        assertThat(resposta.versao()).isEqualTo(3);
        assertThat(resposta.totalCaracteres()).isEqualTo(TEXTO.length());
    }

    @Test
    void enviarCurriculoRepetidoDeveLancarConflito() {
        when(pdfTextExtractor.extrair(any())).thenReturn(TEXTO);
        when(curriculoRepository.existsByUsuarioIdAndHashConteudo(usuarioId, HashUtils.sha256(TEXTO))).thenReturn(true);

        assertThatThrownBy(() -> service.enviar(usuarioId, "CV", arquivo))
                .isInstanceOf(ConflitoException.class);

        verify(curriculoRepository, never()).saveAndFlush(any());
    }

    @Test
    void enviarArquivoVazioDeveFalharSemTentarExtrair() {
        var vazio = new MockMultipartFile("arquivo", "cv.pdf", "application/pdf", new byte[0]);

        assertThatThrownBy(() -> service.enviar(usuarioId, "CV", vazio))
                .isInstanceOf(RegraNegocioException.class);

        verifyNoInteractions(pdfTextExtractor, curriculoRepository);
    }

    @Test
    void buscarCurriculoDeOutroUsuarioDeveLancarNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(curriculoRepository.findByIdAndUsuarioId(id, usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(id, usuarioId))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
