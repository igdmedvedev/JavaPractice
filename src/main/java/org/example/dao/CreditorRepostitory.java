package org.example.dao;

import org.example.dao.common.DbConnector;
import org.example.dao.common.IEntityRepository;
import org.example.entity.Creditor;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreditorRepostitory implements IEntityRepository<Creditor> {
    @Override
    public List<Creditor> loadData() {
        List<Creditor> result = new ArrayList<>();
        //Обязательно делаем try-with-resources для оптимизации
        //https://javarush.com/quests/lectures/questsyntaxpro.level15.lecture00
        try (Statement stmt = DbConnector.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM creditor order by id");
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String name = rs.getString("name");
                Integer age = rs.getInt("age");
                Date startDate = rs.getDate("startDate");

                LocalDate startLocalDate = startDate != null ? startDate.toLocalDate() : null;

                result.add(new Creditor(id, name, age, startLocalDate));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }

        return result;
    }

    @Override
    public void saveEntity(Creditor e) {
        String strStmt = """
                insert into creditor (name, age, startDate)
                values (?, ?, ?)
                """;
        try (PreparedStatement stmt = DbConnector.getConnection().prepareStatement(strStmt)) {
            stmt.setString(1, e.getName());
            stmt.setInt(2, e.getAge());
            stmt.setDate(3, e.getStartDate());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean updateEntity(Creditor e) {
        String strStmt = """
                update creditor
                set name = ?, age = ?, startDate = ?
                where id = ?
                """;
        try (PreparedStatement stmt = DbConnector.getConnection().prepareStatement(strStmt)) {
            stmt.setString(1, e.getName());
            stmt.setInt(2, e.getAge());
            stmt.setDate(3, e.getStartDate());
            stmt.setInt(4, e.getPk());

            return stmt.executeUpdate() != 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean deleteEntity(Object pk) {
        String strStmt = """
                delete from creditor where id = ?
                """;
        try (PreparedStatement stmt = DbConnector.getConnection().prepareStatement(strStmt)) {
            stmt.setInt(1, (Integer)pk);

            //если нет строки с таким ид., то возвращаем false
            return stmt.executeUpdate() != 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean deleteEntityByName(String name) {
        //так делать нельзя, показываю только в учебных целях
        String strStmt = String.format("delete from creditor where name = '%s'", name);
        try (PreparedStatement stmt = DbConnector.getConnection().prepareStatement(strStmt)) {
            return stmt.executeUpdate() != 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
