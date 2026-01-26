package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class ASip extends BaseCard {

    public static final String ID = makeID("ASip");

    private static final int COST = 1;
    private static final int TEMP_STR = 5;
    private static final int UPG_TEMP_STR = 2; // 2->3
    private static final int VULN = 1;

    public ASip() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.COMMON,
                CardTarget.SELF,
                COST
        ));

        setMagic(TEMP_STR, UPG_TEMP_STR); // 临时力量
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获得临时力量
        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, magicNumber), magicNumber));
        addToBot(new ApplyPowerAction(p, p, new LoseStrengthPower(p, magicNumber), magicNumber));

        // 获得易伤
        addToBot(new ApplyPowerAction(p, p, new VulnerablePower(p, VULN, false), VULN));

        AbstractCard c = this.makeStatEquivalentCopy();
        c.isEthereal = true;

        if (!c.rawDescription.contains("NL")) {
            if (Settings.language == Settings.GameLanguage.ZHS) {
                c.rawDescription = "虚无。 NL " + c.rawDescription;
            } else {
                c.rawDescription = "Ethereal. NL " + c.rawDescription;
            }
            c.initializeDescription();
        }

         addToBot(new MakeTempCardInHandAction(c));
    }
}