package be.vdab.springdata.repositories;

import be.vdab.springdata.domain.Filiaal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FiliaalRepository extends JpaRepository<Filiaal, Long> {

    List<Filiaal> findByGemeente(String gemeente);

    List<Filiaal> findByGemeenteOrderByNaam(String gemeente);
    //Je typt OrderBy. Je typt daarna het attribuut waarop je wil sorteren.
    List<Filiaal> findByOmzetGreaterThanEqual(BigDecimal vanaf);

    List<Filiaal> findByOmzetIsNull();

    List<Filiaal> findByOmzetBetween(BigDecimal van, BigDecimal tot);

    List<Filiaal> findByNaamStartingWith(String woord);

    List<Filiaal> findByNaamContaining(String woord);

    List<Filiaal> findByNaamIn(Set<String> namen);

    //Filialen waarvan de naam één van de namen in de verzameling namen is.
    List<Filiaal> findByNaamAndAndGemeente(String naam, String gemeente);
    //Filialen waarvan de naam gelijk is aan naam én de gemeente gelijk is aan gemeente.
    int countByGemeente(String gemeente);

    @Query("select avg(f.omzet) from Filiaal f")
    BigDecimal findGemiddeldeOmzet();

    List<Filiaal> findMetHoogsteOmzet();

}
