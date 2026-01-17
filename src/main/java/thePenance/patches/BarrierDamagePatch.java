package thePenance.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;
import thePenance.powers.*;
import thePenance.powers.GuardianOfTheLawPower;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "damage"
)
public class BarrierDamagePatch {

    @SpireInsertPatch(
            locator = Locator.class,
            localvars = {"damageAmount"} // 获取当前的伤害值（此时格挡已经抵消过了）
    )
    public static void Insert(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount) {
        // 如果伤害无效，或者不是来自敌人的攻击（比如自残），根据需求可能不需要触发屏障
        // 这里假设只要是 HP 伤害，屏障都能挡
        if (damageAmount[0] <= 0 || info.type == DamageInfo.DamageType.HP_LOSS) {
            return;
        }

        // 检查玩家是否有屏障
        if (__instance.hasPower(BarrierPower.POWER_ID)) {
            BarrierPower barrier = (BarrierPower) __instance.getPower(BarrierPower.POWER_ID);
            int barrierAmount = barrier.amount;
            int damage = damageAmount[0];

            if (barrierAmount > 0) {
                int damageBlockedByBarrier = 0;

                if (damage >= barrierAmount) {
                    // 伤害超过屏障：屏障全碎，扣除部分伤害
                    damageBlockedByBarrier = barrierAmount;
                    damageAmount[0] = damage - barrierAmount; // 修改传入的伤害值，剩下的去扣血

                    // 移除屏障能力
                    AbstractDungeon.actionManager.addToTop(
                            new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(__instance, __instance, barrier)
                    );

                    if (__instance.hasPower(SilenceWrathPower.POWER_ID)) {
                        SilenceWrathPower wrathPower = (SilenceWrathPower) __instance.getPower(SilenceWrathPower.POWER_ID);
                        wrathPower.onBarrierBroken();
                    }

                    if(__instance.hasPower(GuardianOfTheLawPower.POWER_ID)) {
                        GuardianOfTheLawPower guardianPower = (GuardianOfTheLawPower) __instance.getPower(GuardianOfTheLawPower.POWER_ID);
                        guardianPower.onBarrierDamaged();
                    }

                     if (__instance.hasPower(AsceticismPower.POWER_ID)) {
                         AsceticismPower asceticismPower = (AsceticismPower) __instance.getPower(AsceticismPower.POWER_ID);
                         asceticismPower.onBarrierDamaged();
                     }
                } else {
                    // 屏障足够抵挡伤害
                    damageBlockedByBarrier = damage;
                    barrier.amount -= damage; // 减少屏障数值
                    barrier.updateDescription(); // 更新文本
                    barrier.flash(); // 闪烁特效

                    damageAmount[0] = 0; // 伤害被完全吸收，血量不受损
                }

                // --- 触发裁决机制 ---
                // 如果屏障确实吸收了伤害，且伤害来源是敌人
                if (damageBlockedByBarrier > 0 && info.owner != null && info.owner != __instance) {
                    if (__instance.hasPower(JudgementPower.POWER_ID)) {
                        JudgementPower judgement = (JudgementPower) __instance.getPower(JudgementPower.POWER_ID);
                        judgement.onBarrierDamaged(info.owner);
                    }
                }
            }
        }
    }

    // 定位器：找到代码中“伤害值已经被格挡抵消后，准备扣减HP之前”的位置
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            // 在 AbstractPlayer.damage 方法中，寻找 "this.currentHealth -= damageAmount;" 这行代码之前
            return LineFinder.findInOrder(ctMethodToPatch,
                    new Matcher.FieldAccessMatcher(AbstractPlayer.class, "currentHealth"));
        }
    }
}