package be.vdab.springdata.repositories;

import be.vdab.springdata.domain.Filiaal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface FiliaalRepository extends JpaRepository<Filiaal, Long> {
    List<Filiaal> findByGemeente(String gemeente);

    List<Filiaal> findByOmzetGreaterThanEqual(BigDecimal vanaf);

    List<Filiaal> findByGemeenteOrderByNaam(String gemeente);

    int countByGemeente(String gemeente);

    @Query("select avg(f.omzet) from Filiaal f")
    BigDecimal findGemiddeldeOmzet();

    List<Filiaal> findMetHoogsteOmzet();
}
