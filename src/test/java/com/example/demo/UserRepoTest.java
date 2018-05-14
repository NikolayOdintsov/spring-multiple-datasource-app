package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by nikolay.odintsov on 14.05.18.
 */


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepoTest {

    @Autowired
    @Qualifier("userRepoDe")
    private UserRepo userRepoDe;

    @Autowired
    @Qualifier("userRepoUs")
    private UserRepo userRepoUs;

    @Autowired
    @Qualifier("entityManagerFactory")
    private EntityManager entityManagerDE;

    @Autowired
    @Qualifier("entityManagerFactoryUs")
    private EntityManager entityManagerUS;

    @Autowired
    @Qualifier("dataSourceDE")
    private DataSource dataSourceDe;

    @Autowired
    @Qualifier("dataSourceUS")
    private DataSource dataSourceUs;

    @Test
    @Transactional(transactionManager = "transactionManager")
    public void shouldSaveUserDeDb() {
        //given
        User user = new User();
        user.setName("A");
        assertThat(user.getId()).isNull();

        //when
        user = userRepoDe.save(user);
        entityManagerDE.flush();

        //then
        assertThat(user.getId()).isNotNull();
        assertThat(user.getName()).isEqualTo("A");
    }

    @Test
    @Transactional(transactionManager = "transactionManagerUs")
    public void shouldSaveUserUsDb() {
        //given
        User user = new User();
        user.setName("B");
        assertThat(user.getId()).isNull();

        //when
        user = userRepoUs.save(user);
        entityManagerUS.flush();

        //then
        assertThat(user.getId()).isNotNull();
        assertThat(user.getName()).isEqualTo("B");
    }

    @Test
    @Transactional(transactionManager = "transactionManager")
    public void shouldFindUserByNameInDeDB() throws SQLException {
        //given
        this.createDB();

        //when
        User user = userRepoDe.findByName("DE");

        //then
        assertThat(user.getId()).isNotNull();
        assertThat(user.getName()).isEqualTo("DE");
    }

    @Test
    @Transactional(transactionManager = "transactionManagerUs")
    public void shouldFindUserByNameInUsDB() throws SQLException {
        //given
        this.createDB();

        //when
        User user = userRepoUs.findByName("US");

        //then
        assertThat(user.getId()).isNotNull();
        assertThat(user.getName()).isEqualTo("US");
    }

    public void createDB() throws SQLException {
        Random rand = new Random();

        Connection connection1 = dataSourceDe.getConnection();
        connection1.setAutoCommit(true);
        Statement stmtDelete1 = connection1.createStatement();
        stmtDelete1.executeUpdate("DELETE FROM user;");
        stmtDelete1.close();

        PreparedStatement stmt1 = connection1.prepareStatement("INSERT INTO user(id, name) VALUES(?, 'DE');");
        stmt1.setInt(1, rand.nextInt(10));
        stmt1.execute();
        stmt1.close();
        connection1.close();

        Connection connection2 = dataSourceUs.getConnection();
        connection2.setAutoCommit(true);
        Statement stmtDelete2 = connection2.createStatement();
        stmtDelete2.executeUpdate("DELETE FROM user;");
        stmtDelete2.close();

        PreparedStatement stmt2 = connection2.prepareStatement("INSERT INTO user(id, name) VALUES(?, 'US');");
        stmt2.setInt(1, rand.nextInt(10));
        stmt2.execute();
        stmt2.close();
        connection2.close();
    }
}
