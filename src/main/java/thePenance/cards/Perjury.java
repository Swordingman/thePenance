package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.curses.Regret;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.util.CardStats;

public class Perjury extends BaseCard {
    public static final String ID = makeID("Perjury");
    public Perjury() {
        super(ID, new CardStats(
                CardColor.COLORLESS, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF, 0
        ));
        setExhaust(true);
    }
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new MakeTempCardInHandAction(new Regret()));
        addToBot(new GainEnergyAction(2));
    }
}