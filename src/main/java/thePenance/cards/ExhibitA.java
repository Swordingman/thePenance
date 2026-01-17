package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.curses.Doubt;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.util.CardStats;

public class ExhibitA extends BaseCard {
    public static final String ID = makeID("ExhibitA");
    public ExhibitA() {
        super(ID, new CardStats(
                CardColor.COLORLESS, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF, 0
        ));
        setExhaust(true);
    }
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new MakeTempCardInHandAction(new Doubt()));
        addToBot(new DrawCardAction(2));
    }
}