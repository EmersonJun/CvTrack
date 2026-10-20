package br.com.cvmatch.analise.repository;

import java.time.Instant;
import java.util.UUID;

public interface PontoEvolucaoView {

    UUID getAnaliseId();

    Integer getNota();

    Instant getCriadoEm();

    String getCurriculoTitulo();

    Integer getVersao();

    String getVagaTitulo();
}
