package thePenance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class HpLostTracker {
    // 静态变量存储当前战斗损失血量
    public static int hpLostThisCombat = 0;

    // 1. 战斗开始时重置
    @SpirePatch(clz = AbstractPlayer.class, method = "preBattlePrep")
    public static class ResetTracker {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance) {
            hpLostThisCombat = 0;
        }
    }

    // 2. 监控所有掉血 (包括被攻击和自残)
    // 所有的扣血逻辑最终都会走 AbstractPlayer.damage()
    @SpirePatch(clz = AbstractPlayer.class, method = "damage")
    public static class DamageTracker {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance, DamageInfo info) {
            // lastDamageTaken 记录了经过格挡计算后的实际掉血量
            // 无论是被怪打，还是卡牌自残(HP_LOSS)，这个值在 Postfix 时都是准确的
            if (__instance.lastDamageTaken > 0) {
                hpLostThisCombat += __instance.lastDamageTaken;
            }
        }
    }

    // 标记：当前是否正在执行 LoseHPAction
    public static boolean isLoseHpActionRunning = false;

    // Patch: 监控 LoseHPAction 的开始和结束
    @SpirePatch(
            clz = LoseHPAction.class,
            method = "update"
    )
    public static class MonitorLoseHpAction {
        @SpirePrefixPatch
        public static void Prefix(LoseHPAction __instance) {
            // 只有当来源是玩家自己（self-inflicted）时，才标记为 true
            // 这样可以排除敌人造成的 HP Loss (如果有的话)
            if (__instance.source == AbstractDungeon.player) {
                isLoseHpActionRunning = true;
            }
        }

        @SpirePostfixPatch
        public static void Postfix(LoseHPAction __instance) {
            isLoseHpActionRunning = false;
        }
    }
}