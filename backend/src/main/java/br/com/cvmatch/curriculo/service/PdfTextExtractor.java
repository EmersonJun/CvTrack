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

@Component
public class PdfTextExtractor {

    private static final byte[] ASSINATURA_PDF = "%PDF-".getBytes(StandardCharsets.US_ASCII);

    static final int MINIMO_CARACTERES = 50;
    static final int MAXIMO_PAGINAS = 10;

    public String extrair(byte[] conteudo) {
        if (!temAssinaturaPdf(conteudo)) {
            throw new RegraNegocioException("O arquivo enviado não é um PDF válido");
        }

        try (PDDocument documento = Loader.loadPDF(conteudo)) {
            if (documento.getNumberOfPages() > MAXIMO_PAGINAS) {
                throw new RegraNegocioException(
                        "O currículo deve ter no máximo " + MAXIMO_PAGINAS + " páginas");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
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

    static String normalizar(String texto) {
        return texto
                .replace("\u0000", "")
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replaceAll("[ \\t\\x0B\\f\\u00A0]+", " ")
                .replaceAll(" *\\n *", "\n")
                .replaceAll("\\n{3,}", "\n\n")
                .strip();
    }
}
