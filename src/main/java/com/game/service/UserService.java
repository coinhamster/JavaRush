package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@Transactional
public class UserService {
    final PlayerRepo playerRepo;

    public UserService(PlayerRepo playerRepo) {
        this.playerRepo = playerRepo;
    }

    public List<Player> getAll(String name, String title, Race race, Profession profession,
                                           Long after, Long before, Boolean banned, Integer minExperience,
                                           Integer maxExperience, Integer minLevel, Integer maxLevel) {
        List<Player> players = new ArrayList<>();
        final Date afterDate = after == null ? null : new Date(after);
        final Date beforeDate = before == null ? null : new Date(before);

        playerRepo.findAll().forEach(player -> {
            if (name!=null && !player.getName().contains(name)) return;
            if (title!=null && !player.getTitle().contains(title)) return;
            if (race != null && player.getRace() != race) return;
            if (profession != null && player.getProfession() != profession) return;
            if (after != null && player.getBirthday().before(afterDate)) return;
            if (before != null && player.getBirthday().after(beforeDate)) return;
            if (banned != null && player.getBanned() != banned) return;
            if (minExperience != null && player.getExperience().compareTo(minExperience) < 0) return;
            if (maxExperience != null && player.getExperience().compareTo(maxExperience) > 0) return;
            if (minLevel != null && player.getLevel().compareTo(minLevel) < 0) return;
            if (maxLevel != null && player.getLevel().compareTo(maxLevel) > 0) return;
            players.add(player);
        });
        return players;
    }

    public List<Player> getSortedPlayers(List<Player> filteredPlayers, Integer page, Integer countOnPage, PlayerOrder order){
        int pageNum = page + 1;
        int count = countOnPage;
        List<Player> sortedPlayers = new ArrayList<>();
        if (order.equals(PlayerOrder.NAME))
            filteredPlayers.sort(Comparator.comparing(Player::getName));
        else if (order.equals(PlayerOrder.EXPERIENCE))
            filteredPlayers.sort(Comparator.comparing(Player::getExperience));
        else if (order.equals(PlayerOrder.BIRTHDAY))
            filteredPlayers.sort(Comparator.comparing(Player::getBirthday));
        for (int i = pageNum * count - (count - 1) - 1; i < count * pageNum && i < filteredPlayers.size(); i++) {
            sortedPlayers.add(filteredPlayers.get(i));
        }
        return sortedPlayers;
    }


    public Player create(Player player) {
        if (
                player.getName() == null
                        || player.getTitle() == null
                        || player.getRace() == null
                        || player.getProfession() == null
                        || player.getBirthday() == null
                        || player.getExperience() == null

                        // Условия
                        || player.getTitle().length() > 30
                        || player.getName().length() > 12
                        || player.getName().equals("")
                        || player.getExperience() < 0
                        || player.getExperience() > 10000000
                        || player.getBirthday().getTime() < 0
                        || player.getBirthday().before(new Date(946684800000L))
                        || player.getBirthday().after(new Date(32503680000000L))
        )
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        player.setLevel((int) (Math.sqrt((double) 2500 + 200 * player.getExperience()) - 50) / 100);
        player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience());

        return playerRepo.save(player);
    }

    public Player getOne(String id) {
        Long newId;
        try {
            newId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!validId(newId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (playerRepo.existsById(newId)) {
            return playerRepo.findById(newId).get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public Player update(Player playerNew, Player playerOld) {

        if (playerNew.getName() != null) playerOld.setName(playerNew.getName());
        if (playerNew.getTitle() != null) playerOld.setTitle(playerNew.getTitle());
        if (playerNew.getRace() != null) playerOld.setRace(playerNew.getRace());
        if (playerNew.getProfession() != null) playerOld.setProfession(playerNew.getProfession());
        if (playerNew.getExperience() != null) {
            if (isValidExperience(playerNew.getExperience())) {
                playerOld.setExperience(playerNew.getExperience());
            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (playerNew.getBirthday() != null) {
            if (isValidDate(playerNew.getBirthday())) {
                playerOld.setBirthday(playerNew.getBirthday());
            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (playerNew.getBanned() != null) playerOld.setBanned(playerNew.getBanned());

        playerOld.setLevel((int) ((Math.sqrt(2500 + 200 * playerOld.getExperience()) - 50) / 100));
        playerOld.setUntilNextLevel(50 * (playerOld.getLevel() + 1) * (playerOld.getLevel() + 2) - playerOld.getExperience());

        return playerRepo.save(playerOld);
    }


    public void delete(Player player) {
        if(validId(player.getId()))
            playerRepo.delete(player);
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public Boolean validId(Long id) {
        return id > 0;
    }

    private boolean isValidDate(Date date) {
        if (date == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(1999, 11, 31);
        Date after = calendar.getTime();
        calendar.set(3000, 11, 31);
        Date before = calendar.getTime();

        return  (date.before(before) && date.after(after));
    }

    private boolean isValidExperience(Integer experience) {
        return experience >= 0 && experience <= 10000000;
    }
}