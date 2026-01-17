package thePenance.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;
import thePenance.powers.BarrierPower;

public class PurgeAction extends AbstractGameAction {
    public int[] multiDamage;
    private boolean firstFrame = true;

    public PurgeAction(AbstractCreature source, int[] multiDamage, DamageInfo.DamageType damageType) {
        this.source = source;
        this.multiDamage = multiDamage;
        this.damageType = damageType;
        this.actionType = ActionType.DAMAGE;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (this.firstFrame) {
            // 播放音效和特效 (横扫特效)
            addToBot(new SFXAction("ATTACK_HEAVY"));
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractDungeon.effectList.add(new CleaveEffect());
                    this.isDone = true;
                }
            });

            this.firstFrame = false;
        }

        // 等待特效播放一半再结算伤害，更有打击感
        this.tickDuration();

        if (this.isDone) {
            int totalBarrierGain = 0;

            // 遍历房间内的所有怪物
            for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); ++i) {
                AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);

                // 只对没死且没逃跑的怪物造成伤害
                if (!m.isDeadOrEscaped()) {
                    // 造成伤害
                    m.damage(new DamageInfo(this.source, this.multiDamage[i], this.damageType));

                    // 关键点：读取怪物刚才实际受到的伤害 (lastDamageTaken)
                    // 如果怪物有格挡，这个数值会减去格挡；如果怪物无敌，这个数值是0
                    if (m.lastDamageTaken > 0) {
                        totalBarrierGain += m.lastDamageTaken;
                    }
                }
            }

            // 如果造成了伤害，给予玩家屏障
            if (totalBarrierGain > 0) {
                addToTop(new ApplyPowerAction(this.source, this.source,
                        new BarrierPower(this.source, totalBarrierGain), totalBarrierGain));
            }
        }
    }
}