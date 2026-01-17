package thePenance.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import thePenance.PenanceMod;
import thePenance.cards.*; // 引用所有卡牌

import java.util.ArrayList;

public class ThornboundCodex extends BaseRelic {
    public static final String ID = PenanceMod.makeID("ThornboundCodex");

    public ThornboundCodex() {
        super(ID, "ThornboundCodex", RelicTier.BOSS, LandingSound.HEAVY);
    }

    // 回合开始获得能量
    @Override
    public void atTurnStart() {
        AbstractDungeon.player.gainEnergy(1);
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.energy.energyMaster += 1;
    }

    @Override
    public void onUnequip() {
        AbstractDungeon.player.energy.energyMaster -= 1;
    }

    // 回合结束洗入诅咒
    @Override
    public void onPlayerEndTurn() {
        this.flash();
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));

        // 随机选一张
        AbstractCard curse = getRandomWolfCurse();

        // 塞入牌库顶 (toBottom = false)
        addToBot(new MakeTempCardInDrawPileAction(curse, 1, false, true));
    }

    private AbstractCard getRandomWolfCurse() {
        ArrayList<AbstractCard> curses = new ArrayList<>();
        curses.add(new ContinuousRain());
        curses.add(new FinaleCatastrophe());
        curses.add(new DignityOfTheLeader());
        curses.add(new ArtOfTheHidingFox());
        curses.add(new FameOfTheCrownSlayer());

        return curses.get(AbstractDungeon.cardRandomRng.random(curses.size() - 1));
    }
}