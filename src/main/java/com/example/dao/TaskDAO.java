package com.example.dao;

import com.example.model.Task;
import java.util.List;

public interface TaskDAO {
    List<Task> getAll();

    Task get(int id);

    int create(Task task);

    boolean update(Task task);

    boolean delete(int id);
}