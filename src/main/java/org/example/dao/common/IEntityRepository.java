package org.example.dao.common;

import org.example.entity.common.Column;
import org.example.entity.common.IEntity;

import javax.swing.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IEntityRepository<T extends IEntity> {
    List<T> loadData();
    void saveEntity(T e);
    boolean updateEntity(T e);
    boolean deleteEntity(Object pk);
    boolean deleteEntityByName(String name);
}
