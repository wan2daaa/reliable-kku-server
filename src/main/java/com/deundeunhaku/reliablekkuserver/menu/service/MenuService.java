package com.deundeunhaku.reliablekkuserver.menu.service;

import com.deundeunhaku.reliablekkuserver.menu.dto.MenuResponse;
import com.deundeunhaku.reliablekkuserver.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public List<MenuResponse> getMenuList() {
        return MenuResponse.listOf(menuRepository.findAll());
    }
}
