package com.example.dao;

import com.example.model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAOImpl implements TaskDAO {
    private static final String URL = "jdbc:sqlite:todo.db";

    /*
     * The SQLite driver is registered with the JVM when the driver class is
     * loaded. In some servlet containers (Tomcat, Jetty, etc.) the
     * web‑application class loader is not the same as the one used by
     * `DriverManager`, which can prevent the driver from being found. Explicitly
     * loading the class here (or placing the driver jar in Tomcat's
     * lib/ directory) ensures the driver is registered and prevents the
     * "No suitable driver" SQLException seen at runtime.
     */
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            // this should never happen if the dependency is present
            throw new ExceptionInInitializerError(e);
        }
    }

    public TaskDAOImpl() {
        // ensure table exists
        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS tasks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT NOT NULL, " +
                    "completed INTEGER NOT NULL DEFAULT 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    @Override
    public List<Task> getAll() {
        List<Task> list = new ArrayList<>();
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement("SELECT id,title,completed FROM tasks");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Task t = new Task(rs.getInt("id"), rs.getString("title"), rs.getInt("completed") != 0);
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Task get(int id) {
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement("SELECT id,title,completed FROM tasks WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Task(rs.getInt("id"), rs.getString("title"), rs.getInt("completed") != 0);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int create(Task task) {
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO tasks(title,completed) VALUES(?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, task.getTitle());
            ps.setInt(2, task.isCompleted() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean update(Task task) {
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement("UPDATE tasks SET title=?, completed=? WHERE id=?")) {
            ps.setString(1, task.getTitle());
            ps.setInt(2, task.isCompleted() ? 1 : 0);
            ps.setInt(3, task.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM tasks WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}