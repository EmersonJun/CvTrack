package br.com.cvmatch.support;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

public final class PdfTestUtils {

    private PdfTestUtils() {
    }

    public static byte[] pdfComTexto(String... linhas) {
        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage();
            documento.addPage(pagina);

            try (PDPageContentStream conteudo = new PDPageContentStream(documento, pagina)) {
                conteudo.beginText();
                conteudo.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                conteudo.newLineAtOffset(50, 700);
                for (String linha : linhas) {
                    conteudo.showText(linha);
                    conteudo.newLineAtOffset(0, -18);
                }
                conteudo.endText();
            }
            return salvar(documento);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static byte[] curriculo(String sufixo) {
        return pdfComTexto(
                "Emerson Junior - Desenvolvedor Java",
                "Experiencia com Java 21, Spring Boot, PostgreSQL e Docker.",
                "Projetos: CV Match, API REST com JWT e testes automatizados.",
                "Versao: " + sufixo);
    }

    public static byte[] pdfComPaginasEmBranco(int quantidade) {
        try (PDDocument documento = new PDDocument()) {
            for (int i = 0; i < quantidade; i++) {
                documento.addPage(new PDPage());
            }
            return salvar(documento);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static byte[] salvar(PDDocument documento) throws IOException {
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        documento.save(saida);
        return saida.toByteArray();
    }
}
