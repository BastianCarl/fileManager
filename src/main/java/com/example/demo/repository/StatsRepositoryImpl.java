package com.example.demo.repository;

import com.example.demo.model.FileUploaderClient;
import com.example.demo.model.dto.DailyUploadDTO;
import com.example.demo.model.dto.ExtensionStatsDTO;
import com.example.demo.model.dto.FailedUploadDTO;
import com.example.demo.model.dto.TopUploaderDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class StatsRepositoryImpl implements StatsRepository {

  @PersistenceContext private EntityManager em;

  // 🔥 TOP UPLOADERS
  @Override
  public List<TopUploaderDTO> topUploaders(Long from, int limit) {

    String sql =
        """
            SELECT
                f.owner_id,
                u.user_name,
                COUNT(f.id) AS upload_count,
                COALESCE(SUM(f.size), 0) AS total_size
            FROM file_metadata f
            JOIN users u ON u.id = f.owner_id
            WHERE f.upload_time >= :from
            GROUP BY f.owner_id, u.user_name
            ORDER BY upload_count DESC
            LIMIT :limit
        """;

    List<Object[]> result =
        em.createNativeQuery(sql)
            .setParameter("from", from)
            .setParameter("limit", limit)
            .getResultList();

    return result.stream()
        .map(
            r ->
                new TopUploaderDTO(
                    ((Number) r[0]).longValue(),
                    (String) r[1],
                    ((Number) r[2]).longValue(),
                    ((Number) r[3]).longValue()))
        .toList();
  }

  // 📊 DAILY UPLOADS (PostgreSQL version)
  @Override
  public List<DailyUploadDTO> uploadsPerDay(Long from, Long to) {

    String sql =
        """
            SELECT
                DATE(TO_TIMESTAMP(f.upload_time / 1000)) AS day,
                COUNT(*) AS cnt,
                COALESCE(SUM(f.size), 0) AS total_size
            FROM file_metadata f
            WHERE f.upload_time BETWEEN :from AND :to
            GROUP BY day
            ORDER BY day ASC
        """;

    List<Object[]> result =
        em.createNativeQuery(sql).setParameter("from", from).setParameter("to", to).getResultList();

    return result.stream()
        .map(
            r ->
                new DailyUploadDTO(
                    ((java.sql.Date) r[0]).toLocalDate(),
                    ((Number) r[1]).longValue(),
                    ((Number) r[2]).longValue()))
        .toList();
  }

  // 🧩 GROUP BY EXTENSION (mimeType)
  @Override
  public List<ExtensionStatsDTO> groupByExtension(Long from, Long to) {

    String sql =
        """
            SELECT
                f.mime_type,
                COUNT(*) AS cnt
            FROM file_metadata f
            WHERE f.upload_time BETWEEN :from AND :to
            GROUP BY f.mime_type
            ORDER BY cnt DESC
        """;

    List<Object[]> result =
        em.createNativeQuery(sql).setParameter("from", from).setParameter("to", to).getResultList();

    return result.stream()
        .map(r -> new ExtensionStatsDTO((String) r[0], ((Number) r[1]).longValue()))
        .toList();
  }

  // ❌ FAILED UPLOADS (JOIN pe code)
  @Override
  public List<FailedUploadDTO> failedUploads(Long from, FileUploaderClient client) {

    StringBuilder sql =
        new StringBuilder(
            """
        SELECT
            a.code,
            f.owner_id,
            f.upload_time,
            a.step
        FROM file_audit_state a
        JOIN file_metadata f ON f.code = a.code
        WHERE f.upload_time >= :from
          AND a.step != 'DONE'
    """);

    if (client != null) {
      sql.append(" AND f.file_uploader_client = :client");
    }

    sql.append(" ORDER BY f.upload_time DESC");

    var query = em.createNativeQuery(sql.toString()).setParameter("from", from);

    if (client != null) {
      query.setParameter("client", client.name());
    }

    List<Object[]> result = query.getResultList();

    return result.stream()
        .map(
            r ->
                new FailedUploadDTO(
                    (String) r[0],
                    ((Number) r[1]).longValue(),
                    ((Number) r[2]).longValue(),
                    (String) r[3]))
        .toList();
  }
}
