package com.game.repository;

import com.game.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// благодаря этому интерфейсу сможем оперировать многими методами такими как findAll, findOne
@Repository
public interface PlayerRepo extends JpaRepository<Player, Long> {

}