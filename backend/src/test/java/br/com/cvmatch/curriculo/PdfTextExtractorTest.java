package br.com.cvmatch.curriculo;

import br.com.cvmatch.common.exception.RegraNegocioException;
import br.com.cvmatch.curriculo.service.PdfTextExtractor;
import br.com.cvmatch.support.PdfTestUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PdfTextExtractorTest {

    private final PdfTextExtractor extrator = new PdfTextExtractor();

    @Test
    void deveExtrairOTextoDoPdf() {
        byte[] pdf = PdfTestUtils.curriculo("1");

        String texto = extrator.extrair(pdf);

        assertThat(texto)
                .contains("Emerson Junior")
                .contains("Spring Boot")
                .doesNotContain("\r")   // quebras de linha normalizadas
                .doesNotContain("  "); // sem espaços duplicados
    }

    @Test
    void mesmoPdfDeveGerarSempreOMesmoTexto() {
        byte[] pdf = PdfTestUtils.curriculo("1");

        assertThat(extrator.extrair(pdf)).isEqualTo(extrator.extrair(pdf));
    }

    @Test
    void deveRejeitarArquivoQueNaoEPdf() {
        byte[] naoPdf = "Sou um arquivo .txt renomeado para .pdf".getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> extrator.extrair(naoPdf))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não é um PDF válido");
    }

    @Test
    void deveRejeitarPdfCorrompido() {
        byte[] corrompido = "%PDF-1.7 conteudo quebrado".getBytes(StandardCharsets.US_ASCII);

        // Tem a assinatura de PDF, mas o conteúdo é lixo: deve falhar de forma controlada
        assertThatThrownBy(() -> extrator.extrair(corrompido))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void deveRejeitarPdfSemTexto() {
        byte[] escaneadoOuVazio = PdfTestUtils.pdfComPaginasEmBranco(1);

        assertThatThrownBy(() -> extrator.extrair(escaneadoOuVazio))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("extrair texto");
    }

    @Test
    void deveRejeitarPdfComPaginasDemais() {
        byte[] longo = PdfTestUtils.pdfComPaginasEmBranco(11);

        assertThatThrownBy(() -> extrator.extrair(longo))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("no máximo 10 páginas");
    }

    @Test
    void deveRejeitarConteudoNulo() {
        assertThatThrownBy(() -> extrator.extrair(null))
                .isInstanceOf(RegraNegocioException.class);
    }
}
