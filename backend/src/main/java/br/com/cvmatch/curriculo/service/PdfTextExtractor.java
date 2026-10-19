package br.com.cvmatch.curriculo.service;

import br.com.cvmatch.common.exception.RegraNegocioException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Extrai o texto de um PDF com o Apache PDFBox.
 *
 * Fica isolado numa classe própria (e não dentro do service) para:
 * - ser testado sozinho, com PDFs gerados no próprio teste;
 * - ser mockado no teste do service;
 * - poder ser trocado no futuro (ex.: OCR para PDFs escaneados) sem mexer no resto.
 */
@Component
public class PdfTextExtractor {

    /** Todo PDF começa com esses bytes ("magic number"). */
    private static final byte[] ASSINATURA_PDF = "%PDF-".getBytes(StandardCharsets.US_ASCII);

    static final int MINIMO_CARACTERES = 50;
    static final int MAXIMO_PAGINAS = 10;

    public String extrair(byte[] conteudo) {
        // Não confiamos na extensão nem no Content-Type: ambos são definidos pelo
        // cliente e podem ser falsificados. Os primeiros bytes do arquivo não mentem.
        if (!temAssinaturaPdf(conteudo)) {
            throw new RegraNegocioException("O arquivo enviado não é um PDF válido");
        }

        try (PDDocument documento = Loader.loadPDF(conteudo)) {
            if (documento.getNumberOfPages() > MAXIMO_PAGINAS) {
                throw new RegraNegocioException(
                        "O currículo deve ter no máximo " + MAXIMO_PAGINAS + " páginas");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true); // respeita a ordem visual (útil em layouts de colunas)
            String texto = normalizar(stripper.getText(documento));

            if (texto.length() < MINIMO_CARACTERES) {
                throw new RegraNegocioException(
                        "Não foi possível extrair texto do PDF. Ele pode ser uma imagem escaneada; "
                                + "exporte o currículo como PDF a partir do editor de texto.");
            }
            return texto;
        } catch (InvalidPasswordException e) {
            throw new RegraNegocioException("PDF protegido por senha não é suportado");
        } catch (IOException e) {
            throw new RegraNegocioException("Não foi possível ler o PDF: arquivo corrompido ou inválido");
        }
    }

    private boolean temAssinaturaPdf(byte[] conteudo) {
        return conteudo != null
                && conteudo.length >= ASSINATURA_PDF.length
                && Arrays.equals(conteudo, 0, ASSINATURA_PDF.length, ASSINATURA_PDF, 0, ASSINATURA_PDF.length);
    }

    /**
     * Padroniza o texto para que o mesmo currículo gere sempre o mesmo hash
     * e para economizar tokens quando o texto for enviado à IA.
     */
    static String normalizar(String texto) {
        return texto
                .replace("\u0000", "")             // o PostgreSQL não aceita o caractere nulo em TEXT
                .replace("\r\n", "\n")             // quebra de linha do Windows -> padrão Unix
                .replace('\r', '\n')
                .replaceAll("[ \\t\\x0B\\f\\u00A0]+", " ") // vários espaços/tabs -> um espaço
                .replaceAll(" *\\n *", "\n")       // remove espaços nas bordas de cada linha
                .replaceAll("\\n{3,}", "\n\n")     // no máximo uma linha em branco seguida
                .strip();
    }
}
