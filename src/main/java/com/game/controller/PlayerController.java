package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class PlayerController {

    private final UserService userService;

    @Autowired
    public PlayerController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/players")
    public List<Player> getAll(@RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "title", required = false) String title,
                                       @RequestParam(value = "race", required = false) Race race,
                                       @RequestParam(value = "profession", required = false)Profession profession,
                                       @RequestParam(value = "after", required = false) Long after,
                                       @RequestParam(value = "before", required = false) Long before,
                                       @RequestParam(value = "banned", required = false)Boolean banned,
                                       @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                       @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                       @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                       @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                       @RequestParam(defaultValue = "ID", value = "order") PlayerOrder order,
                                       @RequestParam(defaultValue = "0", value = "pageNumber") Integer pageNumber,
                                       @RequestParam(defaultValue = "3", value = "pageSize") Integer pageSize
    ) {
        List<Player> players = userService.getAll(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
        return userService.getSortedPlayers(players, pageNumber, pageSize, order);
    }

    @GetMapping("/players/count")
    public Integer getAll(@RequestParam(value="name", required = false) String name,
                                   @RequestParam(value = "title", required = false) String title,
                                   @RequestParam(value = "race", required = false) Race race,
                                   @RequestParam(value = "profession", required = false) Profession profession,
                                   @RequestParam(value = "after", required = false) Long after,
                                   @RequestParam(value = "before", required = false) Long before,
                                   @RequestParam(value = "banned", required = false) Boolean banned,
                                   @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                   @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                   @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                   @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {
        List<Player> players = userService.getAll(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
        return players.size();
    }

    @PostMapping("/players")
    public Player create(@RequestBody Player player){
        return userService.create(player);
    }

    @GetMapping("/players/{id}")
    public Player getOne(@PathVariable(value = "id") String id){
        return userService.getOne(id);
    }

    @PostMapping("/players/{id}")
    public Player update(
            @PathVariable (value = "id") String id,
            @RequestBody Player player){

        if (player.getName() == null && player.getTitle() == null && player.getProfession() == null && player.getRace() == null && player.getBirthday() == null && player.getExperience() == null)
            return userService.getOne(id);

        return userService.update(player, userService.getOne(id));
    }

    @DeleteMapping("/players/{id}")
    public void delete(@PathVariable(value = "id")String id) {
        userService.delete(userService.getOne(id));
    }
}