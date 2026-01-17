package thePenance.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import thePenance.relics.PenanceBasicRelic;

public class PenanceHealPatches {

    // 状态标记
    public static boolean isGainingMaxHp = false;
    public static boolean isActTransition = false;
    public static boolean isWhitelistedHeal = false;

    /**
     * 【新增工具方法】强制进行一次真实回血
     * 在事件代码中调用这个方法，而不是直接 p.heal()
     */
    public static void forceRealHeal(AbstractCreature creature, int amount) {
        isWhitelistedHeal = true; // 1.以此举起白旗
        try {
            creature.heal(amount); // 2.调用原版回血（此时Patch检测到白旗，会放行）
        } finally {
            isWhitelistedHeal = false; // 3.无论是否报错，必须把白旗放下
        }
    }

    // Patch 1: 监控最大生命值的变化 (增加MaxHP时自带的回血量不应该被转化)
    @SpirePatch(clz = AbstractCreature.class, method = "increaseMaxHp")
    public static class MaxHpMonitorPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCreature __instance, int amount, boolean showEffect) {
            if (__instance.isPlayer) isGainingMaxHp = true;
        }
        @SpirePostfixPatch
        public static void Postfix(AbstractCreature __instance, int amount, boolean showEffect) {
            if (__instance.isPlayer) isGainingMaxHp = false;
        }
    }

    // Patch 2: 监控换层回血 (BOSS战后的回满血)
    @SpirePatch(clz = AbstractDungeon.class, method = "dungeonTransitionSetup")
    public static class ActTransitionMonitorPatch {
        @SpirePrefixPatch
        public static void Prefix() { isActTransition = true; }
        @SpirePostfixPatch
        public static void Postfix() { isActTransition = false; }
    }

    // Patch 3: 针对李之华夫饼 (如果不加这个，华夫饼的回血会变成巨额护盾)
    @SpirePatch(clz = com.megacrit.cardcrawl.relics.Waffle.class, method = "onEquip")
    public static class LeesWaffleWhitelistPatch {
        @SpirePrefixPatch
        public static void Prefix(com.megacrit.cardcrawl.relics.Waffle __instance) {
            PenanceHealPatches.isWhitelistedHeal = true;
        }
        @SpirePostfixPatch
        public static void Postfix(com.megacrit.cardcrawl.relics.Waffle __instance) {
            PenanceHealPatches.isWhitelistedHeal = false;
        }
    }

    // Patch 4: 核心拦截逻辑
    @SpirePatch(clz = AbstractCreature.class, method = "heal", paramtypez = {int.class, boolean.class})
    public static class HealBlockPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractCreature __instance, int amount, boolean showEffect) {
            // 1. 如果不是玩家，或者没带核心遗物，不管
            if (!__instance.isPlayer || !AbstractDungeon.player.hasRelic(PenanceBasicRelic.ID)) {
                return SpireReturn.Continue();
            }

            // 2. 获取遗物实例
            AbstractPlayer p = AbstractDungeon.player;
            PenanceBasicRelic relic = (PenanceBasicRelic) p.getRelic(PenanceBasicRelic.ID);

            // 3. 【放行清单】
            // 情况A: 正在增加最大生命值 (系统自带的补血)
            if (PenanceHealPatches.isGainingMaxHp) return SpireReturn.Continue();

            // 情况B: 换层 (BOSS战后恢复)
            if (PenanceHealPatches.isActTransition) return SpireReturn.Continue();

            // 情况C: 强制放行 (事件、特殊剧情、华夫饼)
            if (PenanceHealPatches.isWhitelistedHeal) return SpireReturn.Continue();

            // 情况D: 濒死/复活 (如只有1血时触发蜥蜴尾巴，不应该被拦截)
            if (p.currentHealth <= 0 || p.isDying) return SpireReturn.Continue();

            // 情况E: 无效数值
            if (amount <= 0) return SpireReturn.Continue();

            // --- 拦截生效 ---

            // 调用遗物的特殊效果 (转为屏障/临时生命/格挡等)
            relic.onTriggerHealing(amount);

            // 阻止原版回血
            return SpireReturn.Return();
        }
    }
}