package thePenance.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import thePenance.relics.BlackUmbrella;
import thePenance.relics.GoldenScales;
import thePenance.relics.LittleGavel;

public class RelicPatches {

    // --- Patch 1: 监控格挡被击碎 (用于 小木槌 和 黑雨伞) ---
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "damage"
    )
    public static class BlockBrokenPatch {
        // 在伤害计算前记录旧格挡值
        public static int preBlock = 0;

        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance, DamageInfo info) {
            preBlock = __instance.currentBlock;
        }

        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance, DamageInfo info) {
            // 如果受到攻击后，格挡变成了0，且原本有格挡 -> 说明被击碎了
            if (__instance.currentBlock == 0 && preBlock > 0) {
                // 计算格挡承受了多少伤害
                // 如果是直接穿透(HP_LOSS)，通常不消耗格挡，这里只处理普通被打破的情况
                // 但 damage() 方法里如果是 HP_LOSS 它是不会动 currentBlock 的，所以这个判断是安全的。

                // 1. 触发 小木槌
                if (__instance.hasRelic(LittleGavel.ID)) {
                    ((LittleGavel) __instance.getRelic(LittleGavel.ID)).onBlockBroken(preBlock);
                }

                // 2. 触发 黑雨伞
                if (__instance.hasRelic(BlackUmbrella.ID)) {
                    ((BlackUmbrella) __instance.getRelic(BlackUmbrella.ID)).onBlockBroken(info.owner);
                }
            }
        }
    }

    // --- Patch 2: 监控卡牌被丢弃 (用于 金天平) ---
    // 无论是手动丢弃还是回合结束自动丢弃，都会走 moveToDiscardPile
    @SpirePatch(
            clz = com.megacrit.cardcrawl.cards.CardGroup.class,
            method = "moveToDiscardPile"
    )
    public static class DiscardMonitorPatch {
        @SpirePrefixPatch
        public static void Prefix(com.megacrit.cardcrawl.cards.CardGroup __instance, AbstractCard c) {
            // 必须是玩家的手牌被丢弃 (防止其他情况干扰，虽说通常只有手牌去弃牌堆)
            // 但 moveToDiscardPile 是 CardGroup 的通用方法，我们需要确定来源。
            // 简单判断：只要是玩家回合内发生的丢弃，我们都记录。

            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(GoldenScales.ID)) {
                if (c.type == AbstractCard.CardType.ATTACK) {
                    ((GoldenScales) AbstractDungeon.player.getRelic(GoldenScales.ID)).onAttackDiscarded();
                }
            }
        }
    }
}