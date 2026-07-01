package org.example.dao;

import org.example.dao.common.DbConnector;
import org.example.dao.common.IEntityRepository;
import org.example.entity.Creditor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

                result.add(new Creditor(id, name, age));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }

        return result;
    }

    @Override
    public void saveEntity(Creditor e) {
        String strStmt = """
                insert into creditor (name, age)
                values (?, ?)
                """;
        try (PreparedStatement stmt = DbConnector.getConnection().prepareStatement(strStmt)) {
            stmt.setString(1, e.getName());
            stmt.setInt(2, e.getAge());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean updateEntity(Creditor e) {
        String strStmt = """
                update creditor
                set name = ?, age = ?
                where id = ?
                """;
        try (PreparedStatement stmt = DbConnector.getConnection().prepareStatement(strStmt)) {
            stmt.setString(1, e.getName());
            stmt.setInt(2, e.getAge());
            stmt.setInt(3, e.getPk());

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
    public Object[] toRow(Creditor e) {
        return new Object[] {e.getPk(), e.getName(), e.getAge()};
    }
}
