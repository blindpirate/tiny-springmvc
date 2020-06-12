package com.github.hcsp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationIntegrationTest {
    @Autowired
    Environment environment;

    @Test
    public void test() throws Exception {
        String url = "http://localhost:" + environment.getProperty("local.server.port") + "/rank.htm";

        // 第一次访问
        Document html = Jsoup.parse(new URL(url), 60 * 1000);
        Assertions.assertEquals(
                Arrays.asList("1", "桃子", "14700", "2", "葡萄", "8000", "3", "香蕉", "6000", "4", "西瓜", "3500", "5", "苹果", "3100"),
                html.select("td").stream().map(Element::text).map(s -> s.replace(",", "").trim()).collect(Collectors.toList())
        );

        runSql("insert into goods (name)values('肥皂')");

        // 第二次访问，应该从缓存中获取，得到旧数据
        html = Jsoup.parse(new URL(url), 60 * 1000);
        Assertions.assertEquals(
                Arrays.asList("1", "桃子", "14700", "2", "葡萄", "8000", "3", "香蕉", "6000", "4", "西瓜", "3500", "5", "苹果", "3100"),
                html.select("td").stream().map(Element::text).map(s -> s.replace(",", "").trim()).collect(Collectors.toList())
        );

        Thread.sleep(2000);

        // 第三次访问，应该得到更新后的数据
        html = Jsoup.parse(new URL(url), 60 * 1000);
        Assertions.assertEquals(
                Arrays.asList("1", "桃子", "14700", "2", "葡萄", "8000", "3", "香蕉", "6000", "4", "西瓜", "3500", "5", "苹果", "3100", "6", "肥皂", "0"),
                html.select("td").stream().map(Element::text).map(s -> s.replace(",", "").trim()).collect(Collectors.toList())
        );
    }

    @AfterEach
    public void cleanUp() throws SQLException {
        runSql("delete from goods where name = '肥皂'");
    }

    private void runSql(String sql) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mall?characterEncoding=utf-8", "root", "123456");
        try (Statement statement = conn.createStatement()) {
            statement.execute(sql);
        }
    }
}
