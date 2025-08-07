package com.tarea.security;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    // Conjunto en memoria para guardar tokens inválidos
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    // Agrega un token a la lista negra
    public void blacklist(String token) {
        blacklist.add(token);
    }

    // Verifica si un token está en la lista negra
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
