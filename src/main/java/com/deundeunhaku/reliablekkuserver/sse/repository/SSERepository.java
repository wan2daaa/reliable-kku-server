package com.deundeunhaku.reliablekkuserver.sse.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;

@Repository
public interface SSERepository {

    public void put(Long key, SseEmitter sseEmitter);

    public Optional<SseEmitter> get(Long key);

    Map<Long, SseEmitter> getAll();

    public void remove(Long key);

}
