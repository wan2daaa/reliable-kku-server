package com.deundeunhaku.reliablekkuserver.menu.dto;

public record CreateMenuRequest(
        String name,
        Integer price,
        String description

) {
    public static CreateMenuRequest of(String name, Integer price, String description) {
        return new CreateMenuRequest(name, price, description);
    }
}
