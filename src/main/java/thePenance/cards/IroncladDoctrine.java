package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.IroncladDoctrinePower;
import thePenance.util.CardStats;

public class IroncladDoctrine extends BaseCard {
    public static final String ID = makeID("IroncladDoctrine");
    private static final int COST = 2;
    private static final int COST_BARRIER = 10;
    private static final int UPG_COST_BARRIER = -2; // 10 -> 8

    public IroncladDoctrine() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));
        setMagic(COST_BARRIER, UPG_COST_BARRIER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new IroncladDoctrinePower(p, magicNumber), magicNumber));
    }
}