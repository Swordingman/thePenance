package thePenance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;

public class CodeOfRevengePower extends BasePower implements CloneablePowerInterface {
    public static final String POWER_ID = PenanceMod.makeID("CodeOfRevengePower");
    // 移除了 PowerStrings, NAME, DESCRIPTIONS 的静态定义，由 BasePower 接管

    public CodeOfRevengePower(AbstractCreature owner, int amount) {
        super(
                POWER_ID,          // ID
                PowerType.BUFF,    // 类型
                false,             // isTurnBased (这种被动触发的能力通常不算回合制)
                owner,             // 拥有者
                null,              // source
                amount,            // 这里的 amount 是每次获得的屏障数
                true,              // initDescription
                false              // loadImage -> 设为 false，因为你要借用原版图标
        );

        // 暂时借用原版“进化”的图标
        this.loadRegion("evolve");
    }

    // 外部调用的接口保持不变
    public void onJudgementTriggered() {
        this.flash();
        addToBot(new ApplyPowerAction(this.owner, this.owner,
                new BarrierPower(this.owner, this.amount), this.amount));
    }

    @Override
    public void updateDescription() {
        // 使用父类继承下来的 DESCRIPTIONS
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public AbstractPower makeCopy() {
        return new CodeOfRevengePower(owner, amount);
    }
}