package br.com.fiap.territory.persistencia;

import br.com.fiap.territory.dominio.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findAllByOrderByIdDesc();

    boolean existsBySensorCodigoAndNivelAndDataHoraGreaterThanEqual(String sensorCodigo, String nivel, LocalDateTime desde);
}
