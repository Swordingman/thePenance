package thePenance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import thePenance.cards.DeadlyEnemy;

@SpirePatch(clz = AbstractPlayer.class, method = "damage")
public class DeadlyEnemyPatch {
    public static void Prefix(AbstractPlayer __instance, DamageInfo info) {
        if (info.type != DamageInfo.DamageType.NORMAL) return;

        int count = 0;
        for (AbstractCard c : __instance.hand.group) {
            if (c instanceof DeadlyEnemy) {
                count++;
            }
        }

        if (count > 0 && info.output > 0) {
            info.output += count;
        }
    }
}