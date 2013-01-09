package uk.co.tkce.engine;

import uk.co.tkce.toolkit.types.Player;

import java.util.ArrayList;

/**
 * Manges the players party.
 */
public class Party
{
    private ArrayList<Player> party;

    public Party(Player mainCharacter)
    {
        party = new ArrayList<>();

        party.add(mainCharacter);
    }

    public void removePlayer(Player player)
    {
        if (party.indexOf(player) != 0)
        {
            party.remove(player);
        }
    }

    public void addPlayer(Player player)
    {
        party.add(player);
    }

    public Player getPlayer(int id)
    {
        // ID 0 is always the party leader.
        return getPlayer(0);
    }
}
