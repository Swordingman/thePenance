package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.PunishmentForTransgressionPower;
import thePenance.util.CardStats;

public class PunishmentForTransgression extends BaseCard {
    public static final String ID = makeID("PunishmentForTransgression");
    private static final int COST = 1;
    private static final int AMOUNT = 1;
    private static final int UPG_AMOUNT = 1; // 1->2

    public PunishmentForTransgression() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));
        setMagic(AMOUNT, UPG_AMOUNT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new PunishmentForTransgressionPower(p, magicNumber), magicNumber));
    }
}