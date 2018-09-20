package com.cse.cou.mobarak.bookshope;

/**
 * Created by Mobarak on 4/25/2018.
 */

public class ShowDept {
    int id;

    public ShowDept(int id, int amount, String dept_name) {
        this.id = id;
        this.amount = amount;
        this.dept_name = dept_name;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int amount;
    String dept_name;


    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }
}
