package thePenance.relics;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.PenanceMod;

public class PlasticMole extends BaseRelic {
    public static final String ID = PenanceMod.makeID("PlasticMole");

    // 阈值
    private static final int THRESHOLD = 3;

    public PlasticMole() {
        super(ID, "PlasticMole", RelicTier.UNCOMMON, LandingSound.CLINK);
        this.counter = 0;
    }

    @Override
    public void atTurnStart() {
        int attackIntents = 0;

        // 遍历所有活着的敌人
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m != null && !m.isDeadOrEscaped()) {
                // 判断是否是攻击意图 (Damage > -1)
                if (m.getIntentBaseDmg() >= 0) {
                    attackIntents++;
                }
            }
        }

        if (attackIntents > 0) {
            this.counter += attackIntents;
            this.flash();

            // 检查是否达到阈值
            if (this.counter >= THRESHOLD) {
                int energyGain = this.counter / THRESHOLD;
                this.counter %= THRESHOLD; // 保留余数

                addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                addToBot(new GainEnergyAction(energyGain));
            }
        }
    }

    @Override
    public void onVictory() {
        this.counter = 0;
    }

    @Override
    public String getUpdatedDescription() {
        // 如果是图鉴状态(-1)，只返回第一句功能介绍
        if (this.counter == -1) {
            return DESCRIPTIONS[0];
        }
        // 如果是游戏状态，加上计数文本
        return DESCRIPTIONS[0] + DESCRIPTIONS[1] + this.counter + DESCRIPTIONS[2];
    }
}