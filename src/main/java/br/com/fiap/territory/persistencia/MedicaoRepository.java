package br.com.fiap.territory.persistencia;

import br.com.fiap.territory.dominio.Medicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

    List<Medicao> findBySensorCodigoOrderByIdAsc(String sensorCodigo);

    List<Medicao> findBySensorCodigoAndDataHoraGreaterThanEqualOrderByIdAsc(String sensorCodigo, LocalDateTime desde);
}
