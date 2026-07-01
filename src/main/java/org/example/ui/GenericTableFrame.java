package org.example.ui;

import org.example.dao.common.IEntityRepository;
import org.example.entity.common.Column;
import org.example.entity.common.IEntity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.*;
import java.util.List;
import java.util.*;

public class GenericTableFrame<T extends IEntity> extends JFrame {

    private final Class<T> entityClass;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<T> data;
    private final IEntityRepository<T> repository;

    public GenericTableFrame(String title, Class<T> entityClass, IEntityRepository<T> repository) {
        this.entityClass = entityClass;
        this.repository = repository;

        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        initUI();
        refreshTable();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 1. Создаем модель таблицы (пока пустую, с 0 строк)
        // Колонки будут определены при первом обновлении через рефлексию
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // 2. Панель с кнопками
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(this::onAdd);
        btnEdit.addActionListener(this::onEdit);
        btnDelete.addActionListener(this::onDelete);
        btnRefresh.addActionListener(e -> refreshTable());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        // Загружаем данные из БД через переданную лямбду
        data = repository.loadData();

        if (data == null || data.isEmpty()) {
            setupColumnsFromEntity();
            tableModel.setRowCount(0);
            return;
        }

        // Определяем колонки по первой записи
        setupColumnsFromEntity();

        // Очищаем и заполняем строки
        tableModel.setRowCount(0);
        for (T entity : data) {
            tableModel.addRow(entityToRow(entity));
        }
    }

    private void setupColumnsFromEntity() {
        try {
            List<String> titles = new ArrayList<>();

            Class<?> clazz = entityClass;
            while (clazz != null && clazz != Object.class) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Column.class)) {
                        Column column = field.getAnnotation(Column.class);
                        titles.add(column.title());
                    }
                }
                clazz = clazz.getSuperclass();
            }

            tableModel.setColumnIdentifiers(titles.toArray(new String[0]));
        } catch (Exception e) {
            tableModel.setColumnIdentifiers(new String[]{"ID", "Data"});
        }
    }

    private Object[] entityToRow(T entity) {
        return repository.toRow(entity);
    }

    private void onAdd(ActionEvent e) {
        try {
            EntityEditor<T> dialog = new EntityEditor<>(this, "Add row", entityClass, repository, true, null);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                T entityToSave = dialog.getEntity();
                repository.saveEntity(entityToSave);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Row added");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void onEdit(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        T selected =  null;
        if (selectedRow != -1) {
            selected = data.get(selectedRow);
        }

        EntityEditor<T> dialog = new EntityEditor<>(this, "Edit row", entityClass, repository, false, selected);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            T updatedEntity = dialog.getEntity();
            repository.updateEntity(updatedEntity);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Row updated!");
        }
    }

    private void onDelete(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select the row to delete");
            return;
        }

        T selected = data.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Do you really want to delete row?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            repository.deleteEntity(selected.getPk());
            refreshTable();
            JOptionPane.showMessageDialog(this, "Row deleted");
        }
    }
}