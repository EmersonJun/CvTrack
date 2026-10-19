package br.com.cvmatch.curriculo;

import br.com.cvmatch.support.IntegrationTest;
import br.com.cvmatch.support.PdfTestUtils;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CurriculoControllerIntegrationTest extends IntegrationTest {

    private ResultActions enviar(String token, String titulo, byte[] conteudo) throws Exception {
        return mvc.perform(multipart("/api/curriculos")
                .file(new MockMultipartFile("arquivo", "cv.pdf", "application/pdf", conteudo))
                .param("titulo", titulo)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    private String idDaResposta(ResultActions resultado) throws Exception {
        return JsonPath.read(resultado.andReturn().getResponse().getContentAsString(), "$.id");
    }

    @Test
    void enviarDeveRetornar201ComVersao1ELocation() throws Exception {
        String token = autenticarNovoUsuario();

        enviar(token, "CV Backend", PdfTestUtils.curriculo("1"))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString("/api/curriculos/")))
                .andExpect(jsonPath("$.titulo").value("CV Backend"))
                .andExpect(jsonPath("$.versao").value(1))
                .andExpect(jsonPath("$.totalCaracteres").isNumber())
                .andExpect(jsonPath("$.textoExtraido").doesNotExist());
    }

    @Test
    void novoEnvioComMesmoTituloDeveCriarVersao2() throws Exception {
        String token = autenticarNovoUsuario();
        enviar(token, "CV Backend", PdfTestUtils.curriculo("1")).andExpect(status().isCreated());

        enviar(token, "CV Backend", PdfTestUtils.curriculo("2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.versao").value(2));

        mvc.perform(get("/api/curriculos/versoes").param("titulo", "CV Backend")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].versao").value(1))
                .andExpect(jsonPath("$[1].versao").value(2));
    }

    @Test
    void enviarOMesmoCurriculoDuasVezesDeveRetornar409() throws Exception {
        String token = autenticarNovoUsuario();
        byte[] pdf = PdfTestUtils.curriculo("1");
        enviar(token, "CV Backend", pdf).andExpect(status().isCreated());

        enviar(token, "Outro título", pdf)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Este currículo já foi enviado anteriormente"));
    }

    @Test
    void enviarArquivoQueNaoEPdfDeveRetornar422() throws Exception {
        String token = autenticarNovoUsuario();

        enviar(token, "CV", "texto qualquer".getBytes(StandardCharsets.UTF_8))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Regra de negócio violada"));
    }

    @Test
    void enviarComTituloEmBrancoDeveRetornar400() throws Exception {
        String token = autenticarNovoUsuario();

        enviar(token, "   ", PdfTestUtils.curriculo("1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Dados inválidos"))
                .andExpect(jsonPath("$.campos[0].campo").value("titulo"));
    }

    @Test
    void listarNaoDeveTrazerOTextoMasODetalheSim() throws Exception {
        String token = autenticarNovoUsuario();
        String id = idDaResposta(enviar(token, "CV", PdfTestUtils.curriculo("1")));

        mvc.perform(get("/api/curriculos").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].textoExtraido").doesNotExist());

        mvc.perform(get("/api/curriculos/{id}", id).header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.textoExtraido", containsString("Emerson Junior")));
    }

    @Test
    void naoDeveAcessarCurriculoDeOutroUsuario() throws Exception {
        String id = idDaResposta(enviar(autenticarNovoUsuario(), "CV", PdfTestUtils.curriculo("1")));
        String tokenDeOutro = autenticarNovoUsuario();

        mvc.perform(get("/api/curriculos/{id}", id).header(HttpHeaders.AUTHORIZATION, tokenDeOutro))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/curriculos/{id}", id).header(HttpHeaders.AUTHORIZATION, tokenDeOutro))
                .andExpect(status().isNotFound());
    }

    @Test
    void excluirDeveRetornar204EDepois404() throws Exception {
        String token = autenticarNovoUsuario();
        String id = idDaResposta(enviar(token, "CV", PdfTestUtils.curriculo("1")));

        mvc.perform(delete("/api/curriculos/{id}", id).header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/curriculos/{id}", id).header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound());
    }

    @Test
    void semTokenDeveRetornar401() throws Exception {
        mvc.perform(multipart("/api/curriculos")
                        .file(new MockMultipartFile("arquivo", "cv.pdf", "application/pdf", PdfTestUtils.curriculo("1")))
                        .param("titulo", "CV"))
                .andExpect(status().isUnauthorized());
    }
}
