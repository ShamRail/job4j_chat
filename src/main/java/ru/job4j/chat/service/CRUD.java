package ru.job4j.chat.service;

import java.util.List;
import java.util.Optional;

public interface CRUD<T> {

    T saveOrUpdate(T entity);
    void delete(T entity);
    List<T> findAll();
    Optional<T> findById(int id);

}
