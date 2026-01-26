package thePenance.cards;

import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.actions.WolfCurseHelper;
import thePenance.character.Penance;
import thePenance.util.CardStats;

import java.util.ArrayList;

public class SyracusanWolves extends BaseCard {

    public static final String ID = makeID("SyracusanWolves");

    private static final int COST = 1;

    public SyracusanWolves() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.RARE,
                CardTarget.SELF,
                COST
        ));
        setExhaust(true);

        setCarousel(WolfCurseHelper.getAllWolfCurses());
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int amount = 5;

        for (int i = 0; i < amount; i++) {
            AbstractCard c = WolfCurseHelper.getRandomWolfCurse();
            if (this.upgraded) {
                c.upgrade();
            }
            addToBot(new MakeTempCardInDrawPileAction(c, 1, true, true));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
           upgradeName();
            upgradeCarousel();
        }
    }
}