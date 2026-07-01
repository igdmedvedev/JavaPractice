package org.example.ui;

import org.example.dao.common.IEntityRepository;
import org.example.entity.common.Column;
import org.example.entity.common.IEntity;
import org.example.entity.common.NonEditColumn;
import org.example.entity.common.SequenceColumn;

import javax.swing.*;
import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class EntityEditor<T extends IEntity> extends JDialog {
    private T entity;

    private boolean confirmed = false;
    private final Map<String, JTextField> fieldComponents = new HashMap<>();

    public EntityEditor(JFrame parent, String title, Class<T> entityClass, IEntityRepository<T> repository, boolean isNew, T selectedEntity) {
        super(parent, title, true);

        setLayout(new BorderLayout());
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Через рефлексию получаем все поля сущности и создаем для них поля ввода
        java.util.List<Field> fields = getFieldsWithColumnAnnotation(entityClass, Column.class);
        java.util.Map<String, String> inputMap = new java.util.HashMap<>();

        Map<String, String> predefinedValues;
        try {
            predefinedValues = getColumnsStringValue(selectedEntity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        for (Field field : fields) {
            field.setAccessible(true);

            // Пропускаем поля, значения которых генерируеются Sequence'ом
            if (isNew && field.isAnnotationPresent(SequenceColumn.class)) {
                continue;
            }

            String label = getColumnTitle(field);
            fieldsPanel.add(new JLabel(label + ":"));

            JTextField textField = new JTextField();
            if (!isNew && field.isAnnotationPresent(NonEditColumn.class)) {
                textField.setEditable(false);
            }

            if (predefinedValues != null) {
                textField.setText(predefinedValues.get(label));
            }

            fieldsPanel.add(textField);
            fieldComponents.put(field.getName(), textField);
        }

        add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            java.util.Map<String, String> values = new java.util.HashMap<>();
            for (java.util.Map.Entry<String, JTextField> entry : fieldComponents.entrySet()) {
                values.put(entry.getKey(), entry.getValue().getText());
            }

            try {
                entity = (T)entityClass.getConstructor(Map.class).newInstance(values);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }

            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    public static java.util.List<Field> getFieldsWithColumnAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        java.util.List<Field> annotatedFields = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(annotation)) {
                annotatedFields.add(field);
            }
        }

        return annotatedFields;
    }

    public static Map<String, String> getColumnsStringValue(Object entity) throws IllegalAccessException {
        if (entity == null) {
            return null;
        }

        Map<String, String> ret = new HashMap<>();
        for (Field field : getFieldsWithColumnAnnotation(entity.getClass(), Column.class)) {
            field.setAccessible(true);
            ret.put(getColumnTitle(field), String.valueOf(field.get(entity)));
        }
        return ret;
    }

    public static String getColumnTitle(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !column.title().isEmpty()) {
            return column.title();
        }
        return field.getName();
    }

    public void setInitialValues(Map<String, String> values) {
        if (values == null) {
            return;
        }
        for (java.util.Map.Entry<String, JTextField> entry : fieldComponents.entrySet()) {
            String fieldName = entry.getKey();
            if (values.containsKey(fieldName)) {
                entry.getValue().setText(values.get(fieldName));
            }
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public T getEntity() {
        return entity;
    }
}