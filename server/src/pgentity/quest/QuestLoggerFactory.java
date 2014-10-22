/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import pgentity.LogPool;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
interface QuestLoggerFactory
{
    public QuestLogger createLogger(LogPool logPool);
}