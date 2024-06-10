package com.mybank.gui;

import com.mybank.data.DataSource;
import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;
import com.mybank.reporting.CustomerReport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class Main {

    private final JEditorPane log;
    private final JButton show;
    private final JButton report;
    private final JComboBox clients;

    public Main() {
        log = new JEditorPane("text/html", "");
        log.setPreferredSize(new Dimension(350, 450));
        show = new JButton("Show");
        report = new JButton("Report");
        clients = new JComboBox();

        for (int i=0; i < Bank.getNumberOfCustomers(); i++) {
            clients.addItem(Bank.getCustomer(i).getLastName()+", "+Bank.getCustomer(i).getFirstName());
        }
    }
    
    private void launchFrame() {
        JFrame frame = new JFrame("MyBank clients");
        frame.setLayout(new BorderLayout());
        JPanel cpane = new JPanel();
        ScrollPaneLayout scrollPane = new ScrollPaneLayout();
        cpane.setLayout(new GridLayout(1, 3));
        
        cpane.add(clients);
        cpane.add(show);
        cpane.add(report);
        frame.add(cpane, BorderLayout.NORTH);
        frame.add(log, BorderLayout.CENTER);
        
        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Customer current = Bank.getCustomer(clients.getSelectedIndex());
                String accType = current.getAccount(0)instanceof CheckingAccount?"Checking":"Savings";                
                String custInfo="<br>&nbsp;<b><span style=\"font-size:2em;\">"+current.getLastName()+", "+
                        current.getFirstName()+"</span><br><hr>"+
                        "&nbsp;<b>Acc Type: </b>"+accType+
                        "<br>&nbsp;<b>Balance: <span style=\"color:red;\">$"+current.getAccount(0).getBalance()+"</span></b>";
                log.setText(custInfo);
            }
        });

        report.addActionListener(e -> {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(byteStream);

            System.setOut(printStream);

            new CustomerReport().generateReport();  // Як так, він виводить все в консоль :(

            System.out.flush();
            System.setOut(System.out);

            log.setText(byteStream.toString().replace("\n", "<br>"));
            log.setAutoscrolls(true);
        });
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        frame.setResizable(false);
        frame.setVisible(true);        
    }
    
    public static void main(String[] args) {
        Locale.setDefault(new Locale("en", "US"));

        try {
            new DataSource("./data/test.dat").loadData();
        } catch (IOException e) {
            System.out.println("Не вдалося завантажити дані про клієнтів");
        }
        
        Main demo = new Main();
        demo.launchFrame();
    }
    
}
