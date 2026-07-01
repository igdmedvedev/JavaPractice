package org.example.dao.common;

import org.example.entity.common.IEntity;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

public interface IEntityRepository<T extends IEntity> {
    List<T> loadData();
    void saveEntity(T e);
    boolean updateEntity(T e);
    boolean deleteEntity(Object pk);

    Object[] toRow(T e);
}
