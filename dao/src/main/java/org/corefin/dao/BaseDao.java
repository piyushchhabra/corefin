package org.corefin.dao;

import org.jdbi.v3.core.mapper.RowMapper;

public interface BaseDao<T> {
    void insert(T dto);
    T findById(String id);
    void registerRowMapper();

//    TODO: do update when needed
//    void update(T dto, String id);
}
