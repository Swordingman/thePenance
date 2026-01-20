package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;

public class JustifiedDefensePower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("JustifiedDefensePower");

    public JustifiedDefensePower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, true, owner, amount);
    }

    @Override
    public void atStartOfTurn() {
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        // 【核心修改点】必须放在最最最前面！
        // 如果层数已经是0或者更小，说明之前的攻击已经耗光了防御，直接略过逻辑
        if (this.amount <= 0) {
            return damageAmount;
        }

        // 下面是正常的判断逻辑
        if (info.type != DamageInfo.DamageType.HP_LOSS && info.owner != null && info.owner != this.owner) {
            this.flash();

            // 1. 先扣层数 (立即生效，防多段)
            this.amount--;

            // 2. 只有扣完这一层，才给奖励
            addToBot(new ApplyPowerAction(owner, owner, new JustifiedDefenseTriggeredPower(owner, 1), 1));

            // 3. 处理移除逻辑和UI更新
            if (this.amount == 0) {
                addToBot(new RemoveSpecificPowerAction(owner, owner, this));
            }

            // 4. 立即更新显示的数字，不然玩家看还得是1
            this.updateDescription();
        }

        return damageAmount;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}