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

public class TriggerJudgementAction extends AbstractGameAction {
    private final AbstractCreature target;

    public TriggerJudgementAction(AbstractCreature target) {
        this.target = target;
        this.actionType = ActionType.DAMAGE;
    }

    @Override
    public void update() {
        // 获取玩家
        AbstractPlayer p = AbstractDungeon.player;

        // 检查玩家是否有裁决能力
        if (p.hasPower(JudgementPower.POWER_ID)) {
            // 获取当前裁决层数
            int amt = p.getPower(JudgementPower.POWER_ID).amount;

            int damageAmount = amt;

            if (p.hasRelic(Innocent.ID)) { damageAmount *= 1.2f; }

            if (p.hasRelic(ShopVoucher.ID)) {
                damageAmount += 2;
                p.getRelic(ShopVoucher.ID).flash();
            }

            if (damageAmount > 0) {
                // 闪烁一下裁决图标，提示效果触发
                p.getPower(JudgementPower.POWER_ID).flash();

                if (p.hasPower(InTheNameOfTheLawPower.POWER_ID)) {
                    AbstractPower strength = p.getPower("Strength");
                    if (strength != null) {
                        damageAmount += strength.amount;
                    }
                }

                if (damageAmount < 0) damageAmount = 0;

                // 造成伤害
                addToTop(new DamageAction(target,
                        new DamageInfo(p, damageAmount, DamageInfo.DamageType.NORMAL),
                        AttackEffect.SLASH_HEAVY)); // 使用重击特效，符合“行刑”的感觉

                if (p.hasPower(CodeOfRevengePower.POWER_ID)) {
                    thePenance.powers.CodeOfRevengePower power =
                            (thePenance.powers.CodeOfRevengePower) p.getPower(CodeOfRevengePower.POWER_ID);
                    power.onJudgementTriggered();
                }
            }
        }

        this.isDone = true;
    }
}