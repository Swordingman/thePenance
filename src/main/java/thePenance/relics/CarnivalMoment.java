package thePenance.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import thePenance.PenanceMod;

public class CarnivalMoment extends BaseRelic {
    public static final String ID = PenanceMod.makeID("CarnivalMoment");

    public CarnivalMoment() {
        // 请确保图片路径正确，或使用内置图片占位
        super(ID, "CarnivalMoment", RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return super.getUpdatedDescription();
    }

    @Override
    public void onEquip() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.hasTag(PenanceMod.CURSE_OF_WOLVES) && c.canUpgrade()) {
                c.upgrade();
            }
        }
    }

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