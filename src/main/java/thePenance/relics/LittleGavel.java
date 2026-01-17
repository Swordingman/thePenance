package thePenance.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import thePenance.PenanceMod;
import thePenance.powers.BarrierPower;

public class LittleGavel extends BaseRelic {
    public static final String ID = PenanceMod.makeID("LittleGavel");

    public LittleGavel() {
        super(ID, "LittleGavel", RelicTier.RARE, LandingSound.SOLID);
    }

    // 由 Patch 调用
    public void onBlockBroken(int blockAmountLost) {
        this.flash();
        // 获得等同于被击碎前格挡值的屏障
        // (因为格挡归零了，说明它完全承受了这部分伤害)
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new BarrierPower(AbstractDungeon.player, blockAmountLost), blockAmountLost));
    }
}