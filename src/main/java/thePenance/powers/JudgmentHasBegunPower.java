package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.PenanceMod;

public class JudgmentHasBegunPower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("JudgmentHasBegunPower");

    // 记录本回合已触发次数
    private int attacksPlayed = 0;
    // 阈值
    private static final int THRESHOLD = 3;
    // 固定收益数值
    private static final int BARRIER_GAIN = 5;
    private static final int STR_JUDGE_GAIN = 1;
    private static final int ENERGY_GAIN = 2;

    public JudgmentHasBegunPower(AbstractCreature owner) {
        super(POWER_ID, PowerType.BUFF, false, owner, -1);
        this.loadRegion("accuracy"); // 借用"旋转"图标
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            this.flash();
            this.attacksPlayed++;

            // 1. 获得 5屏障, 1力量, 1裁决
            addToBot(new ApplyPowerAction(owner, owner, new BarrierPower(owner, BARRIER_GAIN), BARRIER_GAIN));
            addToBot(new ApplyPowerAction(owner, owner, new StrengthPower(owner, STR_JUDGE_GAIN), STR_JUDGE_GAIN));
            addToBot(new ApplyPowerAction(owner, owner, new JudgementPower(owner, STR_JUDGE_GAIN), STR_JUDGE_GAIN));

            // 2. 累计3张 -> 获得能量
            if (this.attacksPlayed == THRESHOLD) {
                addToBot(new GainEnergyAction(ENERGY_GAIN));
                // 如果你想让它循环触发（每3张），可以重置 attacksPlayed = 0;
                // 如果仅限一次（累积使用3张后），就不重置，或者加个标记。
                // 描述是“累积使用3张后”，通常暗示是一次性的阈值奖励。
            }
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        // 回合结束移除
        addToBot(new RemoveSpecificPowerAction(owner, owner, this));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + BARRIER_GAIN + DESCRIPTIONS[1]
                + STR_JUDGE_GAIN + DESCRIPTIONS[2]
                + (Math.max(0, THRESHOLD - attacksPlayed)) + DESCRIPTIONS[3];
    }
}