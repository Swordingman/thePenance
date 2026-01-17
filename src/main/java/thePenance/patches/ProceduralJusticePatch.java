package thePenance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import thePenance.powers.BarrierPower;
import thePenance.powers.ProceduralJusticePower;

@SpirePatch(
        clz = AbstractCreature.class,
        method = "addBlock"
)
public class ProceduralJusticePatch {
    @SpirePostfixPatch
    public static void Postfix(AbstractCreature __instance, int blockAmount) {
        // 必须是玩家，且格挡量大于0
        if (__instance.isPlayer && blockAmount > 0) {
            // 检查是否有程序正义能力
            if (__instance.hasPower(ProceduralJusticePower.POWER_ID)) {
                __instance.getPower(ProceduralJusticePower.POWER_ID).flash();

                // 获得等量的屏障
                AbstractDungeon.actionManager.addToBottom(
                        new ApplyPowerAction(__instance, __instance,
                                new BarrierPower(__instance, blockAmount), blockAmount)
                );
            }
        }
    }
}