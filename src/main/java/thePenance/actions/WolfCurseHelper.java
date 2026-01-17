package thePenance.actions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import thePenance.PenanceMod;

import java.util.ArrayList;

public class WolfCurseHelper {

    /**
     * 获取所有带有 CURSE_OF_WOLVES 标签的卡牌列表
     */
    public static ArrayList<AbstractCard> getAllWolfCurses() {
        ArrayList<AbstractCard> list = new ArrayList<>();
        // 遍历游戏内所有注册的卡牌
        for (AbstractCard c : CardLibrary.getAllCards()) {
            // 检查是否有该标签，且不是原始的基础卡（避免拿到未初始化的数据，虽然一般没事）
            if (c.hasTag(PenanceMod.CURSE_OF_WOLVES)) {
                list.add(c.makeCopy());
            }
        }
        return list;
    }

    /**
     * 随机获取一张狼之诅咒
     */
    public static AbstractCard getRandomWolfCurse() {
        ArrayList<AbstractCard> list = getAllWolfCurses();
        if (list.isEmpty()) {
            // 防止崩溃，如果没找到卡返回一个默认诅咒
            return new com.megacrit.cardcrawl.cards.curses.Clumsy();
        }
        return list.get(AbstractDungeon.cardRandomRng.random(list.size() - 1));
    }
}