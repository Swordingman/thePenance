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
            localvars = {"damageAmount"}
    )
    public static void Insert(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount) {
        if (damageAmount[0] <= 0 || info.type == DamageInfo.DamageType.HP_LOSS) {
            return;
        }

        if (__instance.hasPower(BarrierPower.POWER_ID)) {
            BarrierPower barrier = (BarrierPower) __instance.getPower(BarrierPower.POWER_ID);
            int barrierAmount = barrier.amount;
            int damage = damageAmount[0];

            if (barrierAmount > 0) {
                int damageBlockedByBarrier = 0;

                // --- 1. 计算抵扣逻辑 ---
                if (damage >= barrierAmount) {
                    // 【情况A：屏障破碎】
                    damageBlockedByBarrier = barrierAmount;
                    damageAmount[0] = damage - barrierAmount;

                    // 移除屏障
                    AbstractDungeon.actionManager.addToTop(
                            new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(__instance, __instance, barrier)
                    );

                    // 只有屏障彻底破碎才触发的能力放这里
                    if (__instance.hasPower(SilenceWrathPower.POWER_ID)) {
                        SilenceWrathPower wrathPower = (SilenceWrathPower) __instance.getPower(SilenceWrathPower.POWER_ID);
                        wrathPower.onBarrierBroken();
                    }

                } else {
                    // 【情况B：屏障幸存】
                    damageBlockedByBarrier = damage;
                    barrier.amount -= damage;
                    barrier.updateDescription();
                    barrier.flash();

                    damageAmount[0] = 0;
                }

                // --- 2. 统一触发逻辑 (只要屏障受损就触发) ---
                if (damageBlockedByBarrier > 0) {

                    // === 修复点：将苦修移到这里 ===
                    // 无论屏障是碎了还是仅仅掉了层数，只要抵挡了伤害，就算"Damaged"
                    if (__instance.hasPower(AsceticismPower.POWER_ID)) {
                        AsceticismPower asceticismPower = (AsceticismPower) __instance.getPower(AsceticismPower.POWER_ID);
                        asceticismPower.onBarrierDamaged();
                    }

                    // 你的法律守护者如果是受损触发，也应该放这里；如果是破碎触发，就放回上面
                    if(__instance.hasPower(GuardianOfTheLawPower.POWER_ID)) {
                        GuardianOfTheLawPower guardianPower = (GuardianOfTheLawPower) __instance.getPower(GuardianOfTheLawPower.POWER_ID);
                        guardianPower.onBarrierDamaged();
                    }

                    // 裁决机制 (通常要求来源是敌人)
                    if (info.owner != null && info.owner != __instance) {
                        if (__instance.hasPower(JudgementPower.POWER_ID)) {
                            JudgementPower judgement = (JudgementPower) __instance.getPower(JudgementPower.POWER_ID);
                            judgement.onBarrierDamaged(info.owner);
                        }
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