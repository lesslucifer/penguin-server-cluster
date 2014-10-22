/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services;

import config.PGConfig;
import config.CFUpgradeRequirement;
import pgentity.BoxEgg;
import pgentity.User;
import pgentity.quest.QuestLogger;
import share.PGError;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class BoxEggServices
{
    private BoxEggServices()
    {
        super();
    }
    
    private static final BoxEggServices inst = new BoxEggServices();
    
    public static BoxEggServices inst()
    {
        return inst;
    }
    
    public void upgradeBoxEgg(final User user, final QuestLogger userQLogger,
            final BoxEgg boxegg) throws PGException
    {
        int nextLevel = boxegg.getLevel() + 1;
        PGException.Assert(PGConfig.inst().getBoxEgg().containsKey(nextLevel),
                PGError.OUT_OF_BOXEGG_LEVEL,
                "End of config boxegg level");
        
        CFUpgradeRequirement upgradeReq = PGConfig.inst().getBoxEgg().get(nextLevel).getUpgradeRequire();
        // Check require upgrade
        PGException.Assert(upgradeReq.getCoin() <= user.getCoin(),
                PGError.NOT_ENOUGH_COIN, "Not enough coin");
        PGException.Assert(upgradeReq.getGold() <= user.getGold(),
                PGError.NOT_ENOUGH_GOLD, "Not enough gold");
        PGException.Assert(upgradeReq.getLevel() <= user.getLevel(),
                PGError.NOT_ENOUGH_LEVEL, "Not enough level");
        
        // Success, now can upgrade to next level
        boxegg.setLevel(nextLevel);
        
        // Update user data
        if (upgradeReq.getCoin() > 0)
        {
            UserServices.inst().decreaseCoin(user, nextLevel);
        }
        
        if (upgradeReq.getGold() > 0)
        {
            UserServices.inst().decreaseGold(user, userQLogger, upgradeReq.getGold());
        }
    }
}