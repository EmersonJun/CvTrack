package br.com.cvmatch.analise;

import br.com.cvmatch.analise.ia.AnalisadorIa;
import br.com.cvmatch.analise.ia.AnaliseIaResponse;
import br.com.cvmatch.analise.ia.ResultadoAnaliseIa;
import br.com.cvmatch.support.IntegrationTest;
import br.com.cvmatch.support.PdfTestUtils;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HistoricoEDashboardIntegrationTest extends IntegrationTest {

    @MockitoBean
    private AnalisadorIa analisadorIa;

    private String token;
    private String vagaId;
    private String analiseVersao1;
    private String analiseVersao2;

    private String lerId(ResultActions resultado) throws Exception {
        return JsonPath.read(resultado.andReturn().getResponse().getContentAsString(), "$.id");
    }

    private String enviarCurriculo(String sufixo) throws Exception {
        return lerId(mvc.perform(multipart("/api/curriculos")
                        .file(new MockMultipartFile("arquivo", "cv.pdf", "application/pdf", PdfTestUtils.curriculo(sufixo)))
                        .param("titulo", "CV Backend")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isCreated()));
    }

    private String cadastrarVaga(String titulo) throws Exception {
        return lerId(mvc.perform(post("/api/vagas")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo": "%s", "descricao": "Vaga para desenvolvedor Java com Spring Boot, Docker e Kubernetes."}
                                """.formatted(titulo)))
                .andExpect(status().isCreated()));
    }

    private String solicitarAnalise(String curriculoId, String idDaVaga) throws Exception {
        return lerId(mvc.perform(post("/api/analises")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"curriculoId": "%s", "vagaId": "%s"}
                                """.formatted(curriculoId, idDaVaga)))
                .andExpect(status().isCreated()));
    }

    private ResultadoAnaliseIa resultado(int nota, List<String> presentes, List<String> ausentes) {
        return new ResultadoAnaliseIa(
                new AnaliseIaResponse(nota, "Resumo", List.of("Ponto forte"), List.of("Sugestão"), presentes, ausentes),
                "ollama", "llama3.1");
    }

    @BeforeEach
    void prepararDuasVersoesAnalisadas() throws Exception {
        token = autenticarNovoUsuario();
        String versao1 = enviarCurriculo("1");
        String versao2 = enviarCurriculo("2");
        vagaId = cadastrarVaga("Dev Java Júnior");

        when(analisadorIa.analisar(anyString(), anyString())).thenReturn(
                resultado(50, List.of("Java"), List.of("Docker", "Kubernetes")),
                resultado(80, List.of("Java", "Docker"), List.of("Kubernetes")));

        analiseVersao1 = solicitarAnalise(versao1, vagaId);
        analiseVersao2 = solicitarAnalise(versao2, vagaId);
    }

    @Test
    void historicoDeveSerPaginadoComAMaisRecentePrimeiro() throws Exception {
        mvc.perform(get("/api/analises").param("tamanho", "1").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElementos").value(2))
                .andExpect(jsonPath("$.totalPaginas").value(2))
                .andExpect(jsonPath("$.conteudo.length()").value(1))
                .andExpect(jsonPath("$.conteudo[0].nota").value(80))
                .andExpect(jsonPath("$.conteudo[0].curriculo.versao").value(2));
    }

    @Test
    void historicoComTamanhoAcimaDoLimiteDeveRetornar400() throws Exception {
        mvc.perform(get("/api/analises").param("tamanho", "100").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("tamanho"));
    }

    @Test
    void comparacaoDeveMostrarAEvolucaoEntreAsVersoes() throws Exception {
        mvc.perform(get("/api/analises/comparacao")
                        .param("anteriorId", analiseVersao1)
                        .param("atualId", analiseVersao2)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.anterior.curriculo.versao").value(1))
                .andExpect(jsonPath("$.atual.curriculo.versao").value(2))
                .andExpect(jsonPath("$.variacaoNota").value(30))
                .andExpect(jsonPath("$.palavrasConquistadas[0]").value("Docker"))
                .andExpect(jsonPath("$.palavrasAindaAusentes[0]").value("Kubernetes"))
                .andExpect(jsonPath("$.palavrasPerdidas.length()").value(0));
    }

    @Test
    void comparacaoComVagasDiferentesDeveRetornar422() throws Exception {
        String outraVaga = cadastrarVaga("Dev Java Pleno");
        String curriculoId = enviarCurriculo("3");
        when(analisadorIa.analisar(anyString(), anyString()))
                .thenReturn(resultado(60, List.of("Java"), List.of()));
        String analiseOutraVaga = solicitarAnalise(curriculoId, outraVaga);

        mvc.perform(get("/api/analises/comparacao")
                        .param("anteriorId", analiseVersao1)
                        .param("atualId", analiseOutraVaga)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void dashboardDeveConsolidarAsAnalisesDoUsuario() throws Exception {
        mvc.perform(get("/api/dashboard").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCurriculos").value(2))
                .andExpect(jsonPath("$.totalVagas").value(1))
                .andExpect(jsonPath("$.totalAnalises").value(2))
                .andExpect(jsonPath("$.notaMedia").value(65.0))
                .andExpect(jsonPath("$.melhorNota").value(80))
                .andExpect(jsonPath("$.ultimaNota").value(80))
                .andExpect(jsonPath("$.evolucao.length()").value(2))
                .andExpect(jsonPath("$.evolucao[0].nota").value(50))
                .andExpect(jsonPath("$.evolucao[1].nota").value(80))
                .andExpect(jsonPath("$.palavrasMaisAusentes[0].termo").value("kubernetes"))
                .andExpect(jsonPath("$.palavrasMaisAusentes[0].ocorrencias").value(2));
    }

    @Test
    void dashboardDeUsuarioNovoDeveVirZerado() throws Exception {
        mvc.perform(get("/api/dashboard").header(HttpHeaders.AUTHORIZATION, autenticarNovoUsuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAnalises").value(0))
                .andExpect(jsonPath("$.notaMedia").value(nullValue()))
                .andExpect(jsonPath("$.evolucao.length()").value(0));
    }
}
