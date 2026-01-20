package thePenance.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.powers.CodeOfRevengePower;
import thePenance.powers.InTheNameOfTheLawPower;
import thePenance.powers.JudgementPower;
import thePenance.relics.Innocent;
import thePenance.relics.ShopVoucher;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class TriggerJudgementAction extends AbstractGameAction {
    private final AbstractCreature target;

    public TriggerJudgementAction(AbstractCreature target) {
        this.target = target;
        this.actionType = ActionType.DAMAGE;
    }

    @Override
    public void update() {
        AbstractPlayer p = AbstractDungeon.player;

        // 检查玩家是否有裁决能力
        if (p.hasPower(JudgementPower.POWER_ID)) {
            // 1. 获取基础层数
            int baseDamage = p.getPower(JudgementPower.POWER_ID).amount;

            // 2. 【核心修改】先处理力量加成
            // 只有拥有“以法律之名”能力时，才把力量加入基础伤害
            if (p.hasPower(InTheNameOfTheLawPower.POWER_ID)) {
                // 使用官方的 ID 常量，防止拼写错误
                if (p.hasPower(com.megacrit.cardcrawl.powers.StrengthPower.POWER_ID)) {
                    int strengthAmt = p.getPower(com.megacrit.cardcrawl.powers.StrengthPower.POWER_ID).amount;
                    baseDamage += strengthAmt;

                    // 调试日志：如果觉得伤害不对，看下控制台有没有这行字
                    // System.out.println("裁决触发力量加成！当前力量: " + strengthAmt + "，修正后基伤: " + baseDamage);
                }
            }

            // 3. 处理乘区（Relic加成）
            // 注意：Java中 int * float 会隐式转换，这里显式强转一下更安全清楚
            float calculatedDamage = baseDamage;

            if (p.hasRelic(Innocent.ID)) {
                calculatedDamage *= 1.2f;
            }

            // 4. 处理最终固定增伤
            int finalDamage = (int) calculatedDamage;

            if (p.hasRelic(ShopVoucher.ID)) {
                finalDamage += 2;
                p.getRelic(ShopVoucher.ID).flash();
            }

            // 防止伤害变成负数
            if (finalDamage < 0) finalDamage = 0;

            if (finalDamage > 0) {
                p.getPower(JudgementPower.POWER_ID).flash();

                // 造成伤害
                // 注意：DamageType.NORMAL 会再次受到 易伤(Target)、虚弱(Source) 的影响
                // 如果你想让这次伤害“无视虚弱但享受易伤”，计算会更复杂。
                // 现在的写法是标准的“攻击伤害”，会受虚弱影响。
                addToTop(new DamageAction(target,
                        new DamageInfo(p, finalDamage, DamageInfo.DamageType.THORNS),
                        AttackEffect.SLASH_HEAVY));

                if (p.hasPower(CodeOfRevengePower.POWER_ID)) {
                    ((thePenance.powers.CodeOfRevengePower) p.getPower(CodeOfRevengePower.POWER_ID)).onJudgementTriggered();
                }
            }
        }
        this.isDone = true;
    }
}