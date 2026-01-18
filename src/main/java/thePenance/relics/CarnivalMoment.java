package thePenance.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import thePenance.PenanceMod;

public class CarnivalMoment extends BaseRelic { // 如果你有 BaseRelic 类，请继承它
    public static final String ID = PenanceMod.makeID("CarnivalMoment");

    public CarnivalMoment() {
        // 请确保图片路径正确，或使用内置图片占位
        super(ID, "CarnivalMoment", RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return super.getUpdatedDescription();
    }

    /**
     * 当获得遗物时，扫描玩家的主牌组（MasterDeck），
     * 升级所有带有 CURSE_OF_WOLVES 标签的卡牌。
     */
    @Override
    public void onEquip() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            // 检查标签，并且确保卡牌是可以升级的
            if (c.hasTag(PenanceMod.CURSE_OF_WOLVES) && c.canUpgrade()) {
                c.upgrade();
            }
        }
    }

    /**
     * 当玩家获得新卡牌时触发。
     * 如果新卡带有 CURSE_OF_WOLVES 标签，则自动升级。
     */
    @Override
    public void onObtainCard(AbstractCard c) {
        if (c.hasTag(PenanceMod.CURSE_OF_WOLVES) && c.canUpgrade()) {
            c.upgrade();
            // 播放闪烁特效，提示玩家遗物生效了
            this.flash();
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new CarnivalMoment();
    }
}