package com.moodflow.repository;

import com.moodflow.model.Emotion;
import com.moodflow.model.MoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * CAPA REPOSITORY: unico punto de acceso a los datos de MoodEntry.
 * Su responsabilidad es consultar y persistir informacion en la base de datos.
 * No conoce reglas de negocio ni nada de HTTP; solo sabe "hablar" con la BD,
 * apoyandose en Spring Data JPA para generar las consultas.
 */

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {

    // Ya implementado por Spring Data solo con el nombre del metodo (query method)
    List<MoodEntry> findByEmotion(Emotion emotion);

    // Util para las estadisticas "de este mes": trae los registros en un rango de fechas
    List<MoodEntry> findByDateBetween(LocalDate start, LocalDate end);

    // Ordenados del mas reciente al mas antiguo, para el historial
    List<MoodEntry> findAllByOrderByDateDesc();

    /*
     * TODO (tuyo): si quieres que el conteo por emocion se haga en la base de datos
     * en vez de en Java, puedes escribir algo asi:
     *
     * @Query("SELECT m.emotion AS emotion, COUNT(m) AS total " +
     *        "FROM MoodEntry m WHERE m.date BETWEEN :start AND :end " +
     *        "GROUP BY m.emotion ORDER BY total DESC")
     * List<Object[]> countByEmotionBetween(@Param("start") LocalDate start,
     *                                       @Param("end") LocalDate end);
     *
     * Es opcional: tambien puedes hacer el conteo en el Service usando
     * findByDateBetween(...) y agrupando con streams de Java (mas facil de depurar).
     */
}
