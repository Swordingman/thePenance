package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;
import thePenance.character.PenanceDifficultyHelper;

public class IroncladDoctrinePower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("IroncladDoctrinePower");

    // 我们用 amount 来存储 "需要消耗的屏障量" (10/8)
    // 恢复量固定为 3，如果需要升级可变，可以引入 amount2 或者 magic number
    private static final int HEAL_AMT = 3;
    private static final int HP_AMT = 3;

    public IroncladDoctrinePower(AbstractCreature owner, int costAmount) {
        super(POWER_ID, PowerType.BUFF, false, owner, costAmount);
    }

    @Override
    public void atStartOfTurn() {
        // 检查是否有屏障
        if (owner.hasPower(BarrierPower.POWER_ID)) {
            AbstractPower barrier = owner.getPower(BarrierPower.POWER_ID);
            // 检查屏障是否足够
            if (barrier.amount >= this.amount) {
                this.flash();
                // 消耗屏障
                addToBot(new ReducePowerAction(owner, owner, BarrierPower.POWER_ID, this.amount));
                // 恢复生命 (注意：这会通过我们之前的白名单Patch被允许吗？)
                // 之前写的 Adjourn 是手动绕过。这里如果是自动回血，
                // 我们需要在 HealAction 执行前打开开关，或者使用特制的 Action。
                // 为了简单，我们直接用自定义 Action 瞬间回血。
                addToBot(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                    @Override
                    public void update() {
                        thePenance.patches.PenanceHealPatches.isWhitelistedHeal = true;
                        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL){
                            owner.increaseMaxHp(HP_AMT, true);
                        } else {
                            owner.heal(HEAL_AMT);
                        }
                        thePenance.patches.PenanceHealPatches.isWhitelistedHeal = false;
                        this.isDone = true;
                    }
                });
            }
        }
    }

    @Override
    public void updateDescription() {
        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL) {
            // Hell 描述: 回合开始时，消耗 X 屏障，获得 1 点最大生命。
            // 对应 JSON DESCRIPTIONS[3], [4]
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[3] + HP_AMT + DESCRIPTIONS[4];
        } else {
            // 普通 描述: 回合开始时，消耗 X 屏障，恢复 3 点生命。
            // 对应 JSON DESCRIPTIONS[0], [1], [2]
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + HEAL_AMT + DESCRIPTIONS[2];
        }
    }
}