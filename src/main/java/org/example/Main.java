package org.example;

import org.example.dao.CreditorRepostitory;
import org.example.dao.common.DbConnector;
import org.example.entity.Creditor;
import org.example.ui.GenericTableFrame;

public class Main {
    public static void main(String[] args) {
        DbConnector connector = new DbConnector();

        CreditorRepostitory creditorRepostitory = new CreditorRepostitory();
        GenericTableFrame<Creditor> frame = new GenericTableFrame<>(
                "Creditors",
                Creditor.class,
                creditorRepostitory
        );
        frame.setVisible(true);
    }
}