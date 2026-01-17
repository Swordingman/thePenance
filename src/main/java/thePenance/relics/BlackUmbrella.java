package thePenance.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import thePenance.PenanceMod;

public class BlackUmbrella extends BaseRelic {
    public static final String ID = PenanceMod.makeID("BlackUmbrella");

    private boolean triggeredThisCombat = false;

    public BlackUmbrella() {
        super(ID, "BlackUmbrella", RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        triggeredThisCombat = false;
        this.grayscale = false;
    }

    // 由 Patch 调用
    public void onBlockBroken(AbstractCreature attacker) {
        if (!triggeredThisCombat) {
            triggeredThisCombat = true;
            this.flash();
            this.grayscale = true; // 变灰表示已触发

            // 给予敌人易伤 (攻击者)
            if (attacker != null) {
                addToBot(new ApplyPowerAction(attacker, AbstractDungeon.player,
                        new VulnerablePower(attacker, 2, false), 1));
            }

            // 给予自己力量
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new StrengthPower(AbstractDungeon.player, 1), 1));

            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        }
    }
}