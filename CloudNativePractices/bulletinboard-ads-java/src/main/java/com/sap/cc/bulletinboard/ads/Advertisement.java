package com.sap.cc.bulletinboard.ads;
import javax.persistence.*;
import java.math.BigDecimal;
@Entity
public class Advertisement {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String title;
    private String contact;

    private String currency;

    private BigDecimal price;
    @Transient
    @OneToOne
    private Number averageContactRating;

    public Number getAverageContactRating() {
        return averageContactRating;
    }

    public void setAverageContactRating(Number averageContactRating) {
        this.averageContactRating = averageContactRating;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    @Override
    public String toString() {
        return "Advertisement [id=" + id + ", title=" + title + ", contact=" + contact + ", price=" + price + ", currency=" + currency + "]";
    }

    public Advertisement() {
    }

    public Advertisement(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
