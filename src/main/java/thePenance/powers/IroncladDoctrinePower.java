package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;
import thePenance.character.PenanceDifficultyHelper;

public class IroncladDoctrinePower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("IroncladDoctrinePower");

    // 这里我们将原本的常量改为“基础增加量”，用于每次叠加时计算
    private static final int BASE_HEAL_AMT = 3;
    private static final int BASE_HP_AMT = 3;

    public IroncladDoctrinePower(AbstractCreature owner, int costAmount) {
        super(POWER_ID, PowerType.BUFF, false, owner, costAmount);

        // 初始化 amount2，根据难度赋予对应的基础恢复量
        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL) {
            this.amount2 = BASE_HP_AMT;
        } else {
            this.amount2 = BASE_HEAL_AMT;
        }

        // 如果你使用的 BasePower 模板支持在 UI 上双数值显示（通常在右上角），请取消下面这行的注释：
        // this.isAmount2 = true;

        updateDescription();
    }

    // 关键点：重写 stackPower 来处理叠加逻辑
    @Override
    public void stackPower(int stackAmount) {
        // 原有逻辑：系统会将新牌的 costAmount (比如 10 或 8) 累加到 this.amount 上
        super.stackPower(stackAmount);

        // 自定义逻辑：每次叠加能力时，按基础值增加我们的恢复量/上限量
        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL) {
            this.amount2 += BASE_HP_AMT;
        } else {
            this.amount2 += BASE_HEAL_AMT;
        }
    }

    @Override
    public void atStartOfTurn() {
        if (owner.hasPower(BarrierPower.POWER_ID)) {
            AbstractPower barrier = owner.getPower(BarrierPower.POWER_ID);

            if (barrier.amount >= this.amount) {
                this.flash();

                // 消耗屏障 (消耗量现在是叠加后的 this.amount)
                addToBot(new ReducePowerAction(owner, owner, BarrierPower.POWER_ID, this.amount));

                // 捕获当前的恢复量，防止在 Action 执行前 amount2 被意外修改
                final int currentEffectAmount = this.amount2;

                addToBot(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                    @Override
                    public void update() {
                        thePenance.patches.PenanceHealPatches.isWhitelistedHeal = true;
                        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL){
                            owner.increaseMaxHp(currentEffectAmount, true);
                        } else {
                            owner.heal(currentEffectAmount);
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
            // Hell 描述: 对应 JSON DESCRIPTIONS[3], [4]
            // 将原来的常量替换为动态的 this.amount2
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[3] + this.amount2 + DESCRIPTIONS[4];
        } else {
            // 普通 描述: 对应 JSON DESCRIPTIONS[0], [1], [2]
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.amount2 + DESCRIPTIONS[2];
        }
    }
}