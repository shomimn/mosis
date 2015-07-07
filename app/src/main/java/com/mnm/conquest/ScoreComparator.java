package com.mnm.conquest;

import java.util.Comparator;

/**
 * Created by Tomasevic on 7.7.2015.
 */
public class ScoreComparator implements Comparator<PlayerInfo>
{
    @Override
    public int compare(PlayerInfo lhs, PlayerInfo rhs)
    {
        int ll = lhs.getLevel();
        int rl = rhs.getLevel();

        int lk = lhs.getKills();
        int rk = rhs.getKills();

        int ld = lhs.getDeaths();
        int rd = rhs.getDeaths();

        int lscore = ll*(lk-ld);
        int rscore = rl*(rk-rd);

        if(lscore > rscore) return lscore;
        else return rscore;
    }
}
