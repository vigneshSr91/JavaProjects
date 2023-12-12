package com.sap.bulletinboard.ads.models;

import java.math.BigDecimal;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ads")
@SequenceGenerator(name = "advertisement_id_generator", sequenceName = "advertisement_sequence", initialValue = 1,
        allocationSize = 1)
public class Advertisement {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "advertisement_id_generator")
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @Embedded
    private EntityMetaData metaData = new EntityMetaData();

    @NotBlank
    @Column(name = "title")
    private String title;

    @NotBlank
    @Column(name = "contact")
    private String contact;

    @NotNull
    @Column(name = "price", precision = 12, scale = 3)
    private BigDecimal price;

    @NotBlank
    @Column(name = "currency")
    private String currency;



    public Advertisement() {
    }

    public Advertisement(String title, String contact, BigDecimal price, String currency) {
        this.title = title;
        this.contact = contact;
        this.price = price;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public EntityMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(EntityMetaData metaData) {
        this.metaData = metaData;
    }

    public Instant getCreatedAt() {
        return metaData.getCreatedAt();
    }

    public Instant getModifiedAt() {
        return metaData.getUpdatedAt();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Advertisement [id=" + id + ", title=" + title + "]";
    }
}
