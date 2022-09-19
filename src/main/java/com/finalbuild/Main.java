package com.finalbuild;

import java.sql.Timestamp;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Timestamp ts=new Timestamp(System.currentTimeMillis());
        Date date=new Date(ts.getTime());
        System.out.println(date.getHours());
        System.out.println(date);
    }
}
