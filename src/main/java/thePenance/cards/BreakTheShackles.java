package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.JudgementPower;
import thePenance.powers.ThornAuraPower;
import thePenance.util.CardStats;

public class BreakTheShackles extends BaseCard {

    public static final String ID = makeID("BreakTheShackles");

    private static final int COST = 1;
    private static final int BASE_VAL = 3;
    private static final int PERCENT = 20;
    private static final int UPG_PERCENT = 10;

    public BreakTheShackles() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));
        setMagic(PERCENT, UPG_PERCENT);
    }

    private int calculateTotalAmount() {
        int total = BASE_VAL;
        AbstractPlayer p = AbstractDungeon.player;

        if (p != null && p.hasPower(JudgementPower.POWER_ID)) {
            int judgeAmt = p.getPower(JudgementPower.POWER_ID).amount;
            total += (int) (judgeAmt * (this.magicNumber / 100.0f));
        }
        return total;
    }

    @Override
    public void applyPowers() {
        super.applyPowers();

        int total = calculateTotalAmount();

        this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0] + total + cardStrings.EXTENDED_DESCRIPTION[1];

        this.initializeDescription();
    }

    @Override
    public void onMoveToDiscard() {
        this.rawDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int total = calculateTotalAmount();
        if (total > 0) {
            addToBot(new ApplyPowerAction(p, p, new ThornAuraPower(p, total), total));
        }
    }
}