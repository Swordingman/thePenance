package thePenance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;
import thePenance.relics.LetterOfGratitude;

public class BarrierPower extends BasePower implements CloneablePowerInterface {
    public static final String POWER_ID = PenanceMod.makeID("BarrierPower");
    // 不需要再手动定义 PowerStrings, NAME, DESCRIPTIONS，BasePower 已经处理了

    public BarrierPower(AbstractCreature owner, int amount) {
        super(
                POWER_ID,          // ID
                PowerType.BUFF,    // 类型
                false,             // isTurnBased (屏障通常不是回合制的，除非回合结束自动消失)
                owner,             // 拥有者
                null,              // 来源 (source)，这里设为 null
                amount,            // 层数
                true,              // initDescription (是否立即初始化描述)
                false              // loadImage -> 【关键点】设为 false，禁止 BasePower 自动找图
        );

        this.canGoNegative = false; // 屏障不能为负数

        // 因为上面 loadImage 设为了 false，我们在这里手动加载原版图标
        this.loadRegion("buffer");
    }

    // BasePower 继承自 AbstractPower，所以 stackPower 逻辑保留即可
    // 你的原版代码里加了 updateDescription()，这很好，保留它
    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;

         if (owner.isPlayer && ((AbstractPlayer) owner).hasRelic(LetterOfGratitude.ID)) {
             stackAmount += Math.max(1, (int)(stackAmount * 0.2f));
         }

        this.amount += stackAmount;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        // 直接使用父类 BasePower 提供的 DESCRIPTIONS 数组
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public AbstractPower makeCopy() {
        return new BarrierPower(owner, amount);
    }
}