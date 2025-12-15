package com.warehouse.warehouse_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class MpDemoApplicationTests {

    // 1. 自动注入数据源，这是 Spring Boot 帮你配置好的
    @Autowired
    private DataSource dataSource;

    @Test
    void testConnection() throws SQLException {
        // 2. 尝试获取一个连接
        System.out.println("开始连接数据库...");

        Connection connection = dataSource.getConnection();

        // 3. 打印连接信息
        System.out.println("连接成功！当前连接对象: " + connection);

        // 4.哪怕连接成功，也可以顺便打印一下数据库产品名称，确保连对库了
        System.out.println("数据库名称: " + connection.getMetaData().getDatabaseProductName());

        // 5. 关闭连接 (虽然测试结束后会自动销毁，但养成好习惯)
        connection.close();
    }
}
