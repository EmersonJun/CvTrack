package br.com.cvmatch.vaga;

import br.com.cvmatch.support.IntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VagaControllerIntegrationTest extends IntegrationTest {

    private static final String DESCRICAO =
            "Buscamos desenvolvedor Java júnior com Spring Boot, PostgreSQL, Docker e testes automatizados.";

    private String json(String titulo, String empresa, String descricao) {
        String campoEmpresa = empresa == null ? "null" : "\"" + empresa + "\"";
        return """
                {"titulo": "%s", "empresa": %s, "descricao": "%s"}
                """.formatted(titulo, campoEmpresa, descricao);
    }

    private ResultActions criar(String token, String corpo) throws Exception {
        return mvc.perform(post("/api/vagas")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo));
    }

    private String criarEObterId(String token) throws Exception {
        String resposta = criar(token, json("Dev Java Júnior", "Empresa X", DESCRICAO))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.id");
    }

    @Test
    void criarDeveRetornar201ComLocation() throws Exception {
        String token = autenticarNovoUsuario();

        criar(token, json("Dev Java Júnior", "Empresa X", DESCRICAO))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString("/api/vagas/")))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.titulo").value("Dev Java Júnior"))
                .andExpect(jsonPath("$.empresa").value("Empresa X"))
                .andExpect(jsonPath("$.criadoEm").isNotEmpty());
    }

    @Test
    void criarSemEmpresaDeveSerPermitido() throws Exception {
        String token = autenticarNovoUsuario();

        criar(token, json("Dev Java", null, DESCRICAO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.empresa").value(nullValue()));
    }

    @Test
    void criarComDadosInvalidosDeveRetornar400ListandoOsCampos() throws Exception {
        String token = autenticarNovoUsuario();

        criar(token, json("", null, "curta demais"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[?(@.campo == 'titulo')]").exists())
                .andExpect(jsonPath("$.campos[?(@.campo == 'descricao')]").exists());
    }

    @Test
    void listarDeveTrazerSomenteAsVagasDoUsuario() throws Exception {
        String token = autenticarNovoUsuario();
        criarEObterId(token);
        criarEObterId(autenticarNovoUsuario());

        mvc.perform(get("/api/vagas").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void atualizarDeveRetornar200ComOsNovosDados() throws Exception {
        String token = autenticarNovoUsuario();
        String id = criarEObterId(token);

        mvc.perform(put("/api/vagas/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Dev Java Pleno", "Empresa Y", DESCRICAO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Dev Java Pleno"))
                .andExpect(jsonPath("$.empresa").value("Empresa Y"));

        mvc.perform(get("/api/vagas/{id}", id).header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(jsonPath("$.titulo").value("Dev Java Pleno"));
    }

    @Test
    void naoDeveLerAlterarOuExcluirVagaDeOutroUsuario() throws Exception {
        String id = criarEObterId(autenticarNovoUsuario());
        String tokenDeOutro = autenticarNovoUsuario();

        mvc.perform(get("/api/vagas/{id}", id).header(HttpHeaders.AUTHORIZATION, tokenDeOutro))
                .andExpect(status().isNotFound());
        mvc.perform(put("/api/vagas/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, tokenDeOutro)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Invasão", null, DESCRICAO)))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/vagas/{id}", id).header(HttpHeaders.AUTHORIZATION, tokenDeOutro))
                .andExpect(status().isNotFound());
    }

    @Test
    void excluirDeveRetornar204EDepois404() throws Exception {
        String token = autenticarNovoUsuario();
        String id = criarEObterId(token);

        mvc.perform(delete("/api/vagas/{id}", id).header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/vagas/{id}", id).header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound());
    }

    @Test
    void semTokenDeveRetornar401() throws Exception {
        mvc.perform(get("/api/vagas"))
                .andExpect(status().isUnauthorized());
    }
}
