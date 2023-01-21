package io.github.aquerr.futrzakbot.storage;

import io.github.aquerr.futrzakbot.storage.entity.WebGameRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebGameRequestStorage extends JpaRepository<WebGameRequest, Long>
{
    WebGameRequest findByToken(String token);
}
