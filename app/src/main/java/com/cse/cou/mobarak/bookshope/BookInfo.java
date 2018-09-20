package com.cse.cou.mobarak.bookshope;

/**
 * Created by Mobarak on 4/26/2018.
 */

public class BookInfo {
    int book_id;
    String book_name;

    String edition;
    String writer;
    String dept;
    String price;
    String img_link;
    String book_contract_number;
    int book_seller_id;
    public BookInfo(int book_id, String book_name, String edition, String writer, String dept, String img_link, String price, String book_contract_number, int book_seller_id) {
        this.book_id = book_id;
        this.book_name = book_name;
        this.edition = edition;
        this.writer = writer;
        this.dept = dept;
        this.img_link = img_link;
        this.price = price;
        this.book_contract_number = book_contract_number;
        this.book_seller_id = book_seller_id;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImg_link() {
        return img_link;
    }

    public void setImg_link(String img_link) {
        this.img_link = img_link;
    }

    public String getBook_contract_number() {
        return book_contract_number;
    }

    public void setBook_contract_number(String book_contract_number) {
        this.book_contract_number = book_contract_number;
    }

    public int getBook_seller_id() {
        return book_seller_id;
    }

    public void setBook_seller_id(int book_seller_id) {
        this.book_seller_id = book_seller_id;
    }
}
