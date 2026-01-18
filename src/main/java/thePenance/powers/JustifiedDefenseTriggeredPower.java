package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import thePenance.PenanceMod;

public class JustifiedDefenseTriggeredPower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("JustifiedDefenseTriggeredPower");

    public JustifiedDefenseTriggeredPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
        this.name = "正当防卫(已触发)"; // 玩家可见，提示下回合有收益
    }

    @Override
    public void atStartOfTurn() {
        this.flash();
        // 每一层提供 1能量 1抽牌
        addToBot(new GainEnergyAction(this.amount));
        addToBot(new DrawCardAction(this.amount));

        // 移除自己
        addToBot(new RemoveSpecificPowerAction(owner, owner, this));
    }
}