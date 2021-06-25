package be.vdab.springdata.repositories;

import be.vdab.springdata.domain.Filiaal;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Sql("/insertFilialen.sql")
class FiliaalRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
    private static final String FILIALEN = "filialen";
    private final FiliaalRepository repository;

    FiliaalRepositoryTest(FiliaalRepository repository) {
        this.repository = repository;
    }

    private long idVanAlfa() {
        return jdbcTemplate.queryForObject(
                "select id from filialen where naam = 'Alfa'", Long.class);
    }

    private long idVanBravo() {
        return jdbcTemplate.queryForObject(
                "select id from filialen where naam = 'Bravo'", Long.class);
    }

    @Test
    void count() {
        assertThat(repository.count()).isEqualTo(countRowsInTable(FILIALEN));
    }

    @Test
    void findById() {
        assertThat(repository.findById(idVanAlfa())).hasValueSatisfying(
                filiaal -> assertThat(filiaal.getNaam()).isEqualTo("Alfa"));
    }

    @Test
    void findAll() {
        assertThat(repository.findAll()).hasSize(countRowsInTable(FILIALEN));
    }

    @Test
    void findAllGesorteerdOpGemeente() {
        assertThat(repository.findAll(Sort.by("gemeente")))
                .hasSize(super.countRowsInTable(FILIALEN))
                .extracting(Filiaal::getGemeente)
                .isSortedAccordingTo(String::compareToIgnoreCase);
    }

    @Test
    void findAllById() {
        var idAlfa = idVanAlfa();
        var idBravo = idVanBravo();
        assertThat(repository.findAllById(List.of(idVanAlfa(), idVanBravo())))
                .extracting(Filiaal::getId)
                .containsOnly(idAlfa, idBravo);
    }

    @Test
    void save() {
        var filiaal = new Filiaal("Delta", "Brugge", BigDecimal.TEN);
        repository.save(filiaal);
        var id = filiaal.getId();
        assertThat(id).isPositive();
        assertThat(countRowsInTableWhere(FILIALEN, "id=" + id)).isOne();
    }

    @Test
    void deleteById() {
        var id = idVanAlfa();
        repository.deleteById(id);
        repository.flush();
        assertThat(countRowsInTableWhere(FILIALEN, "id=" + id)).isZero();
    }

    @Test
    void deleteByOnbestandeId() {
        assertThatExceptionOfType(EmptyResultDataAccessException.class).isThrownBy(
                () -> repository.deleteById(-1L));
    }

    @Test
    void findByGemeente() {
        assertThat(repository.findByGemeente("Brussel"))
                .hasSize(countRowsInTableWhere(FILIALEN, "gemeente='Brussel'"))
                .allSatisfy(filiaal ->
                        assertThat(filiaal.getGemeente()).isEqualToIgnoringCase("Brussel"));
    }

    @Test
    void findByGemeenteOrderByNaam() {
        assertThat(repository.findByGemeenteOrderByNaam("Brussel"))
                .hasSize(countRowsInTableWhere(FILIALEN, "gemeente='Brussel'"))
                .allSatisfy(filiaal ->
                        assertThat(filiaal.getGemeente()).isEqualToIgnoringCase("Brussel"))
                .extracting(Filiaal::getNaam)
                .isSortedAccordingTo(String::compareToIgnoreCase);
    }

    @Test
    void findByOmzetGreaterThanEqual() {
        assertThat(repository.findByOmzetGreaterThanEqual(BigDecimal.valueOf(2000)))
                .hasSize(countRowsInTableWhere(FILIALEN, "omzet>=2000"))
                .allSatisfy(filiaal ->
                        assertThat(filiaal.getOmzet()).isGreaterThanOrEqualTo(BigDecimal.valueOf(2000)));
    }

    @Test
    void countByGemeente() {
        assertThat(repository.countByGemeente("Brussel"))
                .isEqualTo(countRowsInTableWhere(FILIALEN, "gemeente='Brussel'"));
    }

    @Test
    void findGemiddeldeOmzet() {
        assertThat(repository.findGemiddeldeOmzet())
                .isEqualByComparingTo(jdbcTemplate.queryForObject(
                        "select avg(omzet) from filialen", BigDecimal.class));
    }

    @Test
    void findMetHoogsteOmzet() {
        assertThat(repository.findMetHoogsteOmzet())
                .hasSize(countRowsInTableWhere(FILIALEN, "omzet=(select max(omzet) from filialen)"))
                .first()
                .extracting(Filiaal::getNaam).isEqualTo(
                        jdbcTemplate.queryForObject(
                                "select naam from filialen where omzet = (select max(omzet) from filialen)",
                                String.class));
    }
}