package com.deundeunhaku.reliablekkuserver.store.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ColumnDefault("false")
    private Boolean isOpened;

    public Store(Boolean isOpened) {
        this.isOpened = isOpened;
    }

    @Builder
    public Store(Long id, Boolean isOpened) {
        this.id = id;
        this.isOpened = isOpened;
    }
}
