package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;

public class CumulativePrecedentPower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("CumulativePrecedentPower");

    // 新增字段存储每次触发给予的格挡值
    private int blockAmount;

    public CumulativePrecedentPower(AbstractCreature owner, int barrierAmount, int blockAmount) {
        // 父类 amount 存储的是 屏障数值
        super(POWER_ID, PowerType.BUFF, false, owner, barrierAmount);
        this.blockAmount = blockAmount;
        updateDescription();
    }

    // 兼容旧构造函数（如果有其他地方调用）
    public CumulativePrecedentPower(AbstractCreature owner, int amount) {
        this(owner, amount, 0);
    }

    // 供卡牌调用的手动叠加格挡方法
    public void addBlockAmount(int blk) {
        this.blockAmount += blk;
        updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.SKILL) {
            this.flash();

            // 1. 获得屏障 (基于 amount)
            addToBot(new ApplyPowerAction(owner, owner, new BarrierPower(owner, this.amount), this.amount));

            // 2. 获得格挡 (基于 blockAmount)，如果有的话
            if (this.blockAmount > 0) {
                addToBot(new GainBlockAction(owner, owner, this.blockAmount));
            }
        }
    }

    @Override
    public void updateDescription() {
        if (this.blockAmount > 0) {
            // 地狱难度/混合描述：每打出一张技能牌，获得 !M! 点屏障 和 !B! 点格挡。
            // 需要你在 localization JSON 中添加对应的 DESCRIPTIONS[2] 或者动态拼接
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + DESCRIPTIONS[2] + this.blockAmount + DESCRIPTIONS[3];
        } else {
            // 原版描述
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        }
    }
}