package br.com.cvmatch.vaga;

import br.com.cvmatch.common.exception.RecursoNaoEncontradoException;
import br.com.cvmatch.usuario.Usuario;
import br.com.cvmatch.usuario.UsuarioRepository;
import br.com.cvmatch.vaga.dto.VagaRequest;
import br.com.cvmatch.vaga.entity.Vaga;
import br.com.cvmatch.vaga.mapper.VagaMapper;
import br.com.cvmatch.vaga.repository.VagaRepository;
import br.com.cvmatch.vaga.service.VagaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VagaServiceTest {

    private static final String DESCRICAO =
            "Vaga para desenvolvedor Java júnior com Spring Boot, PostgreSQL, Docker e testes automatizados.";

    @Mock private VagaRepository vagaRepository;
    @Mock private UsuarioRepository usuarioRepository;

    private VagaService service;
    private final UUID usuarioId = UUID.randomUUID();
    private final Usuario usuario = new Usuario("Ana", "ana@email.com", "hash");

    @BeforeEach
    void setUp() {
        service = new VagaService(vagaRepository, usuarioRepository, Mappers.getMapper(VagaMapper.class));
    }

    @Test
    void criarDeveNormalizarOsCamposETransformarEmpresaEmBrancoEmNulo() {
        when(usuarioRepository.getReferenceById(usuarioId)).thenReturn(usuario);
        when(vagaRepository.saveAndFlush(any(Vaga.class))).thenAnswer(chamada -> chamada.getArgument(0));

        var resposta = service.criar(usuarioId, new VagaRequest("  Dev Java  ", "   ", "  " + DESCRICAO + "  "));

        ArgumentCaptor<Vaga> captor = ArgumentCaptor.forClass(Vaga.class);
        verify(vagaRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getTitulo()).isEqualTo("Dev Java");
        assertThat(captor.getValue().getEmpresa()).isNull();
        assertThat(captor.getValue().getDescricao()).isEqualTo(DESCRICAO);
        assertThat(resposta.titulo()).isEqualTo("Dev Java");
    }

    @Test
    void atualizarDeveAlterarAVagaSemChamarSave() {
        UUID id = UUID.randomUUID();
        Vaga vaga = new Vaga(usuario, "Antigo", null, DESCRICAO);
        when(vagaRepository.findByIdAndUsuarioId(id, usuarioId)).thenReturn(Optional.of(vaga));

        var resposta = service.atualizar(id, usuarioId, new VagaRequest("Novo título", "Empresa X", DESCRICAO));

        assertThat(vaga.getTitulo()).isEqualTo("Novo título");
        assertThat(vaga.getEmpresa()).isEqualTo("Empresa X");
        assertThat(resposta.titulo()).isEqualTo("Novo título");
        verify(vagaRepository, never()).save(any());
    }

    @Test
    void buscarVagaDeOutroUsuarioDeveLancarNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(vagaRepository.findByIdAndUsuarioId(id, usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(id, usuarioId))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Vaga não encontrada");
    }
}
