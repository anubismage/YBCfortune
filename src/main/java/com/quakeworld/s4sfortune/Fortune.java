package com.quakeworld.s4sfortune;

import org.bukkit.entity.Player;

interface FortuneSideEffect {
    void run(Player player);
}

public class Fortune {
    public String color;
    public String fortune;
    public FortuneSideEffect sideEffect;

    public Fortune(String color, String fortune, FortuneSideEffect sideEffect) {
        this.color = color;
        this.fortune = fortune;
        this.sideEffect = sideEffect;
    }

    public Fortune(String color, String fortune) {
        this.color = color;
        this.fortune = fortune;
        this.sideEffect = (p) -> {};
    }
}
