package thePenance.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import thePenance.PenanceMod;
import thePenance.actions.WolfCurseHelper;
import thePenance.cards.*; // 引用所有卡牌

import java.util.ArrayList;

public class ThornboundCodex extends BaseRelic {
    public static final String ID = PenanceMod.makeID("ThornboundCodex");

    public ThornboundCodex() {
        super(ID, "ThornboundCodex", RelicTier.BOSS, LandingSound.HEAVY);
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

        // 直接调用 Helper 统一的方法获取随机狼之诅咒
        AbstractCard curse = WolfCurseHelper.getRandomWolfCurse();

        // 塞入牌库 (根据你原来的参数：toBottom = false, randomSpot = true)
        addToBot(new MakeTempCardInDrawPileAction(curse, 1, false, true));
    }
}