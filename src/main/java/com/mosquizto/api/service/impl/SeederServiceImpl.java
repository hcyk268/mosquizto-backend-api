package com.mosquizto.api.service.impl;

import com.mosquizto.api.service.SeederService;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@RequiredArgsConstructor
@Service
@Slf4j
public class SeederServiceImpl implements SeederService {

    final private DataSource dataSource; // Dùng để lấy Connection cho ScriptUtils

    final private JdbcTemplate jdbcTemplate; // Dùng để gọi thủ tục

    /**
     * Hàm này sẽ tự động nạp Procedure vào DB và chạy seed cho user cụ thể
     */
    public void initAndSeedDataForUser(int userId) {
        try (Connection conn = dataSource.getConnection()) {

            log.info("1. Đang nạp Procedure...");
            ClassPathResource resource = new ClassPathResource("seeders/create_seed_procedure_v1.sql");

            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(resource);
            populator.setSeparator(";;");
            populator.setCommentPrefixes("--", "/*");
            populator.populate(conn);

            log.info("=> Procedure tạo thành công!");

            log.info("2. Đang chạy seed cho User ID: {}", userId);
            jdbcTemplate.execute("CALL seed_user_collections(" + userId + ")");
            log.info("=> Seed hoàn tất!");

        } catch (Exception e) {
            log.error("Lỗi trong quá trình seed: ", e);
        }
    }
}
