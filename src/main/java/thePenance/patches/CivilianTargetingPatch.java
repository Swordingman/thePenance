package thePenance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.monsters.VolsiniiCivilian;

@SpirePatch(
        clz = AbstractCard.class,
        method = "canUse"
)
public class CivilianTargetingPatch {
    // Prefix 补丁会在原方法执行前运行
    public static SpireReturn<Boolean> Prefix(AbstractCard __instance, AbstractPlayer p, AbstractMonster m) {
        // 判断1: 目标是否是我们的市民
        // 判断2: 卡牌是否是指向性攻击 (CardTarget.ENEMY 或 CardTarget.SELF_AND_ENEMY)
        if (m instanceof VolsiniiCivilian && (__instance.target == AbstractCard.CardTarget.ENEMY || __instance.target == AbstractCard.CardTarget.SELF_AND_ENEMY)) {

            // 设置屏幕下方的红色警告字样
            __instance.cantUseMessage = "我不能伤害无辜的人！";

            // 返回 false，表示卡牌不可用
            return SpireReturn.Return(false);
        }

        // 否则继续执行原版逻辑
        return SpireReturn.Continue();
    }
}