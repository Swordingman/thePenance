package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;

public class JustifiedDefensePower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("JustifiedDefensePower");

    public JustifiedDefensePower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
        this.loadRegion("buffer"); // 暂时借用缓冲图标
    }

    // 受到攻击时触发
    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        // 必须是受到攻击（非HP Loss），且来源不是自己
        if (info.type != DamageInfo.DamageType.HP_LOSS && info.owner != null && info.owner != this.owner) {
            this.flash();

            // 给予一个隐藏的Buff，用于下回合生效
            // 我们通过堆叠这个隐藏Buff来记录下回合该给多少奖励
            addToBot(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(owner, owner,
                    new JustifiedDefenseTriggeredPower(owner, 1), 1));

            // 减少层数
            addToBot(new com.megacrit.cardcrawl.actions.common.ReducePowerAction(owner, owner, this, 1));
        }
        return damageAmount;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}