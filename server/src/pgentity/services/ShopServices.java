/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.services;

import config.CFIBShop;
import config.PGConfig;
import pgentity.Penguin;
import pgentity.PenguinList;
import pgentity.Penguindex;
import pgentity.User;
import pgentity.quest.QuestLogger;
import share.PGError;
import db.PGKeys;
import share.PGException;

/**
 *
 * @author Mrkupi
 */
public class ShopServices
{
    private static class PGShopItem
    {
        private final String itemID;
        private final int itemCount;
        
        public PGShopItem( String shopitemID, int count )
        {
            this.itemID =   shopitemID;
            this.itemCount  =   count;
        }
        
        public Boolean isValid()
        {
            return PGConfig.inst().getShopItems().containsKey(this.getItemID());
        }

        /**
         * @return the itemID
         */
        public String getItemID() {
            return itemID;
        }

        /**
         * @return the itemCount
         */
        public int getItemCount() {
            return itemCount;
        }
    }
    
    private ShopServices()
    {
        super();
    }

    private static final ShopServices inst = new ShopServices();

    public static ShopServices inst() {
        return inst;
    }
    
    /**
     *
     * @param timeNow
     * @param user
     * @param cote
     * @param itemId
     * @param nPeng
     * @return
     * @throws PGException
     */
    public String[] buyPenguins(long timeNow, final User user,
            QuestLogger userQLogger, final Penguindex penguindex,
            final PenguinList penguinList, String itemId, int nPeng) throws PGException
    {
        final String uid = user.getUid();
        final String coteID = penguinList.getCoteID();
        PGShopItem item = new PGShopItem(itemId, nPeng);
        PGException.Assert(item.isValid(),
                PGError.INVALID_ITEM, "Conf item was not found");
        
        config.CFIBShop.Item shopItem =
                PGConfig.inst().getShopItems().get(item.getItemID());
        
        PGException.Assert("penguin".equals(shopItem.getKind()),
                PGError.ITEM_NOT_PENGUIN, "Item wasn't penguin");
        assertItemRequirements(shopItem, item.getItemCount(), user);
        
        config.CFIBShop.PenguinShopItem penguinItem = (config.CFIBShop.PenguinShopItem) shopItem;
        
        String[] pengIDs = new String[item.getItemCount()];
        for( int i = 0; i < item.getItemCount(); ++i )
        {
            String pengId = PGKeys.randomKey();
            Penguin penguinEntity = Penguin.newPenguin(uid, coteID, pengId,
                    penguinItem.getEntity(), PGConfig.inst().temp().PenguinDefaultLevel(),
                    timeNow);

            pengIDs[i] = pengId;
        }
        penguindex.add(penguinItem.getEntity());
        penguinList.add(pengIDs);
        
        // Update user data
        int goldPrice = shopItem.getPrice().getGold() * item.getItemCount();
        if (goldPrice > 0)
        {
            UserServices.inst().decreaseGold(user, userQLogger, goldPrice);
        }
        
        int coinPrice = shopItem.getPrice().getCoin() * item.getItemCount();
        if (coinPrice > 0)
        {
            UserServices.inst().decreaseCoin(user, coinPrice);
        }
        
        return pengIDs;
    }
    
    public Boolean buyFish(final User user, QuestLogger userQLogger,
            String itemId, int nItem) throws PGException
    {
        PGShopItem item = new PGShopItem(itemId, nItem);
        PGException.Assert(item.isValid(),
                PGError.INVALID_ITEM, "Conf item was not found");
        
        config.CFIBShop.Item shopItem =
                PGConfig.inst().getShopItems().get(item.getItemID());
        
        PGException.Assert("fish".equals(shopItem.getKind()), 
                PGError.ITEM_NOT_FISH, "Item wasn't fish");
        assertItemRequirements(shopItem, item.getItemCount(), user);
        
        try
        {
            CFIBShop.FishShopItem fishItem = (CFIBShop.FishShopItem) shopItem;
            int userFish = user.getFish() + fishItem.getnFish();
            user.setFish(userFish);
            
            // Update user data
            int goldPrice = shopItem.getPrice().getGold() * item.getItemCount();
            if (goldPrice > 0)
            {
                UserServices.inst().decreaseGold(user, userQLogger, goldPrice);
            }

            int coinPrice = shopItem.getPrice().getCoin() * item.getItemCount();
            if (coinPrice > 0)
            {
                UserServices.inst().decreaseCoin(user, coinPrice);
            }
        } catch(ClassCastException e)
        {
            throw new PGException(PGError.ITEM_NOT_FISH, "Fish config match item was not found");
        }
        
        return true;
    }
    
    public Boolean buyGold(final User user, String itemId) throws PGException
    {
        PGShopItem item = new PGShopItem(itemId, 1);
        PGException.Assert(item.isValid(),
                PGError.INVALID_ITEM, "Conf item was not found");
        
        config.CFIBShop.Item shopItem =
                PGConfig.inst().getShopItems().get(item.getItemID());
        
        PGException.Assert("gold".equals(shopItem.getKind()), 
                PGError.ITEM_NOT_GOLD, "Item wasn't gold");
        assertItemRequirements(shopItem, item.getItemCount(), user);
        
        try
        {
            CFIBShop.GoldShopItem goldItem = (CFIBShop.GoldShopItem) shopItem;
            UserServices.inst().increaseGold(user, goldItem.getnGold());
            
            // update
            int coinPrice = shopItem.getPrice().getCoin() * item.getItemCount();
            UserServices.inst().decreaseCoin(user, coinPrice);
        } catch(ClassCastException e)
        {
            throw new PGException(PGError.ITEM_NOT_GOLD, "Gold config match item was not found");
        }
        
        return true;
    }
    
    private void assertItemRequirements(CFIBShop.Item item, int nItems, User user) throws PGException
    {
        PGException.Assert(item.getPrice().getCoin() * nItems <= user.getCoin(),
                PGError.NOT_ENOUGH_COIN, "Not enough coin (" + user.getCoin() +
                        "/" + item.getPrice().getCoin() * nItems + ")");
        PGException.Assert(item.getPrice().getGold()* nItems <= user.getGold(),
                PGError.NOT_ENOUGH_GOLD, "Not enough gold (" + user.getGold() +
                        "/" + item.getPrice().getGold() * nItems + ")");
        PGException.Assert(item.getUnlockRequire().getLevel() <= user.getLevel(),
                PGError.NOT_ENOUGH_LEVEL, "Not enough level (" + user.getLevel() +
                        "/" + item.getUnlockRequire().getLevel() + ")");
    }
}