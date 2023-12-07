package com.company.ces_productive.app;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BookNumbGenerate implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BookNumbGenerate.class);
    private AtomicInteger nextNumber;
    private static final String SERIAL_FILE = "booknumber.ser";

    public BookNumbGenerate() {
        try {
            FileInputStream fileInputStream = new FileInputStream(SERIAL_FILE);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            nextNumber = (AtomicInteger) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            nextNumber = new AtomicInteger();
        }
    }

    public String getNextNumber(String bookType,  Integer bookSerialNumber, LocalDate bookDate) {
        int ordinalNumber = nextNumber.getAndIncrement();
        String formattedNumber = String.format("%s/%s/%s/%09d", bookType, bookSerialNumber, bookDate, ordinalNumber);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(SERIAL_FILE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(nextNumber);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            log.error("Error", e);
        }
        return formattedNumber;
    }
}
